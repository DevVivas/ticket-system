package com.ticket_system.tickets.Controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ticket_system.tickets.DTO.TicketDTO;
import com.ticket_system.tickets.Model.Ticket;
import com.ticket_system.tickets.Service.TicketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);

    @Autowired
    private TicketService ticketService;

    @GetMapping
    public ResponseEntity<List<Ticket>> getAll() {
        logger.info("GET /api/tickets");
        return ResponseEntity.ok(ticketService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getById(@PathVariable Long id) {
        return ticketService.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Ticket> getByCodigo(@PathVariable String codigo) {
        return ticketService.obtenerPorCodigo(codigo)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<Ticket>> getByEvento(@PathVariable Long eventoId) {
        return ResponseEntity.ok(ticketService.obtenerPorEvento(eventoId));
    }

    @GetMapping("/comprador/{compradorId}")
    public ResponseEntity<List<Ticket>> getByComprador(@PathVariable Long compradorId) {
        return ResponseEntity.ok(ticketService.obtenerPorComprador(compradorId));
    }

    @PostMapping
    public ResponseEntity<Ticket> generate(@Valid @RequestBody TicketDTO dto) {
        logger.info("POST /api/tickets");
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ticketService.generarTicket(dto));
    }

    @PutMapping("/{id}/vender")
    public ResponseEntity<Ticket> vender(@PathVariable Long id) {
        try {
            logger.info("PUT /api/tickets/{}/vender", id);
            return ResponseEntity.ok(ticketService.marcarVendido(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/usar")
    public ResponseEntity<Ticket> usar(@PathVariable Long id) {
        try {
            logger.info("PUT /api/tickets/{}/usar", id);
            return ResponseEntity.ok(ticketService.marcarUsado(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/anular")
    public ResponseEntity<Ticket> anular(@PathVariable Long id) {
        try {
            logger.info("PUT /api/tickets/{}/anular", id);
            return ResponseEntity.ok(ticketService.anular(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            ticketService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
