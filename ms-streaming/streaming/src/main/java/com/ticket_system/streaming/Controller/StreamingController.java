package com.ticket_system.streaming.Controller;

import com.ticket_system.streaming.Assembler.AccesoStreamingAssembler;
import com.ticket_system.streaming.Assembler.StreamingAssembler;
import com.ticket_system.streaming.DTO.AccesoDTO;
import com.ticket_system.streaming.DTO.StreamingDTO;
import com.ticket_system.streaming.Model.AccesoStreaming;
import com.ticket_system.streaming.Model.Streaming;
import com.ticket_system.streaming.Service.StreamingService;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/streamings")
public class StreamingController {

    private static final Logger logger = LoggerFactory.getLogger(StreamingController.class);

    @Autowired
    private StreamingService streamingService;

    @Autowired
    private StreamingAssembler streamingAssembler;

    @Autowired
    private AccesoStreamingAssembler accesoAssembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Streaming>>> listarTodos() {
        logger.info("[STREAMING] GET /api/streamings");
        List<Streaming> streamings = streamingService.listarTodos();
        CollectionModel<EntityModel<Streaming>> model = streamingAssembler.toCollectionModel(streamings);
        model.add(linkTo(methodOn(StreamingController.class).listarTodos()).withSelfRel());
        return ResponseEntity.ok(model);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Streaming>> obtenerPorId(@PathVariable Long id) {
        logger.info("[STREAMING] GET /api/streamings/{}", id);
        Streaming streaming = streamingService.obtenerPorId(id);
        return ResponseEntity.ok(streamingAssembler.toModel(streaming));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<CollectionModel<EntityModel<Streaming>>> listarPorEstado(@PathVariable String estado) {
        logger.info("[STREAMING] GET /api/streamings/estado/{}", estado);
        List<Streaming> streamings = streamingService.listarPorEstado(estado);
        CollectionModel<EntityModel<Streaming>> model = streamingAssembler.toCollectionModel(streamings);
        model.add(linkTo(methodOn(StreamingController.class).listarPorEstado(estado)).withSelfRel());
        return ResponseEntity.ok(model);
    }

    @PostMapping
    public ResponseEntity<Streaming> crear(@Valid @RequestBody StreamingDTO dto) {
        logger.info("[STREAMING] POST /api/streamings - eventoId: {}", dto.getEventoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(streamingService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Streaming> actualizar(@PathVariable Long id,
                                                 @Valid @RequestBody StreamingDTO dto) {
        logger.info("[STREAMING] PUT /api/streamings/{}", id);
        return ResponseEntity.ok(streamingService.actualizar(id, dto));
    }

    @PatchMapping("/{id}/iniciar")
    public ResponseEntity<Streaming> iniciar(@PathVariable Long id) {
        logger.info("[STREAMING] PATCH /api/streamings/{}/iniciar", id);
        return ResponseEntity.ok(streamingService.iniciarStream(id));
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<Streaming> finalizar(@PathVariable Long id) {
        logger.info("[STREAMING] PATCH /api/streamings/{}/finalizar", id);
        return ResponseEntity.ok(streamingService.finalizarStream(id));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Streaming> cancelar(@PathVariable Long id) {
        logger.info("[STREAMING] PATCH /api/streamings/{}/cancelar", id);
        return ResponseEntity.ok(streamingService.cancelarStream(id));
    }

    @GetMapping("/{id}/accesos")
    public ResponseEntity<CollectionModel<EntityModel<AccesoStreaming>>> obtenerAccesos(@PathVariable Long id) {
        logger.info("[STREAMING] GET /api/streamings/{}/accesos", id);
        List<AccesoStreaming> accesos = streamingService.obtenerAccesosPorStreaming(id);
        CollectionModel<EntityModel<AccesoStreaming>> model = accesoAssembler.toCollectionModel(accesos);
        model.add(linkTo(methodOn(StreamingController.class).obtenerAccesos(id)).withSelfRel());
        return ResponseEntity.ok(model);
    }

    @PostMapping("/{id}/accesos")
    public ResponseEntity<AccesoStreaming> generarAcceso(@PathVariable Long id,
                                                          @Valid @RequestBody AccesoDTO dto) {
        logger.info("[STREAMING] POST /api/streamings/{}/accesos - ticketId: {}", id, dto.getTicketId());
        return ResponseEntity.status(HttpStatus.CREATED).body(streamingService.generarAcceso(id, dto));
    }

    @PatchMapping("/accesos/validar/{codigo}")
    public ResponseEntity<AccesoStreaming> validarAcceso(@PathVariable String codigo) {
        logger.info("[STREAMING] PATCH /api/streamings/accesos/validar/{}", codigo);
        return ResponseEntity.ok(streamingService.validarAcceso(codigo));
    }

    @PatchMapping("/accesos/{accesoId}/revocar")
    public ResponseEntity<AccesoStreaming> revocarAcceso(@PathVariable Long accesoId) {
        logger.info("[STREAMING] PATCH /api/streamings/accesos/{}/revocar", accesoId);
        return ResponseEntity.ok(streamingService.revocarAcceso(accesoId));
    }
}
