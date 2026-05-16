package com.ticket_system.ventas.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticket_system.ventas.Model.ItemVenta;

public interface ItemVentaRepository extends JpaRepository<ItemVenta, Long> {
    List<ItemVenta> findByVentaId(Long ventaId);
}
