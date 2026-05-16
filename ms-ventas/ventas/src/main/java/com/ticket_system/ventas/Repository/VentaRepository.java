package com.ticket_system.ventas.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticket_system.ventas.Model.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByCompradorId(Long compradorId);
    List<Venta> findByEventoId(Long eventoId);
    List<Venta> findByEstado(String estado);
}
