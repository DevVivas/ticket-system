package com.ticket_system.recintos.Assembler;

import com.ticket_system.recintos.Controller.RecintoController;
import com.ticket_system.recintos.Model.Recinto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RecintoAssembler implements RepresentationModelAssembler<Recinto, EntityModel<Recinto>> {

    @Override
    public EntityModel<Recinto> toModel(Recinto recinto) {
        EntityModel<Recinto> model = EntityModel.of(recinto);
        model.add(linkTo(methodOn(RecintoController.class).getById(recinto.getId())).withSelfRel());
        model.add(linkTo(methodOn(RecintoController.class).getAll()).withRel("recintos"));
        return model;
    }
}
