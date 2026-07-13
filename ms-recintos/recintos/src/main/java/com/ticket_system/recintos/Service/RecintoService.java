package com.ticket_system.recintos.Service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ticket_system.recintos.DTO.RecintoDTO;
import com.ticket_system.recintos.DTO.SectorDTO;
import com.ticket_system.recintos.Exception.BusinessException;
import com.ticket_system.recintos.Exception.ResourceNotFoundException;
import com.ticket_system.recintos.Model.Recinto;
import com.ticket_system.recintos.Model.Sector;
import com.ticket_system.recintos.Repository.RecintoRepository;
import com.ticket_system.recintos.Repository.SectorRepository;

@Service
public class RecintoService {

    private static final Logger logger = LoggerFactory.getLogger(RecintoService.class);

    private final RecintoRepository recintoRepository;

    private final SectorRepository sectorRepository;

    RecintoService(RecintoRepository recintoRepository, SectorRepository sectorRepository) {
        this.recintoRepository = recintoRepository;
        this.sectorRepository = sectorRepository;
    }

    public List<Recinto> obtenerTodos() {
        logger.info("[RECINTOS] Obteniendo todos los recintos");
        return recintoRepository.findAll();
    }

    public Recinto obtenerPorId(Long id) {
        logger.info("[RECINTOS] Buscando recinto con id: {}", id);
        return recintoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Recinto no encontrado con id: " + id));
    }

    public Recinto crear(RecintoDTO dto) {
        logger.info("[RECINTOS] Creando recinto: {}", dto.getNombre());
        Recinto recinto = new Recinto();
        recinto.setNombre(dto.getNombre());
        recinto.setDireccion(dto.getDireccion());
        recinto.setCapacidadMaxima(dto.getCapacidadMaxima());
        Recinto guardado = recintoRepository.save(recinto);
        logger.info("[RECINTOS] Recinto creado con id: {}", guardado.getId());
        return guardado;
    }

    public Recinto actualizar(Long id, RecintoDTO dto) {
        logger.info("[RECINTOS] Actualizando recinto con id: {}", id);
        Recinto recinto = obtenerPorId(id);
        recinto.setNombre(dto.getNombre());
        recinto.setDireccion(dto.getDireccion());
        recinto.setCapacidadMaxima(dto.getCapacidadMaxima());
        Recinto actualizado = recintoRepository.save(recinto);
        logger.info("[RECINTOS] Recinto actualizado con id: {}", actualizado.getId());
        return actualizado;
    }

    public void eliminar(Long id) {
        logger.info("[RECINTOS] Eliminando recinto con id: {}", id);
        obtenerPorId(id);
        recintoRepository.deleteById(id);
        logger.info("[RECINTOS] Recinto eliminado con id: {}", id);
    }

    public Sector agregarSector(Long recintoId, SectorDTO dto) {
        logger.info("[RECINTOS] Agregando sector al recinto: {}", recintoId);
        Recinto recinto = obtenerPorId(recintoId);

        // Regla de negocio: la capacidad del sector no puede superar la del recinto
        int totalSectores = sectorRepository.findByRecintoId(recintoId)
            .stream()
            .mapToInt(Sector::getCapacidad)
            .sum();

        if (totalSectores + dto.getCapacidad() > recinto.getCapacidadMaxima()) {
            throw new BusinessException("La capacidad del sector supera la capacidad máxima del recinto. " +
                "Disponible: " + (recinto.getCapacidadMaxima() - totalSectores));
        }

        Sector sector = new Sector();
        sector.setNombre(dto.getNombre());
        sector.setCapacidad(dto.getCapacidad());
        sector.setPrecioBase(dto.getPrecioBase());
        sector.setRecinto(recinto);
        Sector guardado = sectorRepository.save(sector);
        logger.info("[RECINTOS] Sector creado con id: {}", guardado.getId());
        return guardado;
    }

    public List<Sector> obtenerSectores(Long recintoId) {
        logger.info("[RECINTOS] Obteniendo sectores del recinto: {}", recintoId);
        obtenerPorId(recintoId);
        return sectorRepository.findByRecintoId(recintoId);
    }

    public void eliminarSector(Long sectorId) {
        logger.info("[RECINTOS] Eliminando sector con id: {}", sectorId);
        if (!sectorRepository.existsById(sectorId)) {
            throw new ResourceNotFoundException("Sector no encontrado con id: " + sectorId);
        }
        sectorRepository.deleteById(sectorId);
        logger.info("[RECINTOS] Sector eliminado con id: {}", sectorId);
    }
}
