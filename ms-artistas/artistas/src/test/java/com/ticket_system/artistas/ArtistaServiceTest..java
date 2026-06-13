package com.ticket_system.artistas;

import com.ticket_system.DTO.ArtistaDTO;
import com.ticket_system.Exception.BusinessException;
import com.ticket_system.Exception.ResourceNotFoundException;
import com.ticket_system.Model.AgendaArtista;
import com.ticket_system.Model.Artista;
import com.ticket_system.Repository.AgendaArtistaRepository;
import com.ticket_system.Repository.ArtistaRepository;
import com.ticket_system.Service.ArtistaService;
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
class ArtistaServiceTest {

    @Mock
    private ArtistaRepository artistaRepository;

    @Mock
    private AgendaArtistaRepository agendaArtistaRepository;

    @InjectMocks
    private ArtistaService artistaService;

    private Artista artista;
    private ArtistaDTO artistaDTO;

    @BeforeEach
    void setUp() {
        artista = new Artista();
        artista.setId(1L);
        artista.setNombre("Bad Bunny");
        artista.setGenero("Reggaeton");
        artista.setNacionalidad("Puerto Rico");
        artista.setEstado("ACTIVO");

        artistaDTO = new ArtistaDTO();
        artistaDTO.setNombre("Bad Bunny");
        artistaDTO.setGenero("Reggaeton");
        artistaDTO.setNacionalidad("Puerto Rico");
    }

    // ─── TEST: obtenerPorId ───────────────────────────────────────────────────

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarArtista() {
        when(artistaRepository.findById(1L)).thenReturn(Optional.of(artista));

        Artista resultado = artistaService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("Bad Bunny", resultado.getNombre());
        verify(artistaRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeLanzarResourceNotFoundException() {
        when(artistaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> artistaService.obtenerPorId(99L));
    }

    // ─── TEST: crear ──────────────────────────────────────────────────────────

    @Test
    void crear_cuandoNombreNoExiste_debeCrearArtista() {
        when(artistaRepository.findByNombreContainingIgnoreCase("Bad Bunny"))
            .thenReturn(Collections.emptyList());
        when(artistaRepository.save(any(Artista.class))).thenReturn(artista);

        Artista resultado = artistaService.crear(artistaDTO);

        assertNotNull(resultado);
        assertEquals("Bad Bunny", resultado.getNombre());
        verify(artistaRepository, times(1)).save(any(Artista.class));
    }

    @Test
    void crear_cuandoNombreYaExiste_debeLanzarBusinessException() {
        when(artistaRepository.findByNombreContainingIgnoreCase("Bad Bunny"))
            .thenReturn(List.of(artista));

        assertThrows(BusinessException.class,
            () -> artistaService.crear(artistaDTO));

        verify(artistaRepository, never()).save(any());
    }

    // ─── TEST: eliminar ───────────────────────────────────────────────────────

    @Test
    void eliminar_cuandoNoTieneAgendaConfirmada_debeDesactivar() {
        when(artistaRepository.findById(1L)).thenReturn(Optional.of(artista));
        when(agendaArtistaRepository.findByArtistaId(1L))
            .thenReturn(Collections.emptyList());
        when(artistaRepository.save(any(Artista.class))).thenReturn(artista);

        artistaService.eliminar(1L);

        assertEquals("INACTIVO", artista.getEstado());
        verify(artistaRepository, times(1)).save(artista);
    }

    @Test
    void eliminar_cuandoTieneAgendaConfirmada_debeLanzarBusinessException() {
        AgendaArtista agenda = new AgendaArtista();
        agenda.setEstadoAgenda("CONFIRMADO");

        when(artistaRepository.findById(1L)).thenReturn(Optional.of(artista));
        when(agendaArtistaRepository.findByArtistaId(1L))
            .thenReturn(List.of(agenda));

        assertThrows(BusinessException.class,
            () -> artistaService.eliminar(1L));

        verify(artistaRepository, never()).save(any());
    }

    // ─── TEST: listarActivos ──────────────────────────────────────────────────

    @Test
    void listarActivos_debeRetornarSoloActivos() {
        when(artistaRepository.findByEstado("ACTIVO"))
            .thenReturn(List.of(artista));

        List<Artista> resultado = artistaService.listarActivos();

        assertEquals(1, resultado.size());
        assertEquals("ACTIVO", resultado.get(0).getEstado());
    }
}