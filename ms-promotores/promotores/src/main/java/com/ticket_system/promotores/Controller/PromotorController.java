package com.ticket_system.promotores.Controller;

import com.ticket_system.promotores.DTO.ComisionDTO;
import com.ticket_system.promotores.DTO.PromotorDTO;
import com.ticket_system.promotores.Model.ComisionPromotor;
import com.ticket_system.promotores.Model.Promotor;
import com.ticket_system.promotores.Service.PromotorService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/promotores")
public class PromotorController {

    private static final Logger logger = LoggerFactory.getLogger(PromotorController.class);

    @Autowired
    private PromotorService promotorService;

    // GET /api/promotores
    @GetMapping
    public ResponseEntity<List<Promotor>> listarTodos() {
        logger.info("[PROMOTORES] GET /api/promotores");
        return ResponseEntity.ok(promotorService.listarTodos());
    }

    // GET /api/promotores/activos
    @GetMapping("/activos")
    public ResponseEntity<List<Promotor>> listarActivos() {
        logger.info("[PROMOTORES] GET /api/promotores/activos");
        return ResponseEntity.ok(promotorService.listarActivos());
    }

    // GET /api/promotores/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Promotor> obtenerPorId(@PathVariable Long id) {
        logger.info("[PROMOTORES] GET /api/promotores/{}", id);
        return ResponseEntity.ok(promotorService.obtenerPorId(id));
    }

    // POST /api/promotores
    @PostMapping
    public ResponseEntity<Promotor> crear(@Valid @RequestBody PromotorDTO dto) {
        logger.info("[PROMOTORES] POST /api/promotores - nombre: {}", dto.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(promotorService.crear(dto));
    }

    // PUT /api/promotores/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Promotor> actualizar(@PathVariable Long id,
                                                @Valid @RequestBody PromotorDTO dto) {
        logger.info("[PROMOTORES] PUT /api/promotores/{}", id);
        return ResponseEntity.ok(promotorService.actualizar(id, dto));
    }

    // DELETE /api/promotores/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        logger.info("[PROMOTORES] DELETE /api/promotores/{}", id);
        promotorService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/promotores/{id}/comisiones
    @GetMapping("/{id}/comisiones")
    public ResponseEntity<List<ComisionPromotor>> obtenerComisiones(@PathVariable Long id) {
        logger.info("[PROMOTORES] GET /api/promotores/{}/comisiones", id);
        return ResponseEntity.ok(promotorService.obtenerComisionesPorPromotor(id));
    }

    // GET /api/promotores/{id}/comisiones/pendiente
    @GetMapping("/{id}/comisiones/pendiente")
    public ResponseEntity<Double> obtenerTotalPendiente(@PathVariable Long id) {
        logger.info("[PROMOTORES] GET /api/promotores/{}/comisiones/pendiente", id);
        return ResponseEntity.ok(promotorService.obtenerTotalPendientePorPromotor(id));
    }

    // POST /api/promotores/{id}/comisiones
    @PostMapping("/{id}/comisiones")
    public ResponseEntity<ComisionPromotor> registrarComision(@PathVariable Long id,
                                                               @Valid @RequestBody ComisionDTO dto) {
        logger.info("[PROMOTORES] POST /api/promotores/{}/comisiones - ventaId: {}", id, dto.getVentaId());
        return ResponseEntity.status(HttpStatus.CREATED).body(promotorService.registrarComision(id, dto));
    }

    // PATCH /api/promotores/comisiones/{comisionId}/pagar
    @PatchMapping("/comisiones/{comisionId}/pagar")
    public ResponseEntity<ComisionPromotor> pagarComision(@PathVariable Long comisionId) {
        logger.info("[PROMOTORES] PATCH /api/promotores/comisiones/{}/pagar", comisionId);
        return ResponseEntity.ok(promotorService.pagarComision(comisionId));
    }

    // PATCH /api/promotores/comisiones/{comisionId}/anular
    @PatchMapping("/comisiones/{comisionId}/anular")
    public ResponseEntity<ComisionPromotor> anularComision(@PathVariable Long comisionId) {
        logger.info("[PROMOTORES] PATCH /api/promotores/comisiones/{}/anular", comisionId);
        return ResponseEntity.ok(promotorService.anularComision(comisionId));
    }
}