package com.ticketsystem.eventos.Service;

import com.ticketsystem.eventos.DTO.EventoDTO;
import com.ticketsystem.eventos.Model.Evento;
import com.ticketsystem.eventos.Repository.EventoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    private static final Logger logger = LoggerFactory.getLogger(EventoService.class);

    @Autowired
    private EventoRepository eventoRepository;

    public List<Evento> obtenerTodos() {
        logger.info("Obteniendo todos los eventos");
        return eventoRepository.findAll();
    }

    public Optional<Evento> obtenerPorId(Long id) {
        logger.info("Buscando evento con id: {}", id);
        return eventoRepository.findById(id);
    }

    public Evento crear(EventoDTO dto) {
        logger.info("Creando nuevo evento: {}", dto.getNombre());
        Evento evento = new Evento();
        evento.setNombre(dto.getNombre());
        evento.setTipo(dto.getTipo());
        evento.setFechaEvento(dto.getFechaEvento());
        evento.setLugar(dto.getLugar());
        evento.setCapacidadTotal(dto.getCapacidadTotal());
        evento.setDescripcion(dto.getDescripcion());
        evento.setEstado("ACTIVO");
        Evento guardado = eventoRepository.save(evento);
        logger.info("Evento creado con id: {}", guardado.getId());
        return guardado;
    }

    public Evento actualizar(Long id, EventoDTO dto) {
        logger.info("Actualizando evento con id: {}", id);
        Evento evento = eventoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Evento no encontrado con id: " + id));
        evento.setNombre(dto.getNombre());
        evento.setTipo(dto.getTipo());
        evento.setFechaEvento(dto.getFechaEvento());
        evento.setLugar(dto.getLugar());
        evento.setCapacidadTotal(dto.getCapacidadTotal());
        evento.setDescripcion(dto.getDescripcion());
        return eventoRepository.save(evento);
    }

    public void eliminar(Long id) {
        logger.info("Eliminando evento con id: {}", id);
        if (!eventoRepository.existsById(id)) {
            throw new RuntimeException("Evento no encontrado con id: " + id);
        }
        eventoRepository.deleteById(id);
    }

    public Evento cancelar(Long id) {
        logger.warn("Cancelando evento con id: {}", id);
        Evento evento = eventoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Evento no encontrado con id: " + id));
        if (!"ACTIVO".equals(evento.getEstado())) {
            throw new RuntimeException("Solo se pueden cancelar eventos con estado ACTIVO");
        }
        evento.setEstado("CANCELADO");
        return eventoRepository.save(evento);
    }
}
