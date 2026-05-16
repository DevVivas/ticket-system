package com.ticket_system.ventas.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.ticket_system.ventas.DTO.ItemVentaDTO;
import com.ticket_system.ventas.DTO.VentaDTO;
import com.ticket_system.ventas.Model.ItemVenta;
import com.ticket_system.ventas.Model.Venta;
import com.ticket_system.ventas.Repository.VentaRepository;

@Service
public class VentaService {

    private static final Logger logger = LoggerFactory.getLogger(VentaService.class);

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public List<Venta> obtenerTodos() {
        logger.info("Obteniendo todas las ventas");
        return ventaRepository.findAll();
    }

    public Optional<Venta> obtenerPorId(Long id) {
        logger.info("Buscando venta con id: {}", id);
        return ventaRepository.findById(id);
    }

    public List<Venta> obtenerPorComprador(Long compradorId) {
        logger.info("Obteniendo ventas del comprador: {}", compradorId);
        return ventaRepository.findByCompradorId(compradorId);
    }

    public List<Venta> obtenerPorEvento(Long eventoId) {
        logger.info("Obteniendo ventas del evento: {}", eventoId);
        return ventaRepository.findByEventoId(eventoId);
    }

    public Venta crear(VentaDTO dto) {
        logger.info("Creando venta para comprador: {}", dto.getCompradorId());

        Venta venta = new Venta();
        venta.setCompradorId(dto.getCompradorId());
        venta.setEventoId(dto.getEventoId());
        venta.setMetodoPago(dto.getMetodoPago());
        venta.setFechaVenta(LocalDateTime.now());
        venta.setEstado("COMPLETADA");

        // Mapear items y calcular monto total
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
        logger.info("Venta creada con id: {}", guardada.getId());

        // Comunicación con ms-tickets: marcar cada ticket como vendido
        for (ItemVenta item : items) {
            try {
                webClientBuilder.build()
                    .put()
                    .uri("http://localhost:8083/api/tickets/" + item.getTicketId() + "/vender")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                logger.info("Ticket {} marcado como vendido", item.getTicketId());
            } catch (Exception e) {
                logger.error("Error al marcar ticket {} como vendido: {}", item.getTicketId(), e.getMessage());
            }
        }

        return guardada;
    }

    public Venta cancelar(Long id) {
        logger.warn("Cancelando venta con id: {}", id);
        Venta venta = ventaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Venta no encontrada con id: " + id));

        // Regla de negocio: solo se puede cancelar si está COMPLETADA o PENDIENTE
        if ("RECHAZADA".equals(venta.getEstado())) {
            throw new RuntimeException("La venta ya fue rechazada anteriormente");
        }

        venta.setEstado("RECHAZADA");
        return ventaRepository.save(venta);
    }

    public void eliminar(Long id) {
        logger.info("Eliminando venta con id: {}", id);
        if (!ventaRepository.existsById(id)) {
            throw new RuntimeException("Venta no encontrada con id: " + id);
        }
        ventaRepository.deleteById(id);
    }
}
