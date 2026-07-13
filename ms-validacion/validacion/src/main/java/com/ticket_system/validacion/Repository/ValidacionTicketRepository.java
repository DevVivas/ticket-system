package com.ticket_system.validacion.Repository;

import com.ticket_system.validacion.Model.ValidacionTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ValidacionTicketRepository extends JpaRepository<ValidacionTicket, Long> {

    List<ValidacionTicket> findBySesionId(Long sesionId);

    Optional<ValidacionTicket> findByTicketIdAndResultado(Long ticketId, String resultado);

    List<ValidacionTicket> findByResultado(String resultado);

    List<ValidacionTicket> findByCodigoQR(String codigoQR);
}