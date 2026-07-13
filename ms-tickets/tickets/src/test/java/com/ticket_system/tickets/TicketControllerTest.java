package com.ticket_system.tickets;

import com.ticket_system.tickets.Assembler.TicketAssembler;
import com.ticket_system.tickets.Controller.TicketController;
import com.ticket_system.tickets.DTO.TicketDTO;
import com.ticket_system.tickets.Exception.BusinessException;
import com.ticket_system.tickets.Exception.ResourceNotFoundException;
import com.ticket_system.tickets.Model.Ticket;
import com.ticket_system.tickets.Service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TicketAssembler.class)
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TicketService ticketService;

    private Ticket ticket;
    private TicketDTO ticketDTO;

    @BeforeEach
    void setUp() {
        ticket = new Ticket();
        ticket.setId(1L);
        ticket.setCodigoUnico("TK-001-UUID");
        ticket.setEventoId(1L);
        ticket.setSectorId(1L);
        ticket.setCompradorId(1L);
        ticket.setPrecio(50000.0);
        ticket.setEstado("DISPONIBLE");
        ticket.setCodigoQR("QR-TK-001");

        ticketDTO = new TicketDTO();
        ticketDTO.setEventoId(1L);
        ticketDTO.setSectorId(1L);
        ticketDTO.setCompradorId(1L);
        ticketDTO.setPrecio(50000.0);
    }

    @Test
    void getAll_hayTickets_debeRetornarLista() throws Exception {
        when(ticketService.obtenerTodos()).thenReturn(List.of(ticket));

        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.tickets", hasSize(1)))
                .andExpect(jsonPath("$._embedded.tickets[0].codigoUnico").value("TK-001-UUID"));
    }

    @Test
    void getAll_noHayTickets_debeRetornarListaVacia() throws Exception {
        when(ticketService.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/tickets"))
                .andExpect(status().isOk());
    }

    @Test
    void getById_existe_debeRetornarTicket() throws Exception {
        when(ticketService.obtenerPorId(1L)).thenReturn(ticket);

        mockMvc.perform(get("/api/tickets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoUnico").value("TK-001-UUID"))
                .andExpect(jsonPath("$.estado").value("DISPONIBLE"));
    }

    @Test
    void getById_noExiste_debeRetornar404() throws Exception {
        when(ticketService.obtenerPorId(99L))
                .thenThrow(new ResourceNotFoundException("Ticket no encontrado con id: 99"));

        mockMvc.perform(get("/api/tickets/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByCodigo_existe_debeRetornarTicket() throws Exception {
        when(ticketService.obtenerPorCodigo("TK-001-UUID")).thenReturn(ticket);

        mockMvc.perform(get("/api/tickets/codigo/TK-001-UUID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoUnico").value("TK-001-UUID"));
    }

    @Test
    void getByCodigo_noExiste_debeRetornar404() throws Exception {
        when(ticketService.obtenerPorCodigo("INVALID-CODE"))
                .thenThrow(new ResourceNotFoundException("Ticket no encontrado con codigo: INVALID-CODE"));

        mockMvc.perform(get("/api/tickets/codigo/INVALID-CODE"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByEvento_existe_debeRetornarLista() throws Exception {
        when(ticketService.obtenerPorEvento(1L)).thenReturn(List.of(ticket));

        mockMvc.perform(get("/api/tickets/evento/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.tickets", hasSize(1)))
                .andExpect(jsonPath("$._embedded.tickets[0].eventoId").value(1));
    }

    @Test
    void getByEvento_noHayTickets_debeRetornarListaVacia() throws Exception {
        when(ticketService.obtenerPorEvento(99L)).thenReturn(List.of());

        mockMvc.perform(get("/api/tickets/evento/99"))
                .andExpect(status().isOk());
    }

    @Test
    void getByComprador_existe_debeRetornarLista() throws Exception {
        when(ticketService.obtenerPorComprador(1L)).thenReturn(List.of(ticket));

        mockMvc.perform(get("/api/tickets/comprador/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.tickets", hasSize(1)))
                .andExpect(jsonPath("$._embedded.tickets[0].compradorId").value(1));
    }

    @Test
    void getByComprador_noHayTickets_debeRetornarListaVacia() throws Exception {
        when(ticketService.obtenerPorComprador(99L)).thenReturn(List.of());

        mockMvc.perform(get("/api/tickets/comprador/99"))
                .andExpect(status().isOk());
    }

    @Test
    void generate_datosValidos_debeRetornar201() throws Exception {
        when(ticketService.generarTicket(any(TicketDTO.class))).thenReturn(ticket);

        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventoId\":1,\"sectorId\":1,\"compradorId\":1,\"precio\":50000.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigoUnico").value("TK-001-UUID"))
                .andExpect(jsonPath("$.estado").value("DISPONIBLE"));
    }

    @Test
    void generate_datosInvalidos_debeRetornar400() throws Exception {
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void vender_disponible_debeMarcarVendido() throws Exception {
        Ticket vendido = new Ticket();
        vendido.setId(1L);
        vendido.setCodigoUnico("TK-001-UUID");
        vendido.setEstado("VENDIDO");
        when(ticketService.marcarVendido(1L)).thenReturn(vendido);

        mockMvc.perform(put("/api/tickets/1/vender"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("VENDIDO"));
    }

    @Test
    void vender_noDisponible_debeRetornar400() throws Exception {
        when(ticketService.marcarVendido(1L))
                .thenThrow(new BusinessException("Solo se pueden vender tickets con estado DISPONIBLE"));

        mockMvc.perform(put("/api/tickets/1/vender"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void usar_vendido_debeMarcarUsado() throws Exception {
        Ticket usado = new Ticket();
        usado.setId(1L);
        usado.setCodigoUnico("TK-001-UUID");
        usado.setEstado("USADO");
        when(ticketService.marcarUsado(1L)).thenReturn(usado);

        mockMvc.perform(put("/api/tickets/1/usar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("USADO"));
    }

    @Test
    void usar_noVendido_debeRetornar400() throws Exception {
        when(ticketService.marcarUsado(1L))
                .thenThrow(new BusinessException("Solo se pueden usar tickets con estado VENDIDO"));

        mockMvc.perform(put("/api/tickets/1/usar"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void anular_noUsado_debeAnular() throws Exception {
        Ticket anulado = new Ticket();
        anulado.setId(1L);
        anulado.setCodigoUnico("TK-001-UUID");
        anulado.setEstado("ANULADO");
        when(ticketService.anular(1L)).thenReturn(anulado);

        mockMvc.perform(put("/api/tickets/1/anular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ANULADO"));
    }

    @Test
    void anular_usado_debeRetornar400() throws Exception {
        when(ticketService.anular(1L))
                .thenThrow(new BusinessException("No se puede anular un ticket que ya fue usado."));

        mockMvc.perform(put("/api/tickets/1/anular"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_existe_debeRetornar204() throws Exception {
        doNothing().when(ticketService).eliminar(1L);

        mockMvc.perform(delete("/api/tickets/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_noExiste_debeRetornar404() throws Exception {
        doThrow(new ResourceNotFoundException("Ticket no encontrado")).when(ticketService).eliminar(99L);

        mockMvc.perform(delete("/api/tickets/99"))
                .andExpect(status().isNotFound());
    }
}
