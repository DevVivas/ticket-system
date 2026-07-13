package com.ticket_system.ventas.Assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.ticket_system.ventas.Controller.VentaController;
import com.ticket_system.ventas.Model.ItemVenta;

@Component
public class ItemVentaAssembler implements RepresentationModelAssembler<ItemVenta, EntityModel<ItemVenta>> {

    @Override
    public EntityModel<ItemVenta> toModel(ItemVenta itemVenta) {
        EntityModel<ItemVenta> model = EntityModel.of(itemVenta);
        if (itemVenta.getVenta() != null && itemVenta.getVenta().getId() != null) {
            model.add(linkTo(methodOn(VentaController.class).getById(itemVenta.getVenta().getId())).withRel("venta"));
        }
        return model;
    }
}
