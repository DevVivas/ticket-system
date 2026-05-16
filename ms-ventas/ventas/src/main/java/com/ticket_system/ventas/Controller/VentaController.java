package com.ticket_system.ventas.Controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.ticket_system.ventas.DTO.VentaDTO;
import com.ticket_system.ventas.Model.Venta;
import com.ticket_system.ventas.Service.VentaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private static final Logger logger = LoggerFactory.getLogger(VentaController.class);

    @Autowired
    private VentaService ventaService;

    @GetMapping
    public ResponseEntity<List<Venta>> getAll() {
        logger.info("GET /api/ventas");
        return ResponseEntity.ok(ventaService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> getById(@PathVariable Long id) {
        return ventaService.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/comprador/{compradorId}")
    public ResponseEntity<List<Venta>> getByComprador(@PathVariable Long compradorId) {
        return ResponseEntity.ok(ventaService.obtenerPorComprador(compradorId));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<Venta>> getByEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(ventaService.obtenerPorEvento(eventoId));
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
