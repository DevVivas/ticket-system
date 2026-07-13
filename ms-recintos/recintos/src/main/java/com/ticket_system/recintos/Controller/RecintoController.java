package com.ticket_system.recintos.Controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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

import com.ticket_system.recintos.Assembler.RecintoAssembler;
import com.ticket_system.recintos.Assembler.SectorAssembler;
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

    private final RecintoService recintoService;

    private final RecintoAssembler recintoAssembler;

    private final SectorAssembler sectorAssembler;

    RecintoController(RecintoService recintoService, RecintoAssembler recintoAssembler, SectorAssembler sectorAssembler) {
        this.recintoService = recintoService;
        this.recintoAssembler = recintoAssembler;
        this.sectorAssembler = sectorAssembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Recinto>>> getAll() {
        logger.info("GET /api/recintos");
        List<EntityModel<Recinto>> recintos = recintoService.obtenerTodos().stream()
                .map(recintoAssembler::toModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(recintos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Recinto>> getById(@PathVariable Long id) {
        Recinto recinto = recintoService.obtenerPorId(id);
        return ResponseEntity.ok(recintoAssembler.toModel(recinto));
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
    public ResponseEntity<CollectionModel<EntityModel<Sector>>> getSectores(@PathVariable Long id) {
        try {
            List<EntityModel<Sector>> sectores = recintoService.obtenerSectores(id).stream()
                    .map(sectorAssembler::toModel)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(CollectionModel.of(sectores));
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
