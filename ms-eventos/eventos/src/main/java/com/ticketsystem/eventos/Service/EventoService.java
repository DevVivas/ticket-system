package com.ticketsystem.eventos.Service;

import com.ticketsystem.eventos.DTO.EventoDTO;
import com.ticketsystem.eventos.Exception.BusinessException;
import com.ticketsystem.eventos.Exception.ResourceNotFoundException;
import com.ticketsystem.eventos.Model.Evento;
import com.ticketsystem.eventos.Repository.EventoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EventoService {

    private static final Logger logger = LoggerFactory.getLogger(EventoService.class);

    @Autowired
    private EventoRepository eventoRepository;

    public List<Evento> obtenerTodos() {
        logger.info("[EVENTOS] Obteniendo todos los eventos");
        return eventoRepository.findAll();
    }

    public Evento obtenerPorId(Long id) {
        logger.info("[EVENTOS] Buscando evento con id: {}", id);
        return eventoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con id: " + id));
    }

    public Evento crear(EventoDTO dto) {
        logger.info("[EVENTOS] Creando nuevo evento: {}", dto.getNombre());
        Evento evento = new Evento();
        evento.setNombre(dto.getNombre());
        evento.setTipo(dto.getTipo());
        evento.setFechaEvento(dto.getFechaEvento());
        evento.setLugar(dto.getLugar());
        evento.setCapacidadTotal(dto.getCapacidadTotal());
        evento.setDescripcion(dto.getDescripcion());
        evento.setEstado("ACTIVO");
        Evento guardado = eventoRepository.save(evento);
        logger.info("[EVENTOS] Evento creado con id: {}", guardado.getId());
        return guardado;
    }

    public Evento actualizar(Long id, EventoDTO dto) {
        logger.info("[EVENTOS] Actualizando evento con id: {}", id);
        Evento evento = obtenerPorId(id);
        evento.setNombre(dto.getNombre());
        evento.setTipo(dto.getTipo());
        evento.setFechaEvento(dto.getFechaEvento());
        evento.setLugar(dto.getLugar());
        evento.setCapacidadTotal(dto.getCapacidadTotal());
        evento.setDescripcion(dto.getDescripcion());
        Evento actualizado = eventoRepository.save(evento);
        logger.info("[EVENTOS] Evento actualizado con id: {}", actualizado.getId());
        return actualizado;
    }

    public void eliminar(Long id) {
        logger.info("[EVENTOS] Eliminando evento con id: {}", id);
        obtenerPorId(id);
        eventoRepository.deleteById(id);
        logger.info("[EVENTOS] Evento eliminado con id: {}", id);
    }

    public Evento cancelar(Long id) {
        logger.warn("[EVENTOS] Cancelando evento con id: {}", id);
        Evento evento = obtenerPorId(id);
        if (!"ACTIVO".equals(evento.getEstado())) {
            throw new BusinessException("Solo se pueden cancelar eventos con estado ACTIVO. Estado actual: " + evento.getEstado());
        }
        evento.setEstado("CANCELADO");
        Evento cancelado = eventoRepository.save(evento);
        logger.info("[EVENTOS] Evento {} cancelado", id);
        return cancelado;
    }
}
