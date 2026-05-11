package com.ticketsystem.eventos.Repository;

import com.ticketsystem.eventos.Model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findByEstado(String estado);
    List<Evento> findByTipo(String tipo);
}
