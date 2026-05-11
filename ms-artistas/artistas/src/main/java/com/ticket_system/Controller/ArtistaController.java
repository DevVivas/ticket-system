package com.ticket_system.Controller;

import com.ticket_system.DTO.AgendaDTO;
import com.ticket_system.DTO.ArtistaDTO;
import com.ticket_system.Model.AgendaArtista;
import com.ticket_system.Model.Artista;
import com.ticket_system.Service.ArtistaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/artistas")
public class ArtistaController {

    private static final Logger logger = LoggerFactory.getLogger(ArtistaController.class);

    @Autowired
    private ArtistaService artistaService;

    // GET /api/artistas
    @GetMapping
    public ResponseEntity<List<Artista>> listarTodos() {
        logger.info("[ARTISTAS] GET /api/artistas");
        return ResponseEntity.ok(artistaService.listarTodos());
    }

    // GET /api/artistas/activos
    @GetMapping("/activos")
    public ResponseEntity<List<Artista>> listarActivos() {
        logger.info("[ARTISTAS] GET /api/artistas/activos");
        return ResponseEntity.ok(artistaService.listarActivos());
    }

    // GET /api/artistas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Artista> obtenerPorId(@PathVariable Long id) {
        logger.info("[ARTISTAS] GET /api/artistas/{}", id);
        return ResponseEntity.ok(artistaService.obtenerPorId(id));
    }

    // GET /api/artistas/genero/{genero}
    @GetMapping("/genero/{genero}")
    public ResponseEntity<List<Artista>> listarPorGenero(@PathVariable String genero) {
        logger.info("[ARTISTAS] GET /api/artistas/genero/{}", genero);
        return ResponseEntity.ok(artistaService.listarPorGenero(genero));
    }

    // POST /api/artistas
    @PostMapping
    public ResponseEntity<Artista> crear(@Valid @RequestBody ArtistaDTO dto) {
        logger.info("[ARTISTAS] POST /api/artistas - nombre: {}", dto.getNombre());
        return ResponseEntity.status(HttpStatus.CREATED).body(artistaService.crear(dto));
    }

    // PUT /api/artistas/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Artista> actualizar(@PathVariable Long id,
                                               @Valid @RequestBody ArtistaDTO dto) {
        logger.info("[ARTISTAS] PUT /api/artistas/{}", id);
        return ResponseEntity.ok(artistaService.actualizar(id, dto));
    }

    // DELETE /api/artistas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        logger.info("[ARTISTAS] DELETE /api/artistas/{}", id);
        artistaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/artistas/{id}/agenda
    @GetMapping("/{id}/agenda")
    public ResponseEntity<List<AgendaArtista>> obtenerAgenda(@PathVariable Long id) {
        logger.info("[ARTISTAS] GET /api/artistas/{}/agenda", id);
        return ResponseEntity.ok(artistaService.obtenerAgendaPorArtista(id));
    }

    // POST /api/artistas/{id}/agenda
    @PostMapping("/{id}/agenda")
    public ResponseEntity<AgendaArtista> agregarAgenda(@PathVariable Long id,
                                                        @Valid @RequestBody AgendaDTO dto) {
        logger.info("[ARTISTAS] POST /api/artistas/{}/agenda", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(artistaService.agregarAgenda(id, dto));
    }

    // PATCH /api/artistas/agenda/{agendaId}/confirmar
    @PatchMapping("/agenda/{agendaId}/confirmar")
    public ResponseEntity<AgendaArtista> confirmarAgenda(@PathVariable Long agendaId) {
        logger.info("[ARTISTAS] PATCH confirmar agenda id: {}", agendaId);
        return ResponseEntity.ok(artistaService.confirmarAgenda(agendaId));
    }

    // PATCH /api/artistas/agenda/{agendaId}/cancelar
    @PatchMapping("/agenda/{agendaId}/cancelar")
    public ResponseEntity<AgendaArtista> cancelarAgenda(@PathVariable Long agendaId) {
        logger.info("[ARTISTAS] PATCH cancelar agenda id: {}", agendaId);
        return ResponseEntity.ok(artistaService.cancelarAgenda(agendaId));
    }
}