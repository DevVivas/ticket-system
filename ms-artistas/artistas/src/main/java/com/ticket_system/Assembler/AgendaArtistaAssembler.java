package com.ticket_system.Assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.ticket_system.Controller.ArtistaController;
import com.ticket_system.Model.AgendaArtista;

@Component
public class AgendaArtistaAssembler implements RepresentationModelAssembler<AgendaArtista, EntityModel<AgendaArtista>> {

    @Override
    public EntityModel<AgendaArtista> toModel(AgendaArtista agenda) {
        EntityModel<AgendaArtista> model = EntityModel.of(agenda);
        if (agenda.getArtista() != null && agenda.getArtista().getId() != null) {
            model.add(linkTo(methodOn(ArtistaController.class).obtenerPorId(agenda.getArtista().getId())).withRel("artista"));
            model.add(linkTo(methodOn(ArtistaController.class).obtenerAgenda(agenda.getArtista().getId())).withRel("agenda"));
        }
        return model;
    }
}
