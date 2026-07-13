package com.ticket_system.validacion.Controller;

import com.ticket_system.validacion.Assembler.SesionValidacionAssembler;
import com.ticket_system.validacion.Assembler.ValidacionTicketAssembler;
import com.ticket_system.validacion.DTO.SesionDTO;
import com.ticket_system.validacion.DTO.ValidacionDTO;
import com.ticket_system.validacion.Model.SesionValidacion;
import com.ticket_system.validacion.Model.ValidacionTicket;
import com.ticket_system.validacion.Service.ValidacionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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

    @Autowired
    private SesionValidacionAssembler sesionValidacionAssembler;

    @Autowired
    private ValidacionTicketAssembler validacionTicketAssembler;

    @GetMapping("/sesiones")
    public ResponseEntity<CollectionModel<EntityModel<SesionValidacion>>> listarSesiones() {
        logger.info("[VALIDACION] GET /api/validacion/sesiones");
        List<SesionValidacion> sesiones = validacionService.listarSesiones();
        return ResponseEntity.ok(sesionValidacionAssembler.toCollectionModel(sesiones));
    }

    @GetMapping("/sesiones/{id}")
    public ResponseEntity<EntityModel<SesionValidacion>> obtenerSesion(@PathVariable Long id) {
        logger.info("[VALIDACION] GET /api/validacion/sesiones/{}", id);
        SesionValidacion sesion = validacionService.obtenerSesionPorId(id);
        return ResponseEntity.ok(sesionValidacionAssembler.toModel(sesion));
    }

    @GetMapping("/sesiones/evento/{eventoId}")
    public ResponseEntity<CollectionModel<EntityModel<SesionValidacion>>> listarPorEvento(@PathVariable Long eventoId) {
        logger.info("[VALIDACION] GET /api/validacion/sesiones/evento/{}", eventoId);
        List<SesionValidacion> sesiones = validacionService.listarSesionesPorEvento(eventoId);
        return ResponseEntity.ok(sesionValidacionAssembler.toCollectionModel(sesiones));
    }

    @PostMapping("/sesiones")
    public ResponseEntity<SesionValidacion> abrirSesion(@Valid @RequestBody SesionDTO dto) {
        logger.info("[VALIDACION] POST /api/validacion/sesiones - portero: {}", dto.getNombrePortero());
        return ResponseEntity.status(HttpStatus.CREATED).body(validacionService.abrirSesion(dto));
    }

    @PatchMapping("/sesiones/{id}/cerrar")
    public ResponseEntity<SesionValidacion> cerrarSesion(@PathVariable Long id) {
        logger.info("[VALIDACION] PATCH /api/validacion/sesiones/{}/cerrar", id);
        return ResponseEntity.ok(validacionService.cerrarSesion(id));
    }

    @PostMapping("/sesiones/{id}/escanear")
    public ResponseEntity<ValidacionTicket> escanear(@PathVariable Long id,
                                                      @Valid @RequestBody ValidacionDTO dto) {
        logger.info("[VALIDACION] POST /api/validacion/sesiones/{}/escanear - ticketId: {}", id, dto.getTicketId());
        return ResponseEntity.status(HttpStatus.CREATED).body(validacionService.escanearTicket(id, dto));
    }

    @GetMapping("/sesiones/{id}/validaciones")
    public ResponseEntity<CollectionModel<EntityModel<ValidacionTicket>>> obtenerValidaciones(@PathVariable Long id) {
        logger.info("[VALIDACION] GET /api/validacion/sesiones/{}/validaciones", id);
        List<ValidacionTicket> validaciones = validacionService.obtenerValidacionesPorSesion(id);
        return ResponseEntity.ok(validacionTicketAssembler.toCollectionModel(validaciones));
    }

    @GetMapping("/resultado/{resultado}")
    public ResponseEntity<CollectionModel<EntityModel<ValidacionTicket>>> listarPorResultado(@PathVariable String resultado) {
        logger.info("[VALIDACION] GET /api/validacion/resultado/{}", resultado);
        List<ValidacionTicket> validaciones = validacionService.listarPorResultado(resultado);
        return ResponseEntity.ok(validacionTicketAssembler.toCollectionModel(validaciones));
    }
}
