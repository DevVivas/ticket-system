package com.ticket_system.Assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.ticket_system.Controller.ArtistaController;
import com.ticket_system.Model.Artista;

@Component
public class ArtistaAssembler implements RepresentationModelAssembler<Artista, EntityModel<Artista>> {

    @Override
    public EntityModel<Artista> toModel(Artista artista) {
        EntityModel<Artista> model = EntityModel.of(artista);
        model.add(linkTo(methodOn(ArtistaController.class).obtenerPorId(artista.getId())).withSelfRel());
        model.add(linkTo(methodOn(ArtistaController.class).listarTodos()).withRel("artistas"));
        model.add(linkTo(methodOn(ArtistaController.class).obtenerAgenda(artista.getId())).withRel("agenda"));
        return model;
    }

    @Override
    public CollectionModel<EntityModel<Artista>> toCollectionModel(Iterable<? extends Artista> entities) {
        CollectionModel<EntityModel<Artista>> models = RepresentationModelAssembler.super.toCollectionModel(entities);
        models.add(linkTo(methodOn(ArtistaController.class).listarTodos()).withSelfRel());
        return models;
    }
}
