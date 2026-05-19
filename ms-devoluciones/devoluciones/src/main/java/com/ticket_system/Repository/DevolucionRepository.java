package com.ticket_system.Repository;

import com.ticket_system.Model.Devolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DevolucionRepository extends JpaRepository<Devolucion, Long> {

    List<Devolucion> findByEstado(String estado);

    List<Devolucion> findByVentaId(Long ventaId);

    List<Devolucion> findByTicketId(Long ticketId);

    List<Devolucion> findByTipoDevolucion(String tipoDevolucion);
}