package com.ticket_system.recintos;

import com.ticket_system.recintos.DTO.RecintoDTO;
import com.ticket_system.recintos.DTO.SectorDTO;
import com.ticket_system.recintos.Exception.BusinessException;
import com.ticket_system.recintos.Exception.ResourceNotFoundException;
import com.ticket_system.recintos.Model.Recinto;
import com.ticket_system.recintos.Model.Sector;
import com.ticket_system.recintos.Repository.RecintoRepository;
import com.ticket_system.recintos.Repository.SectorRepository;
import com.ticket_system.recintos.Service.RecintoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecintoServiceTest {

    @Mock
    private RecintoRepository recintoRepository;

    @Mock
    private SectorRepository sectorRepository;

    @InjectMocks
    private RecintoService recintoService;

    private Recinto recinto;
    private RecintoDTO recintoDTO;
    private Sector sector;
    private SectorDTO sectorDTO;

    @BeforeEach
    void setUp() {
        recinto = new Recinto();
        recinto.setId(1L);
        recinto.setNombre("Estadio Nacional");
        recinto.setDireccion("Av. Grecia 2001");
        recinto.setCapacidadMaxima(5000);

        recintoDTO = new RecintoDTO();
        recintoDTO.setNombre("Estadio Nacional");
        recintoDTO.setDireccion("Av. Grecia 2001");
        recintoDTO.setCapacidadMaxima(5000);

        sector = new Sector();
        sector.setId(1L);
        sector.setNombre("Cancha");
        sector.setCapacidad(2000);
        sector.setPrecioBase(50000.0);
        sector.setRecinto(recinto);

        sectorDTO = new SectorDTO();
        sectorDTO.setNombre("Cancha");
        sectorDTO.setCapacidad(2000);
        sectorDTO.setPrecioBase(50000.0);
    }

    // ─── TEST: obtenerPorId ───────────────────────────────────────────────────

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarRecinto() {
        when(recintoRepository.findById(1L)).thenReturn(Optional.of(recinto));

        Recinto resultado = recintoService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("Estadio Nacional", resultado.getNombre());
        verify(recintoRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeLanzarResourceNotFoundException() {
        when(recintoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> recintoService.obtenerPorId(99L));
    }

    // ─── TEST: crear ──────────────────────────────────────────────────────────

    @Test
    void crear_debeCrearRecintoCorrectamente() {
        when(recintoRepository.save(any(Recinto.class))).thenReturn(recinto);

        Recinto resultado = recintoService.crear(recintoDTO);

        assertNotNull(resultado);
        assertEquals("Estadio Nacional", resultado.getNombre());
        verify(recintoRepository, times(1)).save(any(Recinto.class));
    }

    // ─── TEST: agregarSector ──────────────────────────────────────────────────

    @Test
    void agregarSector_cuandoCapacidadDisponible_debeAgregarSector() {
        when(recintoRepository.findById(1L)).thenReturn(Optional.of(recinto));
        when(sectorRepository.findByRecintoId(1L)).thenReturn(Collections.emptyList());
        when(sectorRepository.save(any(Sector.class))).thenReturn(sector);

        Sector resultado = recintoService.agregarSector(1L, sectorDTO);

        assertNotNull(resultado);
        assertEquals("Cancha", resultado.getNombre());
        verify(sectorRepository, times(1)).save(any(Sector.class));
    }

    @Test
    void agregarSector_cuandoCapacidadExcede_debeLanzarBusinessException() {
        // Sector existente que ya ocupa 4500 de 5000
        Sector sectorExistente = new Sector();
        sectorExistente.setCapacidad(4500);

        when(recintoRepository.findById(1L)).thenReturn(Optional.of(recinto));
        when(sectorRepository.findByRecintoId(1L)).thenReturn(List.of(sectorExistente));

        // Intentar agregar sector de 2000 cuando solo quedan 500
        assertThrows(BusinessException.class,
            () -> recintoService.agregarSector(1L, sectorDTO));

        verify(sectorRepository, never()).save(any());
    }

    // ─── TEST: eliminar ───────────────────────────────────────────────────────

    @Test
    void eliminar_cuandoExiste_debeEliminar() {
        when(recintoRepository.findById(1L)).thenReturn(Optional.of(recinto));
        doNothing().when(recintoRepository).deleteById(1L);

        recintoService.eliminar(1L);

        verify(recintoRepository, times(1)).deleteById(1L);
    }

    // ─── TEST: obtenerSectores ────────────────────────────────────────────────

    @Test
    void obtenerSectores_debeRetornarSectoresDelRecinto() {
        when(recintoRepository.findById(1L)).thenReturn(Optional.of(recinto));
        when(sectorRepository.findByRecintoId(1L)).thenReturn(List.of(sector));

        List<Sector> resultado = recintoService.obtenerSectores(1L);

        assertEquals(1, resultado.size());
        assertEquals("Cancha", resultado.get(0).getNombre());
    }
}