package com.ticket_system.ventas.Controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ticket_system.ventas.Assembler.VentaAssembler;
import com.ticket_system.ventas.DTO.VentaDTO;
import com.ticket_system.ventas.Model.Venta;
import com.ticket_system.ventas.Service.VentaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private static final Logger logger = LoggerFactory.getLogger(VentaController.class);

    private final VentaService ventaService;

    private final VentaAssembler ventaAssembler;

    VentaController(VentaService ventaService, VentaAssembler ventaAssembler) {
        this.ventaService = ventaService;
        this.ventaAssembler = ventaAssembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Venta>>> getAll() {
        logger.info("GET /api/ventas");
        List<Venta> ventas = ventaService.obtenerTodos();
        return ResponseEntity.ok(ventaAssembler.toCollectionModel(ventas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Venta>> getById(@PathVariable Long id) {
        Venta venta = ventaService.obtenerPorId(id);
        return ResponseEntity.ok(ventaAssembler.toModel(venta));
    }

    @GetMapping("/comprador/{compradorId}")
    public ResponseEntity<CollectionModel<EntityModel<Venta>>> getByComprador(@PathVariable Long compradorId) {
        List<Venta> ventas = ventaService.obtenerPorComprador(compradorId);
        return ResponseEntity.ok(ventaAssembler.toCollectionModel(ventas));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<CollectionModel<EntityModel<Venta>>> getByEvento(@PathVariable Long eventoId) {
        List<Venta> ventas = ventaService.obtenerPorEvento(eventoId);
        return ResponseEntity.ok(ventaAssembler.toCollectionModel(ventas));
    }

    @PostMapping
    public ResponseEntity<Venta> create(@Valid @RequestBody VentaDTO dto) {
        logger.info("POST /api/ventas");
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ventaService.crear(dto));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Venta> cancelar(@PathVariable Long id) {
        try {
            logger.info("PATCH /api/ventas/{}/cancelar", id);
            return ResponseEntity.ok(ventaService.cancelar(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            ventaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
