package com.ticket_system.preventa.Assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.ticket_system.preventa.Controller.PreventaController;
import com.ticket_system.preventa.Model.CodigoBeneficio;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class CodigoBeneficioAssembler implements RepresentationModelAssembler<CodigoBeneficio, EntityModel<CodigoBeneficio>> {

    @Override
    public EntityModel<CodigoBeneficio> toModel(CodigoBeneficio entity) {
        EntityModel<CodigoBeneficio> model = EntityModel.of(entity);
        model.add(linkTo(methodOn(PreventaController.class).getById(entity.getId())).withSelfRel());
        model.add(linkTo(methodOn(PreventaController.class).getAll()).withRel("codigos-beneficio"));
        return model;
    }
}
