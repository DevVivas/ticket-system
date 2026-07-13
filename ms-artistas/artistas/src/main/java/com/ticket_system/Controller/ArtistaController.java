package com.ticket_system.Controller;

import com.ticket_system.Assembler.AgendaArtistaAssembler;
import com.ticket_system.Assembler.ArtistaAssembler;
import com.ticket_system.DTO.AgendaDTO;
import com.ticket_system.DTO.ArtistaDTO;
import com.ticket_system.Model.AgendaArtista;
import com.ticket_system.Model.Artista;
import com.ticket_system.Service.ArtistaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/artistas")
public class ArtistaController {

    private static final Logger logger = LoggerFactory.getLogger(ArtistaController.class);

    private final ArtistaService artistaService;

    private final ArtistaAssembler artistaAssembler;

    private final AgendaArtistaAssembler agendaArtistaAssembler;

    ArtistaController(ArtistaService artistaService, ArtistaAssembler artistaAssembler, AgendaArtistaAssembler agendaArtistaAssembler) {
        this.artistaService = artistaService;
        this.artistaAssembler = artistaAssembler;
        this.agendaArtistaAssembler = agendaArtistaAssembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Artista>>> listarTodos() {
        logger.info("[ARTISTAS] GET /api/artistas");
        List<Artista> artistas = artistaService.listarTodos();
        return ResponseEntity.ok(artistaAssembler.toCollectionModel(artistas));
    }

    @GetMapping("/activos")
    public ResponseEntity<CollectionModel<EntityModel<Artista>>> listarActivos() {
        logger.info("[ARTISTAS] GET /api/artistas/activos");
        List<Artista> artistas = artistaService.listarActivos();
        return ResponseEntity.ok(artistaAssembler.toCollectionModel(artistas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Artista>> obtenerPorId(@PathVariable Long id) {
        logger.info("[ARTISTAS] GET /api/artistas/{}", id);
        Artista artista = artistaService.obtenerPorId(id);
        return ResponseEntity.ok(artistaAssembler.toModel(artista));
    }

    @GetMapping("/genero/{genero}")
    public ResponseEntity<CollectionModel<EntityModel<Artista>>> listarPorGenero(@PathVariable String genero) {
        logger.info("[ARTISTAS] GET /api/artistas/genero/{}", genero);
        List<Artista> artistas = artistaService.listarPorGenero(genero);
        return ResponseEntity.ok(artistaAssembler.toCollectionModel(artistas));
    }

    @PostMapping
    public ResponseEntity<Artista> crear(@Valid @RequestBody ArtistaDTO dto) {
        logger.info("[ARTISTAS] POST /api/artistas - nombre: {}", dto.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(artistaService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Artista> actualizar(@PathVariable Long id,
                                               @Valid @RequestBody ArtistaDTO dto) {
        logger.info("[ARTISTAS] PUT /api/artistas/{}", id);
        return ResponseEntity.ok(artistaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        logger.info("[ARTISTAS] DELETE /api/artistas/{}", id);
        artistaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/agenda")
    public ResponseEntity<CollectionModel<EntityModel<AgendaArtista>>> obtenerAgenda(@PathVariable Long id) {
        logger.info("[ARTISTAS] GET /api/artistas/{}/agenda", id);
        List<AgendaArtista> agenda = artistaService.obtenerAgendaPorArtista(id);
        return ResponseEntity.ok(agendaArtistaAssembler.toCollectionModel(agenda));
    }

    @PostMapping("/{id}/agenda")
    public ResponseEntity<AgendaArtista> agregarAgenda(@PathVariable Long id,
                                                        @Valid @RequestBody AgendaDTO dto) {
        logger.info("[ARTISTAS] POST /api/artistas/{}/agenda", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(artistaService.agregarAgenda(id, dto));
    }

    @PatchMapping("/agenda/{agendaId}/confirmar")
    public ResponseEntity<AgendaArtista> confirmarAgenda(@PathVariable Long agendaId) {
        logger.info("[ARTISTAS] PATCH confirmar agenda id: {}", agendaId);
        return ResponseEntity.ok(artistaService.confirmarAgenda(agendaId));
    }

    @PatchMapping("/agenda/{agendaId}/cancelar")
    public ResponseEntity<AgendaArtista> cancelarAgenda(@PathVariable Long agendaId) {
        logger.info("[ARTISTAS] PATCH cancelar agenda id: {}", agendaId);
        return ResponseEntity.ok(artistaService.cancelarAgenda(agendaId));
    }
}
