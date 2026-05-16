package com.ticket_system.streaming.Service;

import com.ticket_system.streaming.DTO.AccesoDTO;
import com.ticket_system.streaming.DTO.StreamingDTO;
import com.ticket_system.streaming.Exception.BusinessException;
import com.ticket_system.streaming.Exception.ResourceNotFoundException;
import com.ticket_system.streaming.Model.AccesoStreaming;
import com.ticket_system.streaming.Model.Streaming;
import com.ticket_system.streaming.Repository.AccesoStreamingRepository;
import com.ticket_system.streaming.Repository.StreamingRepository;
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
public class StreamingService {

    private static final Logger logger = LoggerFactory.getLogger(StreamingService.class);

    @Autowired
    private StreamingRepository streamingRepository;

    @Autowired
    private AccesoStreamingRepository accesoRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${ms.eventos.url}")
    private String msEventosUrl;

    @Value("${ms.tickets.url}")
    private String msTicketsUrl;

    // ─── CRUD STREAMING ───────────────────────────────────────────────────────

    public List<Streaming> listarTodos() {
        logger.info("[STREAMING] Listando todos los streamings");
        return streamingRepository.findAll();
    }

    public Streaming obtenerPorId(Long id) {
        logger.info("[STREAMING] Buscando streaming con id: {}", id);
        return streamingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Streaming no encontrado con id: " + id));
    }

    public List<Streaming> listarPorEstado(String estado) {
        logger.info("[STREAMING] Listando streamings con estado: {}", estado);
        return streamingRepository.findByEstado(estado);
    }

    public Streaming crear(StreamingDTO dto) {
        logger.info("[STREAMING] Creando streaming para evento id: {}", dto.getEventoId());

        // Regla de negocio: un evento solo puede tener un streaming
        streamingRepository.findByEventoId(dto.getEventoId()).ifPresent(s -> {
            throw new BusinessException("Ya existe un streaming para el evento id: " + dto.getEventoId());
        });

        // Regla de negocio: fecha de fin debe ser posterior a fecha de inicio
        if (!dto.getFechaFin().isAfter(dto.getFechaInicio())) {
            throw new BusinessException("La fecha de fin debe ser posterior a la fecha de inicio.");
        }

        // Comunicacion con ms-eventos para verificar que el evento es online
        try {
            webClientBuilder.build()
                    .get()
                    .uri(msEventosUrl + "/api/eventos/" + dto.getEventoId())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            logger.info("[STREAMING] Evento {} verificado en ms-eventos", dto.getEventoId());
        } catch (Exception e) {
            logger.warn("[STREAMING] No se pudo verificar el evento en ms-eventos: {}", e.getMessage());
        }

        Streaming streaming = new Streaming();
        streaming.setEventoId(dto.getEventoId());
        streaming.setNombreStream(dto.getNombreStream());
        streaming.setUrlStream(dto.getUrlStream());
        streaming.setFechaInicio(dto.getFechaInicio());
        streaming.setFechaFin(dto.getFechaFin());
        streaming.setCapacidadMaxima(dto.getCapacidadMaxima());
        streaming.setDescripcion(dto.getDescripcion());

        Streaming guardado = streamingRepository.save(streaming);
        logger.info("[STREAMING] Streaming creado con id: {}", guardado.getId());
        return guardado;
    }

    public Streaming actualizar(Long id, StreamingDTO dto) {
        logger.info("[STREAMING] Actualizando streaming con id: {}", id);
        Streaming streaming = obtenerPorId(id);

        // Regla de negocio: no editar streamings finalizados o cancelados
        if ("FINALIZADO".equals(streaming.getEstado()) || "CANCELADO".equals(streaming.getEstado())) {
            throw new BusinessException("No se puede editar un streaming en estado: " + streaming.getEstado());
        }

        if (!dto.getFechaFin().isAfter(dto.getFechaInicio())) {
            throw new BusinessException("La fecha de fin debe ser posterior a la fecha de inicio.");
        }

        streaming.setNombreStream(dto.getNombreStream());
        streaming.setUrlStream(dto.getUrlStream());
        streaming.setFechaInicio(dto.getFechaInicio());
        streaming.setFechaFin(dto.getFechaFin());
        streaming.setCapacidadMaxima(dto.getCapacidadMaxima());
        streaming.setDescripcion(dto.getDescripcion());

        Streaming actualizado = streamingRepository.save(streaming);
        logger.info("[STREAMING] Streaming actualizado con id: {}", actualizado.getId());
        return actualizado;
    }

    public Streaming iniciarStream(Long id) {
        logger.info("[STREAMING] Iniciando streaming id: {}", id);
        Streaming streaming = obtenerPorId(id);

        if (!"PROGRAMADO".equals(streaming.getEstado())) {
            throw new BusinessException("Solo se pueden iniciar streamings en estado PROGRAMADO. Estado actual: "
                    + streaming.getEstado());
        }

        streaming.setEstado("EN_VIVO");
        Streaming iniciado = streamingRepository.save(streaming);
        logger.info("[STREAMING] Streaming {} iniciado - EN VIVO", id);
        return iniciado;
    }

