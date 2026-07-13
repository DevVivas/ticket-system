package com.ticket_system.streaming.Repository;

import com.ticket_system.streaming.Model.AccesoStreaming;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AccesoStreamingRepository extends JpaRepository<AccesoStreaming, Long> {

    List<AccesoStreaming> findByStreamingId(Long streamingId);

    Optional<AccesoStreaming> findByCodigoAcceso(String codigoAcceso);

    Optional<AccesoStreaming> findByTicketId(Long ticketId);

    List<AccesoStreaming> findByEstadoAcceso(String estadoAcceso);
}