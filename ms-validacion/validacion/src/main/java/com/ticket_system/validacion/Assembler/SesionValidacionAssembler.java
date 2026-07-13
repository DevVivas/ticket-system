package com.ticket_system.validacion.Assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.ticket_system.validacion.Controller.ValidacionController;
import com.ticket_system.validacion.Model.SesionValidacion;

@Component
public class SesionValidacionAssembler implements RepresentationModelAssembler<SesionValidacion, EntityModel<SesionValidacion>> {

    @Override
    public EntityModel<SesionValidacion> toModel(SesionValidacion sesion) {
        EntityModel<SesionValidacion> model = EntityModel.of(sesion);
        model.add(linkTo(methodOn(ValidacionController.class).obtenerSesion(sesion.getId())).withSelfRel());
        model.add(linkTo(methodOn(ValidacionController.class).listarSesiones()).withRel("sesiones"));
        model.add(linkTo(methodOn(ValidacionController.class).obtenerValidaciones(sesion.getId())).withRel("validaciones"));
        return model;
    }

    @Override
    public CollectionModel<EntityModel<SesionValidacion>> toCollectionModel(Iterable<? extends SesionValidacion> entities) {
        CollectionModel<EntityModel<SesionValidacion>> models = RepresentationModelAssembler.super.toCollectionModel(entities);
        models.add(linkTo(methodOn(ValidacionController.class).listarSesiones()).withSelfRel());
        return models;
    }
}
