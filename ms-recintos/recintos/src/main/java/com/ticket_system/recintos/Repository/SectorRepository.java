package com.ticket_system.recintos.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ticket_system.recintos.Model.Sector;

public interface SectorRepository extends JpaRepository<Sector, Long> {
    List<Sector> findByRecintoId(Long recintoId);
}