    public Streaming finalizarStream(Long id) {
        logger.info("[STREAMING] Finalizando streaming id: {}", id);
        Streaming streaming = obtenerPorId(id);

        if (!"EN_VIVO".equals(streaming.getEstado())) {
            throw new BusinessException("Solo se pueden finalizar streamings EN_VIVO. Estado actual: "
                    + streaming.getEstado());
        }

        streaming.setEstado("FINALIZADO");
        Streaming finalizado = streamingRepository.save(streaming);
        logger.info("[STREAMING] Streaming {} finalizado", id);
        return finalizado;
    }

    public Streaming cancelarStream(Long id) {
        logger.info("[STREAMING] Cancelando streaming id: {}", id);
        Streaming streaming = obtenerPorId(id);

        if ("FINALIZADO".equals(streaming.getEstado())) {
            throw new BusinessException("No se puede cancelar un streaming ya finalizado.");
        }

        streaming.setEstado("CANCELADO");
        Streaming cancelado = streamingRepository.save(streaming);
        logger.info("[STREAMING] Streaming {} cancelado", id);
        return cancelado;
    }

    // ─── CRUD ACCESOS ─────────────────────────────────────────────────────────

    public AccesoStreaming generarAcceso(Long streamingId, AccesoDTO dto) {
        logger.info("[STREAMING] Generando acceso al streaming id: {}", streamingId);
        Streaming streaming = obtenerPorId(streamingId);

        // Regla de negocio: solo se puede acceder a streamings PROGRAMADOS o EN_VIVO
        if ("FINALIZADO".equals(streaming.getEstado()) || "CANCELADO".equals(streaming.getEstado())) {
            throw new BusinessException("No se puede generar acceso a un streaming en estado: "
                    + streaming.getEstado());
        }

        // Regla de negocio: no duplicar acceso por el mismo ticket
        accesoRepository.findByTicketId(dto.getTicketId()).ifPresent(a -> {
            throw new BusinessException("Ya existe un acceso generado para el ticket id: " + dto.getTicketId());
        });

        // Regla de negocio: verificar capacidad disponible
        if (streaming.getCapacidadDisponible() <= 0) {
            throw new BusinessException("El streaming ha alcanzado su capacidad máxima.");
        }

        // Comunicacion con ms-tickets para verificar el ticket
        try {
            webClientBuilder.build()
                    .get()
                    .uri(msTicketsUrl + "/api/tickets/" + dto.getTicketId())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            logger.info("[STREAMING] Ticket {} verificado en ms-tickets", dto.getTicketId());
        } catch (Exception e) {
            logger.warn("[STREAMING] No se pudo verificar el ticket en ms-tickets: {}", e.getMessage());
        }

        AccesoStreaming acceso = new AccesoStreaming();
        acceso.setStreaming(streaming);
        acceso.setTicketId(dto.getTicketId());
        acceso.setNombreEspectador(dto.getNombreEspectador());
        acceso.setEmailEspectador(dto.getEmailEspectador());

        // Reducir capacidad disponible
        streaming.setCapacidadDisponible(streaming.getCapacidadDisponible() - 1);
        streamingRepository.save(streaming);

        AccesoStreaming guardado = accesoRepository.save(acceso);
        logger.info("[STREAMING] Acceso generado con codigo: {}", guardado.getCodigoAcceso());
        return guardado;
    }

    public AccesoStreaming validarAcceso(String codigoAcceso) {
        logger.info("[STREAMING] Validando acceso con codigo: {}", codigoAcceso);
        AccesoStreaming acceso = accesoRepository.findByCodigoAcceso(codigoAcceso)
                .orElseThrow(() -> new ResourceNotFoundException("Código de acceso no válido: " + codigoAcceso));

        if (!"ACTIVO".equals(acceso.getEstadoAcceso())) {
            throw new BusinessException("El código de acceso ya fue usado o está revocado. Estado: "
                    + acceso.getEstadoAcceso());
        }

        acceso.setEstadoAcceso("USADO");
        acceso.setUsadoEn(LocalDateTime.now());
        AccesoStreaming validado = accesoRepository.save(acceso);
        logger.info("[STREAMING] Acceso {} validado exitosamente", codigoAcceso);
        return validado;
    }

    public List<AccesoStreaming> obtenerAccesosPorStreaming(Long streamingId) {
        logger.info("[STREAMING] Obteniendo accesos del streaming id: {}", streamingId);
        obtenerPorId(streamingId);
        return accesoRepository.findByStreamingId(streamingId);
    }

    public AccesoStreaming revocarAcceso(Long accesoId) {
        logger.info("[STREAMING] Revocando acceso id: {}", accesoId);
        AccesoStreaming acceso = accesoRepository.findById(accesoId)
                .orElseThrow(() -> new ResourceNotFoundException("Acceso no encontrado con id: " + accesoId));

        if ("USADO".equals(acceso.getEstadoAcceso())) {
            throw new BusinessException("No se puede revocar un acceso ya usado.");
        }

        acceso.setEstadoAcceso("REVOCADO");

        // Devolver capacidad disponible al streaming
        Streaming streaming = acceso.getStreaming();
        streaming.setCapacidadDisponible(streaming.getCapacidadDisponible() + 1);
        streamingRepository.save(streaming);

        AccesoStreaming revocado = accesoRepository.save(acceso);
        logger.info("[STREAMING] Acceso {} revocado", accesoId);
        return revocado;
    }
}