package com.ticket_system.Service;

import com.ticket_system.DTO.DevolucionDTO;
import com.ticket_system.DTO.ReembolsoDTO;
import com.ticket_system.Exception.BusinessException;
import com.ticket_system.Exception.ResourceNotFoundException;
import com.ticket_system.Model.Devolucion;
import com.ticket_system.Model.ReembolsoDevolucion;
import com.ticket_system.Repository.DevolucionRepository;
import com.ticket_system.Repository.ReembolsoDevolucionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class DevolucionService {

    private static final Logger logger = LoggerFactory.getLogger(DevolucionService.class);

    private final DevolucionRepository devolucionRepository;

    private final ReembolsoDevolucionRepository reembolsoRepository;

    private final WebClient.Builder webClientBuilder;

    @Value("${ms.ventas.url}")
    private String msVentasUrl;

    @Value("${ms.tickets.url}")
    private String msTicketsUrl;

    DevolucionService(DevolucionRepository devolucionRepository, ReembolsoDevolucionRepository reembolsoRepository, WebClient.Builder webClientBuilder) {
        this.devolucionRepository = devolucionRepository;
        this.reembolsoRepository = reembolsoRepository;
        this.webClientBuilder = webClientBuilder;
    }

    // ─── CRUD DEVOLUCIONES ────────────────────────────────────────────────────

    public List<Devolucion> listarTodas() {
        logger.info("[DEVOLUCIONES] Listando todas las devoluciones");
        return devolucionRepository.findAll();
    }

    public Devolucion obtenerPorId(Long id) {
        logger.info("[DEVOLUCIONES] Buscando devolucion con id: {}", id);
        return devolucionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Devolución no encontrada con id: " + id));
    }

    public List<Devolucion> listarPorEstado(String estado) {
        logger.info("[DEVOLUCIONES] Listando devoluciones con estado: {}", estado);
        return devolucionRepository.findByEstado(estado);
    }

    public List<Devolucion> listarPorVenta(Long ventaId) {
        logger.info("[DEVOLUCIONES] Listando devoluciones de venta id: {}", ventaId);
        return devolucionRepository.findByVentaId(ventaId);
    }

    public Devolucion crear(DevolucionDTO dto) {
        logger.info("[DEVOLUCIONES] Creando devolucion para venta id: {}", dto.getVentaId());

        // Regla de negocio: no puede haber dos devoluciones PENDIENTES para el mismo ticket
        List<Devolucion> existentes = devolucionRepository.findByTicketId(dto.getTicketId());
        boolean tienePendiente = existentes.stream()
                .anyMatch(d -> "PENDIENTE".equals(d.getEstado()) || "APROBADA".equals(d.getEstado()));
        if (tienePendiente) {
            throw new BusinessException("Ya existe una devolución activa para el ticket id: " + dto.getTicketId());
        }

        // Comunicacion con ms-ventas para verificar que la venta existe
        try {
            webClientBuilder.build()
                    .get()
                    .uri(msVentasUrl + "/api/ventas/" + dto.getVentaId())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            logger.info("[DEVOLUCIONES] Venta {} verificada en ms-ventas", dto.getVentaId());
        } catch (Exception e) {
            logger.warn("[DEVOLUCIONES] No se pudo verificar la venta en ms-ventas: {}", e.getMessage());
        }

        Devolucion devolucion = new Devolucion();
        devolucion.setVentaId(dto.getVentaId());
        devolucion.setTicketId(dto.getTicketId());
        devolucion.setMotivo(dto.getMotivo());
        devolucion.setMontoDevolucion(dto.getMontoDevolucion());
        devolucion.setTipoDevolucion(dto.getTipoDevolucion());

        Devolucion guardada = devolucionRepository.save(devolucion);
        logger.info("[DEVOLUCIONES] Devolución creada con id: {}", guardada.getId());
        return guardada;
    }

    public Devolucion aprobar(Long id) {
        logger.info("[DEVOLUCIONES] Aprobando devolución id: {}", id);
        Devolucion devolucion = obtenerPorId(id);

        // Regla de negocio: solo se pueden aprobar devoluciones PENDIENTES
        if (!"PENDIENTE".equals(devolucion.getEstado())) {
            throw new BusinessException("Solo se pueden aprobar devoluciones en estado PENDIENTE. Estado actual: "
                    + devolucion.getEstado());
        }

        devolucion.setEstado("APROBADA");
        Devolucion aprobada = devolucionRepository.save(devolucion);
        logger.info("[DEVOLUCIONES] Devolución {} aprobada", id);
        return aprobada;
    }

    public Devolucion rechazar(Long id, String motivo) {
        logger.info("[DEVOLUCIONES] Rechazando devolución id: {}", id);
        Devolucion devolucion = obtenerPorId(id);

        // Regla de negocio: solo se pueden rechazar devoluciones PENDIENTES
        if (!"PENDIENTE".equals(devolucion.getEstado())) {
            throw new BusinessException("Solo se pueden rechazar devoluciones en estado PENDIENTE. Estado actual: "
                    + devolucion.getEstado());
        }

        devolucion.setEstado("RECHAZADA");
        devolucion.setMotivo(devolucion.getMotivo() + " | Rechazo: " + motivo);
        Devolucion rechazada = devolucionRepository.save(devolucion);
        logger.info("[DEVOLUCIONES] Devolución {} rechazada", id);
        return rechazada;
    }

    public Devolucion completar(Long id) {
        logger.info("[DEVOLUCIONES] Completando devolución id: {}", id);
        Devolucion devolucion = obtenerPorId(id);

        // Regla de negocio: solo se pueden completar devoluciones APROBADAS con reembolso procesado
        if (!"APROBADA".equals(devolucion.getEstado())) {
            throw new BusinessException("Solo se pueden completar devoluciones en estado APROBADA. Estado actual: "
                    + devolucion.getEstado());
        }

        boolean tieneReembolsoProcesado = reembolsoRepository.findByDevolucionId(id)
                .stream()
                .anyMatch(r -> "PROCESADO".equals(r.getEstadoReembolso()));
        if (!tieneReembolsoProcesado) {
            throw new BusinessException("La devolución no puede completarse sin un reembolso procesado.");
        }

        devolucion.setEstado("COMPLETADA");
        Devolucion completada = devolucionRepository.save(devolucion);
        logger.info("[DEVOLUCIONES] Devolución {} completada", id);
        return completada;
    }

    // ─── CRUD REEMBOLSOS ──────────────────────────────────────────────────────

    public ReembolsoDevolucion agregarReembolso(Long devolucionId, ReembolsoDTO dto) {
        logger.info("[DEVOLUCIONES] Agregando reembolso a devolución id: {}", devolucionId);
        Devolucion devolucion = obtenerPorId(devolucionId);

        // Regla de negocio: solo se puede reembolsar si la devolución está APROBADA
        if (!"APROBADA".equals(devolucion.getEstado())) {
            throw new BusinessException("Solo se puede reembolsar una devolución APROBADA. Estado actual: "
                    + devolucion.getEstado());
        }

        // Regla de negocio: el monto del reembolso no puede superar el monto de la devolución
        if (dto.getMontoReembolso() > devolucion.getMontoDevolucion()) {
            throw new BusinessException("El monto del reembolso (" + dto.getMontoReembolso()
                    + ") no puede superar el monto de la devolución (" + devolucion.getMontoDevolucion() + ").");
        }

        ReembolsoDevolucion reembolso = new ReembolsoDevolucion();
        reembolso.setDevolucion(devolucion);
        reembolso.setMontoReembolso(dto.getMontoReembolso());
        reembolso.setMetodoReembolso(dto.getMetodoReembolso());
        reembolso.setReferenciaBancaria(dto.getReferenciaBancaria());

        ReembolsoDevolucion guardado = reembolsoRepository.save(reembolso);
        logger.info("[DEVOLUCIONES] Reembolso creado con id: {}", guardado.getId());
        return guardado;
    }

    public ReembolsoDevolucion procesarReembolso(Long reembolsoId) {
        logger.info("[DEVOLUCIONES] Procesando reembolso id: {}", reembolsoId);
        ReembolsoDevolucion reembolso = reembolsoRepository.findById(reembolsoId)
                .orElseThrow(() -> new ResourceNotFoundException("Reembolso no encontrado con id: " + reembolsoId));

        if ("PROCESADO".equals(reembolso.getEstadoReembolso())) {
            throw new BusinessException("El reembolso ya fue procesado.");
        }

        reembolso.setEstadoReembolso("PROCESADO");
        reembolso.setProcesadoEn(LocalDateTime.now());
        ReembolsoDevolucion procesado = reembolsoRepository.save(reembolso);
        logger.info("[DEVOLUCIONES] Reembolso {} procesado exitosamente", reembolsoId);
        return procesado;
    }

    public List<ReembolsoDevolucion> obtenerReembolsosPorDevolucion(Long devolucionId) {
        logger.info("[DEVOLUCIONES] Obteniendo reembolsos de devolución id: {}", devolucionId);
        obtenerPorId(devolucionId); // valida que existe
        return reembolsoRepository.findByDevolucionId(devolucionId);
    }
}