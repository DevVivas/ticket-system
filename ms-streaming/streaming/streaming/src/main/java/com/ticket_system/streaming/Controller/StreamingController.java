package com.ticket_system.streaming.Controller;

import com.ticket_system.streaming.DTO.AccesoDTO;
import com.ticket_system.streaming.DTO.StreamingDTO;
import com.ticket_system.streaming.Model.AccesoStreaming;
import com.ticket_system.streaming.Model.Streaming;
import com.ticket_system.streaming.Service.StreamingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/streamings")
public class StreamingController {

    private static final Logger logger = LoggerFactory.getLogger(StreamingController.class);

    @Autowired
    private StreamingService streamingService;

    // ─── ENDPOINTS STREAMING ──────────────────────────────────────────────────

    // GET /api/streamings
    @GetMapping
    public ResponseEntity<List<Streaming>> listarTodos() {
        logger.info("[STREAMING] GET /api/streamings");
        return ResponseEntity.ok(streamingService.listarTodos());
    }

    // GET /api/streamings/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Streaming> obtenerPorId(@PathVariable Long id) {
        logger.info("[STREAMING] GET /api/streamings/{}", id);
        return ResponseEntity.ok(streamingService.obtenerPorId(id));
    }

    // GET /api/streamings/estado/{estado}
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Streaming>> listarPorEstado(@PathVariable String estado) {
        logger.info("[STREAMING] GET /api/streamings/estado/{}", estado);
        return ResponseEntity.ok(streamingService.listarPorEstado(estado));
    }

    // POST /api/streamings
    @PostMapping
    public ResponseEntity<Streaming> crear(@Valid @RequestBody StreamingDTO dto) {
        logger.info("[STREAMING] POST /api/streamings - eventoId: {}", dto.getEventoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(streamingService.crear(dto));
    }

    // PUT /api/streamings/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Streaming> actualizar(@PathVariable Long id,
                                                 @Valid @RequestBody StreamingDTO dto) {
        logger.info("[STREAMING] PUT /api/streamings/{}", id);
        return ResponseEntity.ok(streamingService.actualizar(id, dto));
    }

    // PATCH /api/streamings/{id}/iniciar
    @PatchMapping("/{id}/iniciar")
    public ResponseEntity<Streaming> iniciar(@PathVariable Long id) {
        logger.info("[STREAMING] PATCH /api/streamings/{}/iniciar", id);
        return ResponseEntity.ok(streamingService.iniciarStream(id));
    }

    // PATCH /api/streamings/{id}/finalizar
    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<Streaming> finalizar(@PathVariable Long id) {
        logger.info("[STREAMING] PATCH /api/streamings/{}/finalizar", id);
        return ResponseEntity.ok(streamingService.finalizarStream(id));
    }

    // PATCH /api/streamings/{id}/cancelar
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Streaming> cancelar(@PathVariable Long id) {
        logger.info("[STREAMING] PATCH /api/streamings/{}/cancelar", id);
        return ResponseEntity.ok(streamingService.cancelarStream(id));
    }

    // ─── ENDPOINTS ACCESOS ────────────────────────────────────────────────────

    // GET /api/streamings/{id}/accesos
    @GetMapping("/{id}/accesos")
    public ResponseEntity<List<AccesoStreaming>> obtenerAccesos(@PathVariable Long id) {
        logger.info("[STREAMING] GET /api/streamings/{}/accesos", id);
        return ResponseEntity.ok(streamingService.obtenerAccesosPorStreaming(id));
    }

    // POST /api/streamings/{id}/accesos
    @PostMapping("/{id}/accesos")
    public ResponseEntity<AccesoStreaming> generarAcceso(@PathVariable Long id,
                                                          @Valid @RequestBody AccesoDTO dto) {
        logger.info("[STREAMING] POST /api/streamings/{}/accesos - ticketId: {}", id, dto.getTicketId());
        return ResponseEntity.status(HttpStatus.CREATED).body(streamingService.generarAcceso(id, dto));
    }

    // PATCH /api/streamings/accesos/validar/{codigo}
    @PatchMapping("/accesos/validar/{codigo}")
    public ResponseEntity<AccesoStreaming> validarAcceso(@PathVariable String codigo) {
        logger.info("[STREAMING] PATCH /api/streamings/accesos/validar/{}", codigo);
        return ResponseEntity.ok(streamingService.validarAcceso(codigo));
    }

    // PATCH /api/streamings/accesos/{accesoId}/revocar
    @PatchMapping("/accesos/{accesoId}/revocar")
    public ResponseEntity<AccesoStreaming> revocarAcceso(@PathVariable Long accesoId) {
        logger.info("[STREAMING] PATCH /api/streamings/accesos/{}/revocar", accesoId);
        return ResponseEntity.ok(streamingService.revocarAcceso(accesoId));
    }
}