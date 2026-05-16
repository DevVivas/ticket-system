package com.ticket_system.tickets.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticket_system.tickets.Model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByCodigoUnico(String codigoUnico);
    List<Ticket> findByEventoId(Long eventoId);
    List<Ticket> findByCompradorId(Long compradorId);
    List<Ticket> findByEstado(String estado);
}
