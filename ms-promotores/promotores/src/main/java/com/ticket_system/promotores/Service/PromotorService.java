package com.ticket_system.promotores.Service;

import com.ticket_system.promotores.DTO.ComisionDTO;
import com.ticket_system.promotores.DTO.PromotorDTO;
import com.ticket_system.promotores.Exception.BusinessException;
import com.ticket_system.promotores.Exception.ResourceNotFoundException;
import com.ticket_system.promotores.Model.ComisionPromotor;
import com.ticket_system.promotores.Model.Promotor;
import com.ticket_system.promotores.Repository.ComisionPromotorRepository;
import com.ticket_system.promotores.Repository.PromotorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PromotorService {

    private static final Logger logger = LoggerFactory.getLogger(PromotorService.class);

    private final PromotorRepository promotorRepository;

    private final ComisionPromotorRepository comisionRepository;

    private final WebClient.Builder webClientBuilder;

    @Value("${ms.ventas.url}")
    private String msVentasUrl;

    PromotorService(PromotorRepository promotorRepository, ComisionPromotorRepository comisionRepository, WebClient.Builder webClientBuilder) {
        this.promotorRepository = promotorRepository;
        this.comisionRepository = comisionRepository;
        this.webClientBuilder = webClientBuilder;
    }

    // ─── CRUD PROMOTORES ──────────────────────────────────────────────────────

    public List<Promotor> listarTodos() {
        logger.info("[PROMOTORES] Listando todos los promotores");
        return promotorRepository.findAll();
    }

    public Promotor obtenerPorId(Long id) {
        logger.info("[PROMOTORES] Buscando promotor con id: {}", id);
        return promotorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotor no encontrado con id: " + id));
    }

    public List<Promotor> listarActivos() {
        logger.info("[PROMOTORES] Listando promotores activos");
        return promotorRepository.findByEstado("ACTIVO");
    }

    public Promotor crear(PromotorDTO dto) {
        logger.info("[PROMOTORES] Creando promotor: {}", dto.getNombre());

        // Regla de negocio: email unico
        promotorRepository.findByEmail(dto.getEmail()).ifPresent(p -> {
            throw new BusinessException("Ya existe un promotor con el email: " + dto.getEmail());
        });

        Promotor promotor = new Promotor();
        promotor.setNombre(dto.getNombre());
        promotor.setEmail(dto.getEmail());
        promotor.setTelefono(dto.getTelefono());
        promotor.setPorcentajeComision(dto.getPorcentajeComision());

        Promotor guardado = promotorRepository.save(promotor);
        logger.info("[PROMOTORES] Promotor creado con id: {}", guardado.getId());
        return guardado;
    }

    public Promotor actualizar(Long id, PromotorDTO dto) {
        logger.info("[PROMOTORES] Actualizando promotor con id: {}", id);
        Promotor promotor = obtenerPorId(id);

        // Regla de negocio: si cambia el email, verificar que no exista
        if (!promotor.getEmail().equals(dto.getEmail())) {
            promotorRepository.findByEmail(dto.getEmail()).ifPresent(p -> {
                throw new BusinessException("Ya existe un promotor con el email: " + dto.getEmail());
            });
        }

        promotor.setNombre(dto.getNombre());
        promotor.setEmail(dto.getEmail());
        promotor.setTelefono(dto.getTelefono());
        promotor.setPorcentajeComision(dto.getPorcentajeComision());

        Promotor actualizado = promotorRepository.save(promotor);
        logger.info("[PROMOTORES] Promotor actualizado con id: {}", actualizado.getId());
        return actualizado;
    }

    public void desactivar(Long id) {
        logger.info("[PROMOTORES] Desactivando promotor con id: {}", id);
        Promotor promotor = obtenerPorId(id);

        // Regla de negocio: no desactivar si tiene comisiones pendientes
        Double pendiente = comisionRepository.sumComisionesPendientesByPromotorId(id);
        if (pendiente != null && pendiente > 0) {
            throw new BusinessException("No se puede desactivar el promotor porque tiene $"
                    + pendiente + " en comisiones pendientes de pago.");
        }

        promotor.setEstado("INACTIVO");
        promotorRepository.save(promotor);
        logger.info("[PROMOTORES] Promotor desactivado con id: {}", id);
    }

    // ─── CRUD COMISIONES ──────────────────────────────────────────────────────

    public ComisionPromotor registrarComision(Long promotorId, ComisionDTO dto) {
        logger.info("[PROMOTORES] Registrando comision para promotor id: {}", promotorId);
        Promotor promotor = obtenerPorId(promotorId);

        // Regla de negocio: solo promotores activos generan comisiones
        if (!"ACTIVO".equals(promotor.getEstado())) {
            throw new BusinessException("No se puede registrar comisión para un promotor inactivo.");
        }

        // Regla de negocio: no duplicar comision por la misma venta
        boolean yaRegistrada = comisionRepository.findByVentaId(dto.getVentaId())
                .stream()
                .anyMatch(c -> c.getPromotor().getId().equals(promotorId));
        if (yaRegistrada) {
            throw new BusinessException("Ya existe una comisión registrada para la venta "
                    + dto.getVentaId() + " y este promotor.");
        }

        // Comunicacion con ms-ventas para verificar la venta
        try {
            webClientBuilder.build()
                    .get()
                    .uri(msVentasUrl + "/api/ventas/" + dto.getVentaId())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            logger.info("[PROMOTORES] Venta {} verificada en ms-ventas", dto.getVentaId());
        } catch (Exception e) {
            logger.warn("[PROMOTORES] No se pudo verificar la venta en ms-ventas: {}", e.getMessage());
        }

        // Calculo automatico de la comision
        double montoComision = dto.getMontoVenta() * (promotor.getPorcentajeComision() / 100);

        ComisionPromotor comision = new ComisionPromotor();
        comision.setPromotor(promotor);
        comision.setVentaId(dto.getVentaId());
        comision.setMontoVenta(dto.getMontoVenta());
        comision.setPorcentajeAplicado(promotor.getPorcentajeComision());
        comision.setMontoComision(montoComision);

        ComisionPromotor guardada = comisionRepository.save(comision);
        logger.info("[PROMOTORES] Comision registrada con id: {} - monto: {}", guardada.getId(), montoComision);
        return guardada;
    }

    public List<ComisionPromotor> obtenerComisionesPorPromotor(Long promotorId) {
        logger.info("[PROMOTORES] Obteniendo comisiones del promotor id: {}", promotorId);
        obtenerPorId(promotorId);
        return comisionRepository.findByPromotorId(promotorId);
    }

    public ComisionPromotor pagarComision(Long comisionId) {
        logger.info("[PROMOTORES] Pagando comision id: {}", comisionId);
        ComisionPromotor comision = comisionRepository.findById(comisionId)
                .orElseThrow(() -> new ResourceNotFoundException("Comisión no encontrada con id: " + comisionId));

        if (!"PENDIENTE".equals(comision.getEstadoComision())) {
            throw new BusinessException("Solo se pueden pagar comisiones en estado PENDIENTE. Estado actual: "
                    + comision.getEstadoComision());
        }

        comision.setEstadoComision("PAGADA");
        comision.setPagadoEn(LocalDateTime.now());
        ComisionPromotor pagada = comisionRepository.save(comision);
        logger.info("[PROMOTORES] Comision {} pagada exitosamente", comisionId);
        return pagada;
    }

    public ComisionPromotor anularComision(Long comisionId) {
        logger.info("[PROMOTORES] Anulando comision id: {}", comisionId);
        ComisionPromotor comision = comisionRepository.findById(comisionId)
                .orElseThrow(() -> new ResourceNotFoundException("Comisión no encontrada con id: " + comisionId));

        if ("PAGADA".equals(comision.getEstadoComision())) {
            throw new BusinessException("No se puede anular una comisión ya pagada.");
        }

        comision.setEstadoComision("ANULADA");
        ComisionPromotor anulada = comisionRepository.save(comision);
        logger.info("[PROMOTORES] Comision {} anulada", comisionId);
        return anulada;
    }

    public Double obtenerTotalPendientePorPromotor(Long promotorId) {
        logger.info("[PROMOTORES] Calculando total pendiente del promotor id: {}", promotorId);
        obtenerPorId(promotorId);
        Double total = comisionRepository.sumComisionesPendientesByPromotorId(promotorId);
        return total != null ? total : 0.0;
    }
}