package com.ticket_system.artistas;

import com.ticket_system.Assembler.AgendaArtistaAssembler;
import com.ticket_system.Assembler.ArtistaAssembler;
import com.ticket_system.Controller.ArtistaController;
import com.ticket_system.DTO.AgendaDTO;
import com.ticket_system.DTO.ArtistaDTO;
import com.ticket_system.Exception.BusinessException;
import com.ticket_system.Exception.ResourceNotFoundException;
import com.ticket_system.Model.AgendaArtista;
import com.ticket_system.Model.Artista;
import com.ticket_system.Service.ArtistaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArtistaController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({ArtistaAssembler.class, AgendaArtistaAssembler.class})
class ArtistaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArtistaService artistaService;

    private Artista artista;
    private AgendaArtista agenda;

    @BeforeEach
    void setUp() {
        artista = new Artista();
        artista.setId(1L);
        artista.setNombre("Bad Bunny");
        artista.setGenero("Reggaeton");
        artista.setNacionalidad("Puerto Rico");
        artista.setEstado("ACTIVO");
        artista.setCreadoEn(LocalDateTime.of(2026, 1, 1, 12, 0));

        agenda = new AgendaArtista();
        agenda.setId(1L);
        agenda.setArtista(artista);
        agenda.setEventoId(10L);
        agenda.setNombreEvento("Festival Summer");
        agenda.setFechaPresentacion(LocalDateTime.of(2026, 8, 15, 20, 0));
        agenda.setLugar("Estadio Nacional");
        agenda.setEstadoAgenda("PENDIENTE");
        agenda.setCreadoEn(LocalDateTime.of(2026, 1, 1, 12, 0));
    }

    @Test
    void listarTodos_cuandoHayDatos_debeRetornar200() throws Exception {
        when(artistaService.listarTodos()).thenReturn(List.of(artista));

        mockMvc.perform(get("/api/artistas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").isMap())
                .andExpect(jsonPath("$._embedded..nombre").value("Bad Bunny"));
    }

    @Test
    void listarTodos_cuandoServicioFalla_debeRetornar500() throws Exception {
        when(artistaService.listarTodos()).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/artistas"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void listarActivos_cuandoHayDatos_debeRetornar200() throws Exception {
        when(artistaService.listarActivos()).thenReturn(List.of(artista));

        mockMvc.perform(get("/api/artistas/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").isMap())
                .andExpect(jsonPath("$._embedded..estado").value("ACTIVO"));
    }

    @Test
    void listarActivos_cuandoServicioFalla_debeRetornar500() throws Exception {
        when(artistaService.listarActivos()).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/artistas/activos"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornar200() throws Exception {
        when(artistaService.obtenerPorId(1L)).thenReturn(artista);

        mockMvc.perform(get("/api/artistas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Bad Bunny"))
                .andExpect(jsonPath("$.genero").value("Reggaeton"))
                .andExpect(jsonPath("$.nacionalidad").value("Puerto Rico"));
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeRetornar404() throws Exception {
        when(artistaService.obtenerPorId(99L))
                .thenThrow(new ResourceNotFoundException("Artista no encontrado con id: 99"));

        mockMvc.perform(get("/api/artistas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarPorGenero_cuandoHayDatos_debeRetornar200() throws Exception {
        when(artistaService.listarPorGenero("Reggaeton")).thenReturn(List.of(artista));

        mockMvc.perform(get("/api/artistas/genero/Reggaeton"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").isMap())
                .andExpect(jsonPath("$._embedded..genero").value("Reggaeton"));
    }

    @Test
    void listarPorGenero_cuandoServicioFalla_debeRetornar500() throws Exception {
        when(artistaService.listarPorGenero("Rock")).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/artistas/genero/Rock"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void crear_conDatosValidos_debeRetornar201() throws Exception {
        when(artistaService.crear(any(ArtistaDTO.class))).thenReturn(artista);

        mockMvc.perform(post("/api/artistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Bad Bunny\",\"genero\":\"Reggaeton\",\"nacionalidad\":\"Puerto Rico\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Bad Bunny"))
                .andExpect(jsonPath("$.genero").value("Reggaeton"));
    }

    @Test
    void crear_conDatosInvalidos_debeRetornar400() throws Exception {
        mockMvc.perform(post("/api/artistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Bad Bunny\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizar_conDatosValidos_debeRetornar200() throws Exception {
        when(artistaService.actualizar(eq(1L), any(ArtistaDTO.class))).thenReturn(artista);

        mockMvc.perform(put("/api/artistas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Bad Bunny\",\"genero\":\"Reggaeton\",\"nacionalidad\":\"Puerto Rico\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Bad Bunny"));
    }

    @Test
    void actualizar_cuandoNoExiste_debeRetornar404() throws Exception {
        when(artistaService.actualizar(eq(99L), any(ArtistaDTO.class)))
                .thenThrow(new ResourceNotFoundException("Artista no encontrado con id: 99"));

        mockMvc.perform(put("/api/artistas/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Bad Bunny\",\"genero\":\"Reggaeton\",\"nacionalidad\":\"Puerto Rico\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_cuandoExiste_debeRetornar204() throws Exception {
        doNothing().when(artistaService).eliminar(1L);

        mockMvc.perform(delete("/api/artistas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_cuandoNoExiste_debeRetornar404() throws Exception {
        doThrow(new ResourceNotFoundException("Artista no encontrado con id: 99"))
                .when(artistaService).eliminar(99L);

        mockMvc.perform(delete("/api/artistas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerAgenda_cuandoHayDatos_debeRetornar200() throws Exception {
        when(artistaService.obtenerAgendaPorArtista(1L)).thenReturn(List.of(agenda));

        mockMvc.perform(get("/api/artistas/1/agenda"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").isMap())
                .andExpect(jsonPath("$._embedded..nombreEvento").value("Festival Summer"));
    }

    @Test
    void obtenerAgenda_cuandoArtistaNoExiste_debeRetornar404() throws Exception {
        when(artistaService.obtenerAgendaPorArtista(99L))
                .thenThrow(new ResourceNotFoundException("Artista no encontrado con id: 99"));

        mockMvc.perform(get("/api/artistas/99/agenda"))
                .andExpect(status().isNotFound());
    }

    @Test
    void agregarAgenda_conDatosValidos_debeRetornar201() throws Exception {
        when(artistaService.agregarAgenda(eq(1L), any(AgendaDTO.class))).thenReturn(agenda);

        mockMvc.perform(post("/api/artistas/1/agenda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventoId\":10,\"nombreEvento\":\"Festival Summer\",\"fechaPresentacion\":\"2026-08-15T20:00:00\",\"lugar\":\"Estadio Nacional\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombreEvento").value("Festival Summer"))
                .andExpect(jsonPath("$.lugar").value("Estadio Nacional"));
    }

    @Test
    void agregarAgenda_conDatosInvalidos_debeRetornar400() throws Exception {
        mockMvc.perform(post("/api/artistas/1/agenda")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventoId\":10}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void confirmarAgenda_cuandoExiste_debeRetornar200() throws Exception {
        AgendaArtista confirmada = new AgendaArtista();
        confirmada.setId(1L);
        confirmada.setArtista(artista);
        confirmada.setEventoId(10L);
        confirmada.setNombreEvento("Festival Summer");
        confirmada.setEstadoAgenda("CONFIRMADO");

        when(artistaService.confirmarAgenda(1L)).thenReturn(confirmada);

        mockMvc.perform(patch("/api/artistas/agenda/1/confirmar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoAgenda").value("CONFIRMADO"));
    }

    @Test
    void confirmarAgenda_cuandoNoExiste_debeRetornar404() throws Exception {
        when(artistaService.confirmarAgenda(99L))
                .thenThrow(new ResourceNotFoundException("Agenda no encontrada con id: 99"));

        mockMvc.perform(patch("/api/artistas/agenda/99/confirmar"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelarAgenda_cuandoExiste_debeRetornar200() throws Exception {
        AgendaArtista cancelada = new AgendaArtista();
        cancelada.setId(1L);
        cancelada.setArtista(artista);
        cancelada.setEventoId(10L);
        cancelada.setNombreEvento("Festival Summer");
        cancelada.setEstadoAgenda("CANCELADO");

        when(artistaService.cancelarAgenda(1L)).thenReturn(cancelada);

        mockMvc.perform(patch("/api/artistas/agenda/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoAgenda").value("CANCELADO"));
    }

    @Test
    void cancelarAgenda_cuandoNoExiste_debeRetornar404() throws Exception {
        when(artistaService.cancelarAgenda(99L))
                .thenThrow(new ResourceNotFoundException("Agenda no encontrada con id: 99"));

        mockMvc.perform(patch("/api/artistas/agenda/99/cancelar"))
                .andExpect(status().isNotFound());
    }
}
