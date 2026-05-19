package com.ticket_system.Controller;

import com.ticket_system.DTO.DevolucionDTO;
import com.ticket_system.DTO.ReembolsoDTO;
import com.ticket_system.Model.Devolucion;
import com.ticket_system.Model.ReembolsoDevolucion;
import com.ticket_system.Service.DevolucionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/devoluciones")
public class DevolucionController {

    private static final Logger logger = LoggerFactory.getLogger(DevolucionController.class);

    @Autowired
    private DevolucionService devolucionService;

    // GET /api/devoluciones
    @GetMapping
    public ResponseEntity<List<Devolucion>> listarTodas() {
        logger.info("[DEVOLUCIONES] GET /api/devoluciones");
        return ResponseEntity.ok(devolucionService.listarTodas());
    }

    // GET /api/devoluciones/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Devolucion> obtenerPorId(@PathVariable Long id) {
        logger.info("[DEVOLUCIONES] GET /api/devoluciones/{}", id);
        return ResponseEntity.ok(devolucionService.obtenerPorId(id));
    }

    // GET /api/devoluciones/estado/{estado}
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Devolucion>> listarPorEstado(@PathVariable String estado) {
        logger.info("[DEVOLUCIONES] GET /api/devoluciones/estado/{}", estado);
        return ResponseEntity.ok(devolucionService.listarPorEstado(estado));
    }

    // GET /api/devoluciones/venta/{ventaId}
    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<List<Devolucion>> listarPorVenta(@PathVariable Long ventaId) {
        logger.info("[DEVOLUCIONES] GET /api/devoluciones/venta/{}", ventaId);
        return ResponseEntity.ok(devolucionService.listarPorVenta(ventaId));
    }

    // POST /api/devoluciones
    @PostMapping
    public ResponseEntity<Devolucion> crear(@Valid @RequestBody DevolucionDTO dto) {
        logger.info("[DEVOLUCIONES] POST /api/devoluciones - ventaId: {}", dto.getVentaId());
        return ResponseEntity.status(HttpStatus.CREATED).body(devolucionService.crear(dto));
    }

    // PATCH /api/devoluciones/{id}/aprobar
    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<Devolucion> aprobar(@PathVariable Long id) {
        logger.info("[DEVOLUCIONES] PATCH /api/devoluciones/{}/aprobar", id);
        return ResponseEntity.ok(devolucionService.aprobar(id));
    }

    // PATCH /api/devoluciones/{id}/rechazar
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<Devolucion> rechazar(@PathVariable Long id,
                                                @RequestBody Map<String, String> body) {
        logger.info("[DEVOLUCIONES] PATCH /api/devoluciones/{}/rechazar", id);
        String motivo = body.getOrDefault("motivo", "Sin motivo especificado");
        return ResponseEntity.ok(devolucionService.rechazar(id, motivo));
    }

    // PATCH /api/devoluciones/{id}/completar
    @PatchMapping("/{id}/completar")
    public ResponseEntity<Devolucion> completar(@PathVariable Long id) {
        logger.info("[DEVOLUCIONES] PATCH /api/devoluciones/{}/completar", id);
        return ResponseEntity.ok(devolucionService.completar(id));
    }

    // GET /api/devoluciones/{id}/reembolsos
    @GetMapping("/{id}/reembolsos")
    public ResponseEntity<List<ReembolsoDevolucion>> obtenerReembolsos(@PathVariable Long id) {
        logger.info("[DEVOLUCIONES] GET /api/devoluciones/{}/reembolsos", id);
        return ResponseEntity.ok(devolucionService.obtenerReembolsosPorDevolucion(id));
    }

    // POST /api/devoluciones/{id}/reembolsos
    @PostMapping("/{id}/reembolsos")
    public ResponseEntity<ReembolsoDevolucion> agregarReembolso(@PathVariable Long id,
                                                                 @Valid @RequestBody ReembolsoDTO dto) {
        logger.info("[DEVOLUCIONES] POST /api/devoluciones/{}/reembolsos", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(devolucionService.agregarReembolso(id, dto));
    }

    // PATCH /api/devoluciones/reembolsos/{reembolsoId}/procesar
    @PatchMapping("/reembolsos/{reembolsoId}/procesar")
    public ResponseEntity<ReembolsoDevolucion> procesarReembolso(@PathVariable Long reembolsoId) {
        logger.info("[DEVOLUCIONES] PATCH /api/devoluciones/reembolsos/{}/procesar", reembolsoId);
        return ResponseEntity.ok(devolucionService.procesarReembolso(reembolsoId));
    }
}