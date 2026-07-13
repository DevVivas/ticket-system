package com.ticket_system.recintos.Assembler;

import com.ticket_system.recintos.Controller.RecintoController;
import com.ticket_system.recintos.Model.Sector;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SectorAssembler implements RepresentationModelAssembler<Sector, EntityModel<Sector>> {

    @Override
    public EntityModel<Sector> toModel(Sector sector) {
        EntityModel<Sector> model = EntityModel.of(sector);
        model.add(linkTo(methodOn(RecintoController.class).getSectores(sector.getRecinto().getId())).withRel("sectores"));
        model.add(linkTo(methodOn(RecintoController.class).getById(sector.getRecinto().getId())).withRel("recinto"));
        return model;
    }
}
