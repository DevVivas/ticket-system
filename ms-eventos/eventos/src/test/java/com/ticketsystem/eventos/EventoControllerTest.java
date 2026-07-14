package com.ticketsystem.eventos;

import com.ticketsystem.eventos.Assembler.EventoAssembler;
import com.ticketsystem.eventos.Controller.EventoController;
import com.ticketsystem.eventos.DTO.EventoDTO;
import com.ticketsystem.eventos.Exception.ResourceNotFoundException;
import com.ticketsystem.eventos.Model.Evento;
import com.ticketsystem.eventos.Service.EventoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(EventoAssembler.class)
class EventoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
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
        evento.setDescripcion("Concierto internacional");
        evento.setEstado("ACTIVO");

        eventoDTO = new EventoDTO();
        eventoDTO.setNombre("Karol G en Chile");
        eventoDTO.setTipo("CONCIERTO");
        eventoDTO.setFechaEvento(LocalDateTime.of(2026, 12, 15, 20, 0));
        eventoDTO.setLugar("Estadio Nacional");
        eventoDTO.setCapacidadTotal(5000);
        eventoDTO.setDescripcion("Concierto internacional");
    }

    @Test
    void getAll_hayEventos_debeRetornarLista() throws Exception {
        when(eventoService.obtenerTodos()).thenReturn(List.of(evento));

        mockMvc.perform(get("/api/eventos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.eventoList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.eventoList[0].nombre").value("Karol G en Chile"))
                .andExpect(jsonPath("$._embedded.eventoList[0].estado").value("ACTIVO"));
    }

    @Test
    void getAll_noHayEventos_debeRetornarListaVacia() throws Exception {
        when(eventoService.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/eventos"))
                .andExpect(status().isOk());
    }

    @Test
    void getById_existe_debeRetornarEvento() throws Exception {
        when(eventoService.obtenerPorId(1L)).thenReturn(evento);

        mockMvc.perform(get("/api/eventos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Karol G en Chile"))
                .andExpect(jsonPath("$.tipo").value("CONCIERTO"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    void getById_noExiste_debeRetornar404() throws Exception {
        when(eventoService.obtenerPorId(99L))
                .thenThrow(new ResourceNotFoundException("Evento no encontrado con id: 99"));

        mockMvc.perform(get("/api/eventos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_datosValidos_debeRetornar201() throws Exception {
        when(eventoService.crear(any(EventoDTO.class))).thenReturn(evento);

        mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Karol G en Chile\",\"tipo\":\"CONCIERTO\",\"fechaEvento\":\"2026-12-15T20:00:00\",\"lugar\":\"Estadio Nacional\",\"capacidadTotal\":5000,\"descripcion\":\"Concierto internacional\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Karol G en Chile"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    void create_datosInvalidos_debeRetornar400() throws Exception {
        mockMvc.perform(post("/api/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_existe_debeRetornarEvento() throws Exception {
        when(eventoService.actualizar(eq(1L), any(EventoDTO.class))).thenReturn(evento);

        mockMvc.perform(put("/api/eventos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Karol G en Chile\",\"tipo\":\"CONCIERTO\",\"fechaEvento\":\"2026-12-15T20:00:00\",\"lugar\":\"Estadio Nacional\",\"capacidadTotal\":5000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Karol G en Chile"));
    }

    @Test
    void update_noExiste_debeRetornar404() throws Exception {
        when(eventoService.actualizar(eq(99L), any(EventoDTO.class)))
                .thenThrow(new ResourceNotFoundException("Evento no encontrado"));

        mockMvc.perform(put("/api/eventos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Karol G en Chile\",\"tipo\":\"CONCIERTO\",\"fechaEvento\":\"2026-12-15T20:00:00\",\"lugar\":\"Estadio Nacional\",\"capacidadTotal\":5000}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_existe_debeRetornar204() throws Exception {
        doNothing().when(eventoService).eliminar(1L);

        mockMvc.perform(delete("/api/eventos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_noExiste_debeRetornar404() throws Exception {
        doThrow(new ResourceNotFoundException("Evento no encontrado")).when(eventoService).eliminar(99L);

        mockMvc.perform(delete("/api/eventos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelar_activo_debeCancelar() throws Exception {
        Evento cancelado = new Evento();
        cancelado.setId(1L);
        cancelado.setNombre("Karol G en Chile");
        cancelado.setTipo("CONCIERTO");
        cancelado.setFechaEvento(LocalDateTime.of(2026, 12, 15, 20, 0));
        cancelado.setLugar("Estadio Nacional");
        cancelado.setCapacidadTotal(5000);
        cancelado.setEstado("CANCELADO");
        when(eventoService.cancelar(1L)).thenReturn(cancelado);

        mockMvc.perform(patch("/api/eventos/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADO"));
    }

    @Test
    void cancelar_noExiste_debeRetornar404() throws Exception {
        when(eventoService.cancelar(99L))
                .thenThrow(new ResourceNotFoundException("Evento no encontrado"));

        mockMvc.perform(patch("/api/eventos/99/cancelar"))
                .andExpect(status().isNotFound());
    }
}
