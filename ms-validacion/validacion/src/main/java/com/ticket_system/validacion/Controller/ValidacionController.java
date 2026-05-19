package com.ticket_system.validacion.Controller;

import com.ticket_system.validacion.DTO.SesionDTO;
import com.ticket_system.validacion.DTO.ValidacionDTO;
import com.ticket_system.validacion.Model.SesionValidacion;
import com.ticket_system.validacion.Model.ValidacionTicket;
import com.ticket_system.validacion.Service.ValidacionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/validacion")
public class ValidacionController {

    private static final Logger logger = LoggerFactory.getLogger(ValidacionController.class);

    @Autowired
    private ValidacionService validacionService;

    // ─── ENDPOINTS SESIONES ───────────────────────────────────────────────────

    // GET /api/validacion/sesiones
    @GetMapping("/sesiones")
    public ResponseEntity<List<SesionValidacion>> listarSesiones() {
        logger.info("[VALIDACION] GET /api/validacion/sesiones");
        return ResponseEntity.ok(validacionService.listarSesiones());
    }

    // GET /api/validacion/sesiones/{id}
    @GetMapping("/sesiones/{id}")
    public ResponseEntity<SesionValidacion> obtenerSesion(@PathVariable Long id) {
        logger.info("[VALIDACION] GET /api/validacion/sesiones/{}", id);
        return ResponseEntity.ok(validacionService.obtenerSesionPorId(id));
    }

    // GET /api/validacion/sesiones/evento/{eventoId}
    @GetMapping("/sesiones/evento/{eventoId}")
    public ResponseEntity<List<SesionValidacion>> listarPorEvento(@PathVariable Long eventoId) {
        logger.info("[VALIDACION] GET /api/validacion/sesiones/evento/{}", eventoId);
        return ResponseEntity.ok(validacionService.listarSesionesPorEvento(eventoId));
    }

    // POST /api/validacion/sesiones
    @PostMapping("/sesiones")
    public ResponseEntity<SesionValidacion> abrirSesion(@Valid @RequestBody SesionDTO dto) {
        logger.info("[VALIDACION] POST /api/validacion/sesiones - portero: {}", dto.getNombrePortero());
        return ResponseEntity.status(HttpStatus.CREATED).body(validacionService.abrirSesion(dto));
    }

    // PATCH /api/validacion/sesiones/{id}/cerrar
    @PatchMapping("/sesiones/{id}/cerrar")
    public ResponseEntity<SesionValidacion> cerrarSesion(@PathVariable Long id) {
        logger.info("[VALIDACION] PATCH /api/validacion/sesiones/{}/cerrar", id);
        return ResponseEntity.ok(validacionService.cerrarSesion(id));
    }

    // ─── ENDPOINTS ESCANEO ────────────────────────────────────────────────────

    // POST /api/validacion/sesiones/{id}/escanear
    @PostMapping("/sesiones/{id}/escanear")
    public ResponseEntity<ValidacionTicket> escanear(@PathVariable Long id,
                                                      @Valid @RequestBody ValidacionDTO dto) {
        logger.info("[VALIDACION] POST /api/validacion/sesiones/{}/escanear - ticketId: {}", id, dto.getTicketId());
        return ResponseEntity.status(HttpStatus.CREATED).body(validacionService.escanearTicket(id, dto));
    }

    // GET /api/validacion/sesiones/{id}/validaciones
    @GetMapping("/sesiones/{id}/validaciones")
    public ResponseEntity<List<ValidacionTicket>> obtenerValidaciones(@PathVariable Long id) {
        logger.info("[VALIDACION] GET /api/validacion/sesiones/{}/validaciones", id);
        return ResponseEntity.ok(validacionService.obtenerValidacionesPorSesion(id));
    }

    // GET /api/validacion/resultado/{resultado}
    @GetMapping("/resultado/{resultado}")
    public ResponseEntity<List<ValidacionTicket>> listarPorResultado(@PathVariable String resultado) {
        logger.info("[VALIDACION] GET /api/validacion/resultado/{}", resultado);
        return ResponseEntity.ok(validacionService.listarPorResultado(resultado));
    }
}