package com.ticket_system.validacion.Assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.ticket_system.validacion.Controller.ValidacionController;
import com.ticket_system.validacion.Model.ValidacionTicket;

@Component
public class ValidacionTicketAssembler implements RepresentationModelAssembler<ValidacionTicket, EntityModel<ValidacionTicket>> {

    @Override
    public EntityModel<ValidacionTicket> toModel(ValidacionTicket validacion) {
        EntityModel<ValidacionTicket> model = EntityModel.of(validacion);
        if (validacion.getSesion() != null && validacion.getSesion().getId() != null) {
            model.add(linkTo(methodOn(ValidacionController.class).obtenerSesion(validacion.getSesion().getId())).withRel("sesion"));
        }
        return model;
    }
}
