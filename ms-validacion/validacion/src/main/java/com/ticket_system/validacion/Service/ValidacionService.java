package com.ticket_system.validacion.Service;

import com.ticket_system.validacion.DTO.SesionDTO;
import com.ticket_system.validacion.DTO.ValidacionDTO;
import com.ticket_system.validacion.Exception.BusinessException;
import com.ticket_system.validacion.Exception.ResourceNotFoundException;
import com.ticket_system.validacion.Model.SesionValidacion;
import com.ticket_system.validacion.Model.ValidacionTicket;
import com.ticket_system.validacion.Repository.SesionValidacionRepository;
import com.ticket_system.validacion.Repository.ValidacionTicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ValidacionService {

    private static final Logger logger = LoggerFactory.getLogger(ValidacionService.class);

    @Autowired
    private SesionValidacionRepository sesionRepository;

    @Autowired
    private ValidacionTicketRepository validacionRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${ms.tickets.url}")
    private String msTicketsUrl;

    @Value("${ms.eventos.url}")
    private String msEventosUrl;

    // ─── CRUD SESIONES ────────────────────────────────────────────────────────

    public List<SesionValidacion> listarSesiones() {
        logger.info("[VALIDACION] Listando todas las sesiones");
        return sesionRepository.findAll();
    }

    public SesionValidacion obtenerSesionPorId(Long id) {
        logger.info("[VALIDACION] Buscando sesion con id: {}", id);
        return sesionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sesion no encontrada con id: " + id));
    }

    public List<SesionValidacion> listarSesionesPorEvento(Long eventoId) {
        logger.info("[VALIDACION] Listando sesiones del evento id: {}", eventoId);
        return sesionRepository.findByEventoId(eventoId);
    }

    public SesionValidacion abrirSesion(SesionDTO dto) {
        logger.info("[VALIDACION] Abriendo sesion para evento id: {} - portero: {}", dto.getEventoId(), dto.getNombrePortero());

        // Comunicacion con ms-eventos para verificar que el evento existe
        try {
            webClientBuilder.build()
                    .get()
                    .uri(msEventosUrl + "/api/eventos/" + dto.getEventoId())
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
            logger.info("[VALIDACION] Evento {} verificado en ms-eventos", dto.getEventoId());
        } catch (Exception e) {
            logger.warn("[VALIDACION] No se pudo verificar el evento en ms-eventos: {}", e.getMessage());
        }

        SesionValidacion sesion = new SesionValidacion();
        sesion.setEventoId(dto.getEventoId());
        sesion.setNombrePortero(dto.getNombrePortero());
        sesion.setPuestoAcceso(dto.getPuestoAcceso());

        SesionValidacion guardada = sesionRepository.save(sesion);
        logger.info("[VALIDACION] Sesion abierta con id: {}", guardada.getId());
        return guardada;
    }

    public SesionValidacion cerrarSesion(Long sesionId) {
        logger.info("[VALIDACION] Cerrando sesion id: {}", sesionId);
        SesionValidacion sesion = obtenerSesionPorId(sesionId);

        if ("CERRADA".equals(sesion.getEstado())) {
            throw new BusinessException("La sesion ya está cerrada.");
        }

        sesion.setEstado("CERRADA");
        sesion.setCerradaEn(LocalDateTime.now());
        SesionValidacion cerrada = sesionRepository.save(sesion);
        logger.info("[VALIDACION] Sesion {} cerrada - total escaneados: {}", sesionId, cerrada.getTotalEscaneados());
        return cerrada;
    }

    // ─── ESCANEO DE TICKETS ───────────────────────────────────────────────────

    public ValidacionTicket escanearTicket(Long sesionId, ValidacionDTO dto) {
        logger.info("[VALIDACION] Escaneando ticket id: {} en sesion id: {}", dto.getTicketId(), sesionId);
        SesionValidacion sesion = obtenerSesionPorId(sesionId);

        // Regla de negocio: solo se puede escanear en sesiones ACTIVAS
        if (!"ACTIVA".equals(sesion.getEstado())) {
            throw new BusinessException("No se puede escanear en una sesion cerrada.");
        }

        ValidacionTicket validacion = new ValidacionTicket();
        validacion.setSesion(sesion);
        validacion.setCodigoQR(dto.getCodigoQR());
        validacion.setTicketId(dto.getTicketId());

        // Verificar si el ticket ya fue escaneado como VALIDO anteriormente
        boolean yaUsado = validacionRepository
                .findByTicketIdAndResultado(dto.getTicketId(), "VALIDO")
                .isPresent();

        if (yaUsado) {
            validacion.setResultado("YA_USADO");
            validacion.setDetalleResultado("El ticket ya fue usado para ingresar al evento.");
            logger.warn("[VALIDACION] Ticket {} ya fue usado", dto.getTicketId());
        } else {
            // Comunicacion con ms-tickets para verificar el ticket
            try {
                Map<String, Object> ticketData = webClientBuilder.build()
                        .get()
                        .uri(msTicketsUrl + "/api/tickets/" + dto.getTicketId())
                        .retrieve()
                        .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                        .block();

                if (ticketData != null) {
                    validacion.setResultado("VALIDO");
                    validacion.setDetalleResultado("Ticket válido. Acceso permitido.");
                    logger.info("[VALIDACION] Ticket {} validado exitosamente", dto.getTicketId());
                } else {
                    validacion.setResultado("INVALIDO");
                    validacion.setDetalleResultado("El ticket no existe en el sistema.");
                    logger.warn("[VALIDACION] Ticket {} no encontrado", dto.getTicketId());
                }
            } catch (Exception e) {
                validacion.setResultado("INVALIDO");
                validacion.setDetalleResultado("No se pudo verificar el ticket: " + e.getMessage());
                logger.error("[VALIDACION] Error verificando ticket {}: {}", dto.getTicketId(), e.getMessage());
            }
        }

        // Incrementar contador de escaneados
        sesion.setTotalEscaneados(sesion.getTotalEscaneados() + 1);
        sesionRepository.save(sesion);

        ValidacionTicket guardada = validacionRepository.save(validacion);
        logger.info("[VALIDACION] Escaneo registrado - resultado: {}", guardada.getResultado());
        return guardada;
    }

    public List<ValidacionTicket> obtenerValidacionesPorSesion(Long sesionId) {
        logger.info("[VALIDACION] Obteniendo validaciones de sesion id: {}", sesionId);
        obtenerSesionPorId(sesionId);
        return validacionRepository.findBySesionId(sesionId);
    }

    public List<ValidacionTicket> listarPorResultado(String resultado) {
        logger.info("[VALIDACION] Listando validaciones con resultado: {}", resultado);
        return validacionRepository.findByResultado(resultado);
    }
}