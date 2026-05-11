package com.ticket_system.Repository;

import com.ticket_system.Model.AgendaArtista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendaArtistaRepository extends JpaRepository<AgendaArtista, Long> {

    List<AgendaArtista> findByArtistaId(Long artistaId);

    List<AgendaArtista> findByEventoId(Long eventoId);

    List<AgendaArtista> findByEstadoAgenda(String estadoAgenda);

    List<AgendaArtista> findByFechaPresentacionBetween(LocalDateTime desde, LocalDateTime hasta);
}