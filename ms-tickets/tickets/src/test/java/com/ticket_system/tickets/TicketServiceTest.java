package com.ticket_system.tickets;

import com.ticket_system.tickets.DTO.TicketDTO;
import com.ticket_system.tickets.Exception.BusinessException;
import com.ticket_system.tickets.Exception.ResourceNotFoundException;
import com.ticket_system.tickets.Model.Ticket;
import com.ticket_system.tickets.Repository.TicketRepository;
import com.ticket_system.tickets.Service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketService ticketService;

    private Ticket ticket;
    private TicketDTO ticketDTO;

    @BeforeEach
    void setUp() {
        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setEventoId(1L);
        ticket.setSectorId(1L);
        ticket.setCompradorId(1L);
        ticket.setPrecio(50000.0);
        ticket.setEstado("DISPONIBLE");
        ticket.setCodigoUnico("ABC-123");
        ticket.setCodigoQR("QR-ABC-123");

        ticketDTO = new TicketDTO();
        ticketDTO.setEventoId(1L);
        ticketDTO.setSectorId(1L);
        ticketDTO.setCompradorId(1L);
        ticketDTO.setPrecio(50000.0);
    }

    // ─── TEST: obtenerPorId ───────────────────────────────────────────────────

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarTicket() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        Ticket resultado = ticketService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("DISPONIBLE", resultado.getEstado());
        verify(ticketRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeLanzarResourceNotFoundException() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> ticketService.obtenerPorId(99L));
    }

    // ─── TEST: generarTicket ──────────────────────────────────────────────────

    @Test
    void generarTicket_debeCrearTicketConEstadoDisponible() {
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket resultado = ticketService.generarTicket(ticketDTO);

        assertNotNull(resultado);
        assertEquals("DISPONIBLE", resultado.getEstado());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    // ─── TEST: marcarVendido ──────────────────────────────────────────────────

    @Test
    void marcarVendido_cuandoEstaDisponible_debeMarcarVendido() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket resultado = ticketService.marcarVendido(1L);

        assertEquals("VENDIDO", resultado.getEstado());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void marcarVendido_cuandoNoEstaDisponible_debeLanzarBusinessException() {
        ticket.setEstado("VENDIDO");
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        assertThrows(BusinessException.class,
            () -> ticketService.marcarVendido(1L));

        verify(ticketRepository, never()).save(any());
    }

    // ─── TEST: anular ─────────────────────────────────────────────────────────

    @Test
    void anular_cuandoNoEstaUsado_debeAnular() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket resultado = ticketService.anular(1L);

        assertEquals("ANULADO", resultado.getEstado());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void anular_cuandoEstaUsado_debeLanzarBusinessException() {
        ticket.setEstado("USADO");
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        assertThrows(BusinessException.class,
            () -> ticketService.anular(1L));

        verify(ticketRepository, never()).save(any());
    }

    // ─── TEST: obtenerPorEvento ───────────────────────────────────────────────

    @Test
    void obtenerPorEvento_debeRetornarTicketsDelEvento() {
        when(ticketRepository.findByEventoId(1L)).thenReturn(List.of(ticket));

        List<Ticket> resultado = ticketService.obtenerPorEvento(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getEventoId());
    }
}