package com.ticket_system.recintos.Controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ticket_system.recintos.DTO.RecintoDTO;
import com.ticket_system.recintos.DTO.SectorDTO;
import com.ticket_system.recintos.Model.Recinto;
import com.ticket_system.recintos.Model.Sector;
import com.ticket_system.recintos.Service.RecintoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recintos")
public class RecintoController {

    private static final Logger logger = LoggerFactory.getLogger(RecintoController.class);

    @Autowired
    private RecintoService recintoService;

    @GetMapping
    public ResponseEntity<List<Recinto>> getAll() {
        logger.info("GET /api/recintos");
        return ResponseEntity.ok(recintoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recinto> getById(@PathVariable Long id) {
        return recintoService.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Recinto> create(@Valid @RequestBody RecintoDTO dto) {
        logger.info("POST /api/recintos");
        return ResponseEntity.status(HttpStatus.CREATED).body(recintoService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recinto> update(@PathVariable Long id, @Valid @RequestBody RecintoDTO dto) {
        try {
            return ResponseEntity.ok(recintoService.actualizar(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            recintoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/sectores")
    public ResponseEntity<Sector> addSector(
            @PathVariable Long id,
            @Valid @RequestBody SectorDTO dto) {
        try {
            logger.info("POST /api/recintos/{}/sectores", id);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(recintoService.agregarSector(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/sectores")
    public ResponseEntity<List<Sector>> getSectores(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(recintoService.obtenerSectores(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/sectores/{sectorId}")
    public ResponseEntity<Void> deleteSector(@PathVariable Long sectorId) {
        try {
            recintoService.eliminarSector(sectorId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}