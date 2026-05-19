package com.ticket_system.preventa.Controller;

import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ticket_system.preventa.DTO.CodigoBeneficioDTO;
import com.ticket_system.preventa.Model.CodigoBeneficio;
import com.ticket_system.preventa.Service.PreventaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/preventa")
public class PreventaController {

    private static final Logger logger = LoggerFactory.getLogger(PreventaController.class);

    @Autowired
    private PreventaService preventaService;

    @GetMapping
    public ResponseEntity<List<CodigoBeneficio>> getAll() {
        logger.info("GET /api/preventa");
        return ResponseEntity.ok(preventaService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CodigoBeneficio> getById(@PathVariable Long id) {
        return preventaService.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CodigoBeneficio> create(@Valid @RequestBody CodigoBeneficioDTO dto) {
        logger.info("POST /api/preventa");
        CodigoBeneficio creado = preventaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CodigoBeneficio> update(@PathVariable Long id, @Valid @RequestBody CodigoBeneficioDTO dto) {
        try {
            return ResponseEntity.ok(preventaService.actualizar(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            preventaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/validar")
    public ResponseEntity<Map<String, Object>> validar(@RequestParam String codigo) {
        try {
            logger.info("POST /api/preventa/validar - codigo: {}", codigo);
            return ResponseEntity.ok(preventaService.validarCodigo(codigo));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<CodigoBeneficio> desactivar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(preventaService.desactivar(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<CodigoBeneficio>> getByEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(preventaService.obtenerPorEvento(eventoId));
    }
}
