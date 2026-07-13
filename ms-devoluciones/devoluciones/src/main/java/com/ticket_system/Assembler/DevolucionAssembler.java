package com.ticket_system.Assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.ticket_system.Controller.DevolucionController;
import com.ticket_system.Model.Devolucion;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class DevolucionAssembler implements RepresentationModelAssembler<Devolucion, EntityModel<Devolucion>> {

    @Override
    public EntityModel<Devolucion> toModel(Devolucion entity) {
        EntityModel<Devolucion> model = EntityModel.of(entity);
        model.add(linkTo(methodOn(DevolucionController.class).obtenerPorId(entity.getId())).withSelfRel());
        model.add(linkTo(methodOn(DevolucionController.class).listarTodas()).withRel("devoluciones"));
        return model;
    }
}
