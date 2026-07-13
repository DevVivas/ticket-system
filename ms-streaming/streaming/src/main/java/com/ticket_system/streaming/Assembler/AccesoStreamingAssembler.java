package com.ticket_system.streaming.Assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.ticket_system.streaming.Controller.StreamingController;
import com.ticket_system.streaming.Model.AccesoStreaming;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class AccesoStreamingAssembler implements RepresentationModelAssembler<AccesoStreaming, EntityModel<AccesoStreaming>> {

    @Override
    public EntityModel<AccesoStreaming> toModel(AccesoStreaming entity) {
        EntityModel<AccesoStreaming> model = EntityModel.of(entity);
        model.add(linkTo(methodOn(StreamingController.class).obtenerAccesos(entity.getStreaming().getId())).withSelfRel());
        model.add(linkTo(methodOn(StreamingController.class).obtenerPorId(entity.getStreaming().getId())).withRel("streaming"));
        return model;
    }
}
