package com.ticket_system.tickets.Service;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ticket_system.tickets.DTO.TicketDTO;
import com.ticket_system.tickets.Exception.BusinessException;
import com.ticket_system.tickets.Exception.ResourceNotFoundException;
import com.ticket_system.tickets.Model.Ticket;
import com.ticket_system.tickets.Repository.TicketRepository;

@Service
public class TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    private TicketRepository ticketRepository;

    public List<Ticket> obtenerTodos() {
        logger.info("[TICKETS] Obteniendo todos los tickets");
        return ticketRepository.findAll();
    }

    public Ticket obtenerPorId(Long id) {
        logger.info("[TICKETS] Buscando ticket con id: {}", id);
        return ticketRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con id: " + id));
    }

    public Ticket obtenerPorCodigo(String codigo) {
        logger.info("[TICKETS] Buscando ticket por código: {}", codigo);
        return ticketRepository.findByCodigoUnico(codigo)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con código: " + codigo));
    }

    public List<Ticket> obtenerPorEvento(Long eventoId) {
        logger.info("[TICKETS] Obteniendo tickets del evento: {}", eventoId);
        return ticketRepository.findByEventoId(eventoId);
    }

    public List<Ticket> obtenerPorComprador(Long compradorId) {
        logger.info("[TICKETS] Obteniendo tickets del comprador: {}", compradorId);
        return ticketRepository.findByCompradorId(compradorId);
    }

    public Ticket generarTicket(TicketDTO dto) {
        logger.info("[TICKETS] Generando ticket para evento: {}", dto.getEventoId());
        Ticket ticket = new Ticket();
        ticket.setCodigoUnico(UUID.randomUUID().toString());
        ticket.setEventoId(dto.getEventoId());
        ticket.setSectorId(dto.getSectorId());
        ticket.setCompradorId(dto.getCompradorId());
        ticket.setPrecio(dto.getPrecio());
        ticket.setEstado("DISPONIBLE");
        ticket.setCodigoQR("QR-" + UUID.randomUUID().toString());
        Ticket guardado = ticketRepository.save(ticket);
        logger.info("[TICKETS] Ticket generado con código: {}", guardado.getCodigoUnico());
        return guardado;
    }

    public Ticket marcarVendido(Long id) {
        logger.info("[TICKETS] Marcando ticket como vendido: {}", id);
        Ticket ticket = obtenerPorId(id);
        if (!"DISPONIBLE".equals(ticket.getEstado())) {
            throw new BusinessException("Solo se pueden vender tickets con estado DISPONIBLE. Estado actual: " + ticket.getEstado());
        }
        ticket.setEstado("VENDIDO");
        Ticket vendido = ticketRepository.save(ticket);
        logger.info("[TICKETS] Ticket {} marcado como VENDIDO", id);
        return vendido;
    }

    public Ticket marcarUsado(Long id) {
        logger.info("[TICKETS] Marcando ticket como usado: {}", id);
        Ticket ticket = obtenerPorId(id);
        if (!"VENDIDO".equals(ticket.getEstado())) {
            throw new BusinessException("Solo se pueden usar tickets con estado VENDIDO. Estado actual: " + ticket.getEstado());
        }
        ticket.setEstado("USADO");
        Ticket usado = ticketRepository.save(ticket);
        logger.info("[TICKETS] Ticket {} marcado como USADO", id);
        return usado;
    }

    public Ticket anular(Long id) {
        logger.warn("[TICKETS] Anulando ticket con id: {}", id);
        Ticket ticket = obtenerPorId(id);
        if ("USADO".equals(ticket.getEstado())) {
            throw new BusinessException("No se puede anular un ticket que ya fue usado.");
        }
        ticket.setEstado("ANULADO");
        Ticket anulado = ticketRepository.save(ticket);
        logger.info("[TICKETS] Ticket {} anulado", id);
        return anulado;
    }

    public void eliminar(Long id) {
        logger.info("[TICKETS] Eliminando ticket con id: {}", id);
        obtenerPorId(id);
        ticketRepository.deleteById(id);
        logger.info("[TICKETS] Ticket {} eliminado", id);
    }
}
