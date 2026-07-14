package com.ticket_system.ventas.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.ticket_system.ventas.DTO.ItemVentaDTO;
import com.ticket_system.ventas.DTO.VentaDTO;
import com.ticket_system.ventas.Exception.BusinessException;
import com.ticket_system.ventas.Exception.ResourceNotFoundException;
import com.ticket_system.ventas.Model.ItemVenta;
import com.ticket_system.ventas.Model.Venta;
import com.ticket_system.ventas.Repository.VentaRepository;

@Service
public class VentaService {

    private static final Logger logger = LoggerFactory.getLogger(VentaService.class);

    private final VentaRepository ventaRepository;

    private final WebClient.Builder webClientBuilder;

    VentaService(VentaRepository ventaRepository, WebClient.Builder webClientBuilder) {
        this.ventaRepository = ventaRepository;
        this.webClientBuilder = webClientBuilder;
    }

    public List<Venta> obtenerTodos() {
        logger.info("[VENTAS] Obteniendo todas las ventas");
        return ventaRepository.findAll();
    }

    public Venta obtenerPorId(Long id) {
        logger.info("[VENTAS] Buscando venta con id: {}", id);
        return ventaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id: " + id));
    }

    public List<Venta> obtenerPorComprador(Long compradorId) {
        logger.info("[VENTAS] Obteniendo ventas del comprador: {}", compradorId);
        return ventaRepository.findByCompradorId(compradorId);
    }

    public List<Venta> obtenerPorEvento(Long eventoId) {
        logger.info("[VENTAS] Obteniendo ventas del evento: {}", eventoId);
        return ventaRepository.findByEventoId(eventoId);
    }

    public Venta crear(VentaDTO dto) {
        logger.info("[VENTAS] Creando venta para comprador: {}", dto.getCompradorId());

        // Verificar disponibilidad de tickets ANTES de crear la venta
        for (ItemVentaDTO itemDTO : dto.getItems()) {
            try {
                Map<String, Object> ticketData = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8083/api/tickets/" + itemDTO.getTicketId())
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

                if (ticketData != null) {
                    String estado = (String) ticketData.get("estado");
                    if (!"DISPONIBLE".equals(estado)) {
                        throw new BusinessException("El ticket " + itemDTO.getTicketId() +
                            " no está disponible. Estado actual: " + estado);
                    }
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                logger.warn("[VENTAS] No se pudo verificar el ticket {}: {}", itemDTO.getTicketId(), e.getMessage());
            }
        }

        Venta venta = new Venta();
        venta.setCompradorId(dto.getCompradorId());
        venta.setEventoId(dto.getEventoId());
        venta.setMetodoPago(dto.getMetodoPago());
        venta.setFechaVenta(LocalDateTime.now());
        venta.setEstado("COMPLETADA");

        List<ItemVenta> items = new ArrayList<>();
        double montoTotal = 0;

        for (ItemVentaDTO itemDTO : dto.getItems()) {
            ItemVenta item = new ItemVenta();
            item.setTicketId(itemDTO.getTicketId());
            item.setPrecioUnitario(itemDTO.getPrecioUnitario());
            item.setCantidad(itemDTO.getCantidad());
            item.setVenta(venta);
            items.add(item);
            montoTotal += itemDTO.getPrecioUnitario() * itemDTO.getCantidad();
        }

        venta.setItems(items);
        venta.setMontoTotal(montoTotal);

        Venta guardada = ventaRepository.save(venta);
        logger.info("[VENTAS] Venta creada con id: {}", guardada.getId());

        // Marcar cada ticket como vendido en ms-tickets
        for (ItemVenta item : items) {
            try {
                webClientBuilder.build()
                    .put()
                    .uri("http://localhost:8083/api/tickets/" + item.getTicketId() + "/vender")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                logger.info("[VENTAS] Ticket {} marcado como vendido", item.getTicketId());
            } catch (Exception e) {
                logger.error("[VENTAS] Error al marcar ticket {} como vendido: {}", item.getTicketId(), e.getMessage());
            }
        }

        return guardada;
    }

    public Venta cancelar(Long id) {
        logger.warn("[VENTAS] Cancelando venta con id: {}", id);
        Venta venta = obtenerPorId(id);
        if ("RECHAZADA".equals(venta.getEstado())) {
            throw new BusinessException("La venta ya fue rechazada anteriormente.");
        }
        venta.setEstado("RECHAZADA");
        Venta cancelada = ventaRepository.save(venta);
        logger.info("[VENTAS] Venta {} cancelada", id);
        return cancelada;
    }

    public void eliminar(Long id) {
        logger.info("[VENTAS] Eliminando venta con id: {}", id);
        obtenerPorId(id);
        ventaRepository.deleteById(id);
        logger.info("[VENTAS] Venta {} eliminada", id);
    }
}
