package com.ticket_system.tickets.Assembler;

import com.ticket_system.tickets.Controller.TicketController;
import com.ticket_system.tickets.Model.Ticket;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TicketAssembler implements RepresentationModelAssembler<Ticket, EntityModel<Ticket>> {

    @Override
    public EntityModel<Ticket> toModel(Ticket ticket) {
        EntityModel<Ticket> model = EntityModel.of(ticket);
        model.add(linkTo(methodOn(TicketController.class).getById(ticket.getId())).withSelfRel());
        model.add(linkTo(methodOn(TicketController.class).getAll()).withRel("tickets"));
        return model;
    }
}
