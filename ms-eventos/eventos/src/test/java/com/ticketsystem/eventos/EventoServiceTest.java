package com.ticketsystem.eventos;

import com.ticketsystem.eventos.DTO.EventoDTO;
import com.ticketsystem.eventos.Exception.BusinessException;
import com.ticketsystem.eventos.Exception.ResourceNotFoundException;
import com.ticketsystem.eventos.Model.Evento;
import com.ticketsystem.eventos.Repository.EventoRepository;
import com.ticketsystem.eventos.Service.EventoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @InjectMocks
    private EventoService eventoService;

    private Evento evento;
    private EventoDTO eventoDTO;

    @BeforeEach
    void setUp() {
        evento = new Evento();
        evento.setId(1L);
        evento.setNombre("Karol G en Chile");
        evento.setTipo("CONCIERTO");
        evento.setFechaEvento(LocalDateTime.of(2026, 12, 15, 20, 0));
        evento.setLugar("Estadio Nacional");
        evento.setCapacidadTotal(5000);
        evento.setEstado("ACTIVO");

        eventoDTO = new EventoDTO();
        eventoDTO.setNombre("Karol G en Chile");
        eventoDTO.setTipo("CONCIERTO");
        eventoDTO.setFechaEvento(LocalDateTime.of(2026, 12, 15, 20, 0));
        eventoDTO.setLugar("Estadio Nacional");
        eventoDTO.setCapacidadTotal(5000);
    }

    // ─── TEST: obtenerPorId ───────────────────────────────────────────────────

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarEvento() {
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));

        Evento resultado = eventoService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("Karol G en Chile", resultado.getNombre());
        verify(eventoRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeLanzarResourceNotFoundException() {
        when(eventoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> eventoService.obtenerPorId(99L));
    }

    // ─── TEST: crear ──────────────────────────────────────────────────────────

    @Test
    void crear_debeCrearEventoConEstadoActivo() {
        when(eventoRepository.save(any(Evento.class))).thenReturn(evento);

        Evento resultado = eventoService.crear(eventoDTO);

        assertNotNull(resultado);
        assertEquals("ACTIVO", resultado.getEstado());
        verify(eventoRepository, times(1)).save(any(Evento.class));
    }

    // ─── TEST: cancelar ───────────────────────────────────────────────────────

    @Test
    void cancelar_cuandoEstaActivo_debeCancelar() {
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));
        when(eventoRepository.save(any(Evento.class))).thenReturn(evento);

        Evento resultado = eventoService.cancelar(1L);

        assertEquals("CANCELADO", resultado.getEstado());
        verify(eventoRepository, times(1)).save(any(Evento.class));
    }

    @Test
    void cancelar_cuandoYaEstaCancelado_debeLanzarBusinessException() {
        evento.setEstado("CANCELADO");
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));

        assertThrows(BusinessException.class,
            () -> eventoService.cancelar(1L));

        verify(eventoRepository, never()).save(any());
    }

    // ─── TEST: eliminar ───────────────────────────────────────────────────────

    @Test
    void eliminar_cuandoExiste_debeEliminar() {
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));
        doNothing().when(eventoRepository).deleteById(1L);

        eventoService.eliminar(1L);

        verify(eventoRepository, times(1)).deleteById(1L);
    }

    // ─── TEST: obtenerTodos ───────────────────────────────────────────────────

    @Test
    void obtenerTodos_debeRetornarListaDeEventos() {
        when(eventoRepository.findAll()).thenReturn(List.of(evento));

        List<Evento> resultado = eventoService.obtenerTodos();

        assertEquals(1, resultado.size());
        assertEquals("Karol G en Chile", resultado.get(0).getNombre());
    }
}