package com.ticket_system.tickets.Controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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

import com.ticket_system.tickets.Assembler.TicketAssembler;
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

    @Autowired
    private TicketAssembler ticketAssembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Ticket>>> getAll() {
        logger.info("GET /api/tickets");
        List<EntityModel<Ticket>> tickets = ticketService.obtenerTodos().stream()
                .map(ticketAssembler::toModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(tickets));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Ticket>> getById(@PathVariable Long id) {
        Ticket ticket = ticketService.obtenerPorId(id);
        return ResponseEntity.ok(ticketAssembler.toModel(ticket));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<EntityModel<Ticket>> getByCodigo(@PathVariable String codigo) {
        Ticket ticket = ticketService.obtenerPorCodigo(codigo);
        return ResponseEntity.ok(ticketAssembler.toModel(ticket));
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<CollectionModel<EntityModel<Ticket>>> getByEvento(@PathVariable Long eventoId) {
        List<EntityModel<Ticket>> tickets = ticketService.obtenerPorEvento(eventoId).stream()
                .map(ticketAssembler::toModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(tickets));
    }

    @GetMapping("/comprador/{compradorId}")
    public ResponseEntity<CollectionModel<EntityModel<Ticket>>> getByComprador(@PathVariable Long compradorId) {
        List<EntityModel<Ticket>> tickets = ticketService.obtenerPorComprador(compradorId).stream()
                .map(ticketAssembler::toModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(tickets));
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
