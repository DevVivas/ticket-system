package com.ticketsystem.eventos.Assembler;

import com.ticketsystem.eventos.Controller.EventoController;
import com.ticketsystem.eventos.Model.Evento;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EventoAssembler implements RepresentationModelAssembler<Evento, EntityModel<Evento>> {

    @Override
    public EntityModel<Evento> toModel(Evento evento) {
        EntityModel<Evento> model = EntityModel.of(evento);
        model.add(linkTo(methodOn(EventoController.class).getById(evento.getId())).withSelfRel());
        model.add(linkTo(methodOn(EventoController.class).getAll()).withRel("eventos"));
        return model;
    }
}
