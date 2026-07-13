package com.ticket_system.ventas.Assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.ticket_system.ventas.Controller.VentaController;
import com.ticket_system.ventas.Model.Venta;

@Component
public class VentaAssembler implements RepresentationModelAssembler<Venta, EntityModel<Venta>> {

    @Override
    public EntityModel<Venta> toModel(Venta venta) {
        EntityModel<Venta> model = EntityModel.of(venta);
        model.add(linkTo(methodOn(VentaController.class).getById(venta.getId())).withSelfRel());
        model.add(linkTo(methodOn(VentaController.class).getAll()).withRel("ventas"));
        return model;
    }

    @Override
    public CollectionModel<EntityModel<Venta>> toCollectionModel(Iterable<? extends Venta> entities) {
        CollectionModel<EntityModel<Venta>> models = RepresentationModelAssembler.super.toCollectionModel(entities);
        models.add(linkTo(methodOn(VentaController.class).getAll()).withSelfRel());
        return models;
    }
}
