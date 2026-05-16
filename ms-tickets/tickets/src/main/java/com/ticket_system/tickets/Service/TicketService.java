package com.ticket_system.tickets.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ticket_system.tickets.DTO.TicketDTO;
import com.ticket_system.tickets.Model.Ticket;
import com.ticket_system.tickets.Repository.TicketRepository;

@Service
public class TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    private TicketRepository ticketRepository;

    public List<Ticket> obtenerTodos() {
        logger.info("Obteniendo todos los tickets");
        return ticketRepository.findAll();
    }

    public Optional<Ticket> obtenerPorId(Long id) {
        logger.info("Buscando ticket con id: {}", id);
        return ticketRepository.findById(id);
    }

    public Optional<Ticket> obtenerPorCodigo(String codigo) {
        logger.info("Buscando ticket por código: {}", codigo);
        return ticketRepository.findByCodigoUnico(codigo);
    }

    public List<Ticket> obtenerPorEvento(Long eventoId) {
        logger.info("Obteniendo tickets del evento: {}", eventoId);
        return ticketRepository.findByEventoId(eventoId);
    }

    public List<Ticket> obtenerPorComprador(Long compradorId) {
        logger.info("Obteniendo tickets del comprador: {}", compradorId);
        return ticketRepository.findByCompradorId(compradorId);
    }

    public Ticket generarTicket(TicketDTO dto) {
        logger.info("Generando ticket para evento: {}", dto.getEventoId());
        Ticket ticket = new Ticket();
        ticket.setCodigoUnico(UUID.randomUUID().toString());
        ticket.setEventoId(dto.getEventoId());
        ticket.setSectorId(dto.getSectorId());
        ticket.setCompradorId(dto.getCompradorId());
        ticket.setPrecio(dto.getPrecio());
        ticket.setEstado("DISPONIBLE");
        ticket.setCodigoQR("QR-" + UUID.randomUUID().toString());
        Ticket guardado = ticketRepository.save(ticket);
        logger.info("Ticket generado con código: {}", guardado.getCodigoUnico());
        return guardado;
    }

    public Ticket marcarVendido(Long id) {
        logger.info("Marcando ticket como vendido: {}", id);
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket no encontrado con id: " + id));
        // Regla de negocio: solo se puede vender si está DISPONIBLE
        if (!"DISPONIBLE".equals(ticket.getEstado())) {
            throw new RuntimeException("Solo se pueden vender tickets con estado DISPONIBLE");
        }
        ticket.setEstado("VENDIDO");
        return ticketRepository.save(ticket);
    }

    public Ticket marcarUsado(Long id) {
        logger.info("Marcando ticket como usado: {}", id);
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket no encontrado con id: " + id));
        // Regla de negocio: solo se puede usar si está VENDIDO
        if (!"VENDIDO".equals(ticket.getEstado())) {
            throw new RuntimeException("Solo se pueden usar tickets con estado VENDIDO");
        }
        ticket.setEstado("USADO");
        return ticketRepository.save(ticket);
    }

    public Ticket anular(Long id) {
        logger.warn("Anulando ticket con id: {}", id);
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ticket no encontrado con id: " + id));
        // Regla de negocio: no se puede anular un ticket ya usado
        if ("USADO".equals(ticket.getEstado())) {
            throw new RuntimeException("No se puede anular un ticket que ya fue usado");
        }
        ticket.setEstado("ANULADO");
        return ticketRepository.save(ticket);
    }

    public void eliminar(Long id) {
        logger.info("Eliminando ticket con id: {}", id);
        if (!ticketRepository.existsById(id)) {
            throw new RuntimeException("Ticket no encontrado con id: " + id);
        }
        ticketRepository.deleteById(id);
    }
}
