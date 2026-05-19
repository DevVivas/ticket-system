package com.ticket_system.preventa.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticket_system.preventa.Model.CodigoBeneficio;

public interface CodigoBeneficioRepository extends JpaRepository<CodigoBeneficio, Long> {
    Optional<CodigoBeneficio> findByCodigo(String codigo);
    List<CodigoBeneficio> findByEventoId(Long eventoId);
    List<CodigoBeneficio> findByActivo(boolean activo);
}