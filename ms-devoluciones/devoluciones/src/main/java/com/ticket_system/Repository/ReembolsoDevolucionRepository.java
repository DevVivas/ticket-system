package com.ticket_system.Repository;

import com.ticket_system.Model.ReembolsoDevolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReembolsoDevolucionRepository extends JpaRepository<ReembolsoDevolucion, Long> {

    List<ReembolsoDevolucion> findByDevolucionId(Long devolucionId);

    List<ReembolsoDevolucion> findByEstadoReembolso(String estadoReembolso);

    List<ReembolsoDevolucion> findByMetodoReembolso(String metodoReembolso);
}