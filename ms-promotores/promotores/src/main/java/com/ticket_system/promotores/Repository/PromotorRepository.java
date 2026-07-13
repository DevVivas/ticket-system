package com.ticket_system.promotores.Repository;

import com.ticket_system.promotores.Model.Promotor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PromotorRepository extends JpaRepository<Promotor, Long> {

    List<Promotor> findByEstado(String estado);

    Optional<Promotor> findByEmail(String email);

    List<Promotor> findByNombreContainingIgnoreCase(String nombre);
}