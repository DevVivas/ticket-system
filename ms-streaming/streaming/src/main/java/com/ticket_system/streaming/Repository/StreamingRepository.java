package com.ticket_system.streaming.Repository;

import com.ticket_system.streaming.Model.Streaming;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StreamingRepository extends JpaRepository<Streaming, Long> {

    Optional<Streaming> findByEventoId(Long eventoId);

    List<Streaming> findByEstado(String estado);
}