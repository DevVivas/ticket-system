package com.ticket_system.Controller;

import com.ticket_system.Assembler.DevolucionAssembler;
import com.ticket_system.Assembler.ReembolsoDevolucionAssembler;
import com.ticket_system.DTO.DevolucionDTO;
import com.ticket_system.DTO.ReembolsoDTO;
import com.ticket_system.Model.Devolucion;
import com.ticket_system.Model.ReembolsoDevolucion;
import com.ticket_system.Service.DevolucionService;
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
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/devoluciones")
public class DevolucionController {

    private static final Logger logger = LoggerFactory.getLogger(DevolucionController.class);

    @Autowired
    private DevolucionService devolucionService;

    @Autowired
    private DevolucionAssembler devolucionAssembler;

    @Autowired
    private ReembolsoDevolucionAssembler reembolsoAssembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Devolucion>>> listarTodas() {
        logger.info("[DEVOLUCIONES] GET /api/devoluciones");
        List<Devolucion> devoluciones = devolucionService.listarTodas();
        CollectionModel<EntityModel<Devolucion>> model = devolucionAssembler.toCollectionModel(devoluciones);
        model.add(linkTo(methodOn(DevolucionController.class).listarTodas()).withSelfRel());
        return ResponseEntity.ok(model);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Devolucion>> obtenerPorId(@PathVariable Long id) {
        logger.info("[DEVOLUCIONES] GET /api/devoluciones/{}", id);
        Devolucion devolucion = devolucionService.obtenerPorId(id);
        return ResponseEntity.ok(devolucionAssembler.toModel(devolucion));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<CollectionModel<EntityModel<Devolucion>>> listarPorEstado(@PathVariable String estado) {
        logger.info("[DEVOLUCIONES] GET /api/devoluciones/estado/{}", estado);
        List<Devolucion> devoluciones = devolucionService.listarPorEstado(estado);
        CollectionModel<EntityModel<Devolucion>> model = devolucionAssembler.toCollectionModel(devoluciones);
        model.add(linkTo(methodOn(DevolucionController.class).listarPorEstado(estado)).withSelfRel());
        return ResponseEntity.ok(model);
    }

    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<CollectionModel<EntityModel<Devolucion>>> listarPorVenta(@PathVariable Long ventaId) {
        logger.info("[DEVOLUCIONES] GET /api/devoluciones/venta/{}", ventaId);
        List<Devolucion> devoluciones = devolucionService.listarPorVenta(ventaId);
        CollectionModel<EntityModel<Devolucion>> model = devolucionAssembler.toCollectionModel(devoluciones);
        model.add(linkTo(methodOn(DevolucionController.class).listarPorVenta(ventaId)).withSelfRel());
        return ResponseEntity.ok(model);
    }

    @PostMapping
    public ResponseEntity<Devolucion> crear(@Valid @RequestBody DevolucionDTO dto) {
        logger.info("[DEVOLUCIONES] POST /api/devoluciones - ventaId: {}", dto.getVentaId());
        return ResponseEntity.status(HttpStatus.CREATED).body(devolucionService.crear(dto));
    }

    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<Devolucion> aprobar(@PathVariable Long id) {
        logger.info("[DEVOLUCIONES] PATCH /api/devoluciones/{}/aprobar", id);
        return ResponseEntity.ok(devolucionService.aprobar(id));
    }

    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<Devolucion> rechazar(@PathVariable Long id,
                                                @RequestBody Map<String, String> body) {
        logger.info("[DEVOLUCIONES] PATCH /api/devoluciones/{}/rechazar", id);
        String motivo = body.getOrDefault("motivo", "Sin motivo especificado");
        return ResponseEntity.ok(devolucionService.rechazar(id, motivo));
    }

    @PatchMapping("/{id}/completar")
    public ResponseEntity<Devolucion> completar(@PathVariable Long id) {
        logger.info("[DEVOLUCIONES] PATCH /api/devoluciones/{}/completar", id);
        return ResponseEntity.ok(devolucionService.completar(id));
    }

    @GetMapping("/{id}/reembolsos")
    public ResponseEntity<CollectionModel<EntityModel<ReembolsoDevolucion>>> obtenerReembolsos(@PathVariable Long id) {
        logger.info("[DEVOLUCIONES] GET /api/devoluciones/{}/reembolsos", id);
        List<ReembolsoDevolucion> reembolsos = devolucionService.obtenerReembolsosPorDevolucion(id);
        CollectionModel<EntityModel<ReembolsoDevolucion>> model = reembolsoAssembler.toCollectionModel(reembolsos);
        model.add(linkTo(methodOn(DevolucionController.class).obtenerReembolsos(id)).withSelfRel());
        return ResponseEntity.ok(model);
    }

    @PostMapping("/{id}/reembolsos")
    public ResponseEntity<ReembolsoDevolucion> agregarReembolso(@PathVariable Long id,
                                                                 @Valid @RequestBody ReembolsoDTO dto) {
        logger.info("[DEVOLUCIONES] POST /api/devoluciones/{}/reembolsos", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(devolucionService.agregarReembolso(id, dto));
    }

    @PatchMapping("/reembolsos/{reembolsoId}/procesar")
    public ResponseEntity<ReembolsoDevolucion> procesarReembolso(@PathVariable Long reembolsoId) {
        logger.info("[DEVOLUCIONES] PATCH /api/devoluciones/reembolsos/{}/procesar", reembolsoId);
        return ResponseEntity.ok(devolucionService.procesarReembolso(reembolsoId));
    }
}
