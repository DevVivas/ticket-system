package com.ticket_system.promotores.Assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.ticket_system.promotores.Controller.PromotorController;
import com.ticket_system.promotores.Model.Promotor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class PromotorAssembler implements RepresentationModelAssembler<Promotor, EntityModel<Promotor>> {

    @Override
    public EntityModel<Promotor> toModel(Promotor entity) {
        EntityModel<Promotor> model = EntityModel.of(entity);
        model.add(linkTo(methodOn(PromotorController.class).obtenerPorId(entity.getId())).withSelfRel());
        model.add(linkTo(methodOn(PromotorController.class).listarTodos()).withRel("promotores"));
        return model;
    }
}
