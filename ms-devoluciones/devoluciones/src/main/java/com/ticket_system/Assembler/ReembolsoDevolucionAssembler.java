package com.ticket_system.Assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.ticket_system.Controller.DevolucionController;
import com.ticket_system.Model.ReembolsoDevolucion;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ReembolsoDevolucionAssembler implements RepresentationModelAssembler<ReembolsoDevolucion, EntityModel<ReembolsoDevolucion>> {

    @Override
    public EntityModel<ReembolsoDevolucion> toModel(ReembolsoDevolucion entity) {
        EntityModel<ReembolsoDevolucion> model = EntityModel.of(entity);
        model.add(linkTo(methodOn(DevolucionController.class).obtenerReembolsos(entity.getDevolucion().getId())).withSelfRel());
        model.add(linkTo(methodOn(DevolucionController.class).obtenerPorId(entity.getDevolucion().getId())).withRel("devolucion"));
        return model;
    }
}
