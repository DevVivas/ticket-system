package com.ticket_system.promotores.Assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.ticket_system.promotores.Controller.PromotorController;
import com.ticket_system.promotores.Model.ComisionPromotor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ComisionPromotorAssembler implements RepresentationModelAssembler<ComisionPromotor, EntityModel<ComisionPromotor>> {

    @Override
    public EntityModel<ComisionPromotor> toModel(ComisionPromotor entity) {
        EntityModel<ComisionPromotor> model = EntityModel.of(entity);
        model.add(linkTo(methodOn(PromotorController.class).obtenerComisiones(entity.getPromotor().getId())).withSelfRel());
        model.add(linkTo(methodOn(PromotorController.class).obtenerPorId(entity.getPromotor().getId())).withRel("promotor"));
        return model;
    }
}
