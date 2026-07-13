package com.ticketsystem.eventos.Controller;

import com.ticketsystem.eventos.Assembler.EventoAssembler;
import com.ticketsystem.eventos.DTO.EventoDTO;
import com.ticketsystem.eventos.Model.Evento;
import com.ticketsystem.eventos.Service.EventoService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private static final Logger logger = LoggerFactory.getLogger(EventoController.class);

    @Autowired
    private EventoService eventoService;

    @Autowired
    private EventoAssembler eventoAssembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Evento>>> getAll() {
        logger.info("GET /api/eventos");
        List<EntityModel<Evento>> eventos = eventoService.obtenerTodos().stream()
                .map(eventoAssembler::toModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(eventos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Evento>> getById(@PathVariable Long id) {
        Evento evento = eventoService.obtenerPorId(id);
        return ResponseEntity.ok(eventoAssembler.toModel(evento));
    }

    @PostMapping
    public ResponseEntity<Evento> create(@Valid @RequestBody EventoDTO dto) {
        logger.info("POST /api/eventos");
        Evento creado = eventoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> update(@PathVariable Long id, @Valid @RequestBody EventoDTO dto) {
        try {
            return ResponseEntity.ok(eventoService.actualizar(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            eventoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Evento> cancelar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(eventoService.cancelar(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
