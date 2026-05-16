package com.ticket_system.recintos.Service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ticket_system.recintos.DTO.RecintoDTO;
import com.ticket_system.recintos.DTO.SectorDTO;
import com.ticket_system.recintos.Model.Recinto;
import com.ticket_system.recintos.Model.Sector;
import com.ticket_system.recintos.Repository.RecintoRepository;
import com.ticket_system.recintos.Repository.SectorRepository;

@Service
public class RecintoService {

    private static final Logger logger = LoggerFactory.getLogger(RecintoService.class);

    @Autowired
    private RecintoRepository recintoRepository;

    @Autowired
    private SectorRepository sectorRepository;

    public List<Recinto> obtenerTodos() {
        logger.info("Obteniendo todos los recintos");
        return recintoRepository.findAll();
    }

    public Optional<Recinto> obtenerPorId(Long id) {
        logger.info("Buscando recinto con id: {}", id);
        return recintoRepository.findById(id);
    }

    public Recinto crear(RecintoDTO dto) {
        logger.info("Creando recinto: {}", dto.getNombre());
        Recinto recinto = new Recinto();
        recinto.setNombre(dto.getNombre());
        recinto.setDireccion(dto.getDireccion());
        recinto.setCapacidadMaxima(dto.getCapacidadMaxima());
        Recinto guardado = recintoRepository.save(recinto);
        logger.info("Recinto creado con id: {}", guardado.getId());
        return guardado;
    }

    public Recinto actualizar(Long id, RecintoDTO dto) {
        logger.info("Actualizando recinto con id: {}", id);
        Recinto recinto = recintoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recinto no encontrado con id: " + id));
        recinto.setNombre(dto.getNombre());
        recinto.setDireccion(dto.getDireccion());
        recinto.setCapacidadMaxima(dto.getCapacidadMaxima());
        return recintoRepository.save(recinto);
    }

    public void eliminar(Long id) {
        logger.info("Eliminando recinto con id: {}", id);
        if (!recintoRepository.existsById(id)) {
            throw new RuntimeException("Recinto no encontrado con id: " + id);
        }
        recintoRepository.deleteById(id);
    }

    public Sector agregarSector(Long recintoId, SectorDTO dto) {
        logger.info("Agregando sector al recinto: {}", recintoId);
        Recinto recinto = recintoRepository.findById(recintoId)
            .orElseThrow(() -> new RuntimeException("Recinto no encontrado con id: " + recintoId));

        // Regla de negocio: la capacidad del sector no puede superar la del recinto
        int totalSectores = sectorRepository.findByRecintoId(recintoId)
            .stream()
            .mapToInt(Sector::getCapacidad)
            .sum();

        if (totalSectores + dto.getCapacidad() > recinto.getCapacidadMaxima()) {
            throw new RuntimeException("La capacidad del sector supera la capacidad máxima del recinto");
        }

        Sector sector = new Sector();
        sector.setNombre(dto.getNombre());
        sector.setCapacidad(dto.getCapacidad());
        sector.setPrecioBase(dto.getPrecioBase());
        sector.setRecinto(recinto);
        Sector guardado = sectorRepository.save(sector);
        logger.info("Sector creado con id: {}", guardado.getId());
        return guardado;
    }

    public List<Sector> obtenerSectores(Long recintoId) {
        logger.info("Obteniendo sectores del recinto: {}", recintoId);
        if (!recintoRepository.existsById(recintoId)) {
            throw new RuntimeException("Recinto no encontrado con id: " + recintoId);
        }
        return sectorRepository.findByRecintoId(recintoId);
    }

    public void eliminarSector(Long sectorId) {
        logger.info("Eliminando sector con id: {}", sectorId);
        if (!sectorRepository.existsById(sectorId)) {
            throw new RuntimeException("Sector no encontrado con id: " + sectorId);
        }
        sectorRepository.deleteById(sectorId);
    }
}
