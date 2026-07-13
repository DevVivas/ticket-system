package com.ticket_system.streaming.Assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.ticket_system.streaming.Controller.StreamingController;
import com.ticket_system.streaming.Model.Streaming;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class StreamingAssembler implements RepresentationModelAssembler<Streaming, EntityModel<Streaming>> {

    @Override
    public EntityModel<Streaming> toModel(Streaming entity) {
        EntityModel<Streaming> model = EntityModel.of(entity);
        model.add(linkTo(methodOn(StreamingController.class).obtenerPorId(entity.getId())).withSelfRel());
        model.add(linkTo(methodOn(StreamingController.class).listarTodos()).withRel("streamings"));
        return model;
    }
}
