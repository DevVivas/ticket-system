package com.ticket_system.validacion.Repository;

import com.ticket_system.validacion.Model.SesionValidacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SesionValidacionRepository extends JpaRepository<SesionValidacion, Long> {

    List<SesionValidacion> findByEventoId(Long eventoId);

    List<SesionValidacion> findByEstado(String estado);

    List<SesionValidacion> findByNombrePorteroIgnoreCase(String nombrePortero);
}