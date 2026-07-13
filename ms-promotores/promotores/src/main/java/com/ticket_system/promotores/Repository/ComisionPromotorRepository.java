package com.ticket_system.promotores.Repository;

import com.ticket_system.promotores.Model.ComisionPromotor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ComisionPromotorRepository extends JpaRepository<ComisionPromotor, Long> {

    List<ComisionPromotor> findByPromotorId(Long promotorId);

    List<ComisionPromotor> findByEstadoComision(String estadoComision);

    List<ComisionPromotor> findByVentaId(Long ventaId);

    @Query("SELECT SUM(c.montoComision) FROM ComisionPromotor c WHERE c.promotor.id = :promotorId AND c.estadoComision = 'PENDIENTE'")
    Double sumComisionesPendientesByPromotorId(Long promotorId);
}