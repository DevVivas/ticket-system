package com.ticket_system.promotores.Controller;

import com.ticket_system.promotores.Assembler.ComisionPromotorAssembler;
import com.ticket_system.promotores.Assembler.PromotorAssembler;
import com.ticket_system.promotores.DTO.ComisionDTO;
import com.ticket_system.promotores.DTO.PromotorDTO;
import com.ticket_system.promotores.Model.ComisionPromotor;
import com.ticket_system.promotores.Model.Promotor;
import com.ticket_system.promotores.Service.PromotorService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/promotores")
public class PromotorController {

    private static final Logger logger = LoggerFactory.getLogger(PromotorController.class);

    private final PromotorService promotorService;

    private final PromotorAssembler promotorAssembler;

    private final ComisionPromotorAssembler comisionAssembler;

    PromotorController(PromotorService promotorService, PromotorAssembler promotorAssembler, ComisionPromotorAssembler comisionAssembler) {
        this.promotorService = promotorService;
        this.promotorAssembler = promotorAssembler;
        this.comisionAssembler = comisionAssembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Promotor>>> listarTodos() {
        logger.info("[PROMOTORES] GET /api/promotores");
        List<Promotor> promotores = promotorService.listarTodos();
        CollectionModel<EntityModel<Promotor>> model = promotorAssembler.toCollectionModel(promotores);
        model.add(linkTo(methodOn(PromotorController.class).listarTodos()).withSelfRel());
        return ResponseEntity.ok(model);
    }

    @GetMapping("/activos")
    public ResponseEntity<CollectionModel<EntityModel<Promotor>>> listarActivos() {
        logger.info("[PROMOTORES] GET /api/promotores/activos");
        List<Promotor> promotores = promotorService.listarActivos();
        CollectionModel<EntityModel<Promotor>> model = promotorAssembler.toCollectionModel(promotores);
        model.add(linkTo(methodOn(PromotorController.class).listarActivos()).withSelfRel());
        return ResponseEntity.ok(model);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Promotor>> obtenerPorId(@PathVariable Long id) {
        logger.info("[PROMOTORES] GET /api/promotores/{}", id);
        Promotor promotor = promotorService.obtenerPorId(id);
        return ResponseEntity.ok(promotorAssembler.toModel(promotor));
    }

    @PostMapping
    public ResponseEntity<Promotor> crear(@Valid @RequestBody PromotorDTO dto) {
        logger.info("[PROMOTORES] POST /api/promotores - nombre: {}", dto.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(promotorService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Promotor> actualizar(@PathVariable Long id,
                                                @Valid @RequestBody PromotorDTO dto) {
        logger.info("[PROMOTORES] PUT /api/promotores/{}", id);
        return ResponseEntity.ok(promotorService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        logger.info("[PROMOTORES] DELETE /api/promotores/{}", id);
        promotorService.desactivar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/comisiones")
    public ResponseEntity<CollectionModel<EntityModel<ComisionPromotor>>> obtenerComisiones(@PathVariable Long id) {
        logger.info("[PROMOTORES] GET /api/promotores/{}/comisiones", id);
        List<ComisionPromotor> comisiones = promotorService.obtenerComisionesPorPromotor(id);
        CollectionModel<EntityModel<ComisionPromotor>> model = comisionAssembler.toCollectionModel(comisiones);
        model.add(linkTo(methodOn(PromotorController.class).obtenerComisiones(id)).withSelfRel());
        return ResponseEntity.ok(model);
    }

    @GetMapping("/{id}/comisiones/pendiente")
    public ResponseEntity<EntityModel<Map<String, Object>>> obtenerTotalPendiente(@PathVariable Long id) {
        logger.info("[PROMOTORES] GET /api/promotores/{}/comisiones/pendiente", id);
        Double total = promotorService.obtenerTotalPendientePorPromotor(id);
        Map<String, Object> response = new HashMap<>();
        response.put("promotorId", id);
        response.put("totalPendiente", total);
        EntityModel<Map<String, Object>> model = EntityModel.of(response);
        model.add(linkTo(methodOn(PromotorController.class).obtenerTotalPendiente(id)).withSelfRel());
        model.add(linkTo(methodOn(PromotorController.class).obtenerPorId(id)).withRel("promotor"));
        return ResponseEntity.ok(model);
    }

    @PostMapping("/{id}/comisiones")
    public ResponseEntity<ComisionPromotor> registrarComision(@PathVariable Long id,
                                                               @Valid @RequestBody ComisionDTO dto) {
        logger.info("[PROMOTORES] POST /api/promotores/{}/comisiones - ventaId: {}", id, dto.getVentaId());
        return ResponseEntity.status(HttpStatus.CREATED).body(promotorService.registrarComision(id, dto));
    }

    @PatchMapping("/comisiones/{comisionId}/pagar")
    public ResponseEntity<ComisionPromotor> pagarComision(@PathVariable Long comisionId) {
        logger.info("[PROMOTORES] PATCH /api/promotores/comisiones/{}/pagar", comisionId);
        return ResponseEntity.ok(promotorService.pagarComision(comisionId));
    }

    @PatchMapping("/comisiones/{comisionId}/anular")
    public ResponseEntity<ComisionPromotor> anularComision(@PathVariable Long comisionId) {
        logger.info("[PROMOTORES] PATCH /api/promotores/comisiones/{}/anular", comisionId);
        return ResponseEntity.ok(promotorService.anularComision(comisionId));
    }
}
