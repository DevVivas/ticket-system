package com.ticket_system.recintos.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticket_system.recintos.Model.Recinto;

public interface RecintoRepository extends JpaRepository<Recinto, Long> {
    List<Recinto> findByNombreContaining(String nombre);
}
