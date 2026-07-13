package com.ticket_system.validacion;

import com.ticket_system.validacion.Assembler.SesionValidacionAssembler;
import com.ticket_system.validacion.Assembler.ValidacionTicketAssembler;
import com.ticket_system.validacion.Controller.ValidacionController;
import com.ticket_system.validacion.DTO.SesionDTO;
import com.ticket_system.validacion.DTO.ValidacionDTO;
import com.ticket_system.validacion.Exception.BusinessException;
import com.ticket_system.validacion.Exception.ResourceNotFoundException;
import com.ticket_system.validacion.Model.SesionValidacion;
import com.ticket_system.validacion.Model.ValidacionTicket;
import com.ticket_system.validacion.Service.ValidacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ValidacionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({SesionValidacionAssembler.class, ValidacionTicketAssembler.class})
class ValidacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ValidacionService validacionService;

    private SesionValidacion sesion;
    private ValidacionTicket validacion;

    @BeforeEach
    void setUp() {
        sesion = new SesionValidacion();
        sesion.setId(1L);
        sesion.setEventoId(10L);
        sesion.setNombrePortero("Juan Perez");
        sesion.setPuestoAcceso("Puerta Principal");
        sesion.setEstado("ACTIVA");
        sesion.setTotalEscaneados(5);
        sesion.setIniciadaEn(LocalDateTime.of(2026, 7, 12, 10, 0));

        validacion = new ValidacionTicket();
        validacion.setId(1L);
        validacion.setSesion(sesion);
        validacion.setCodigoQR("QR-12345");
        validacion.setTicketId(100L);
        validacion.setResultado("VALIDO");
        validacion.setDetalleResultado("Ticket valido. Acceso permitido.");
        validacion.setEscaneadoEn(LocalDateTime.of(2026, 7, 12, 10, 30));
    }

    @Test
    void listarSesiones_cuandoHayDatos_debeRetornar200() throws Exception {
        when(validacionService.listarSesiones()).thenReturn(List.of(sesion));

        mockMvc.perform(get("/api/validacion/sesiones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").isMap())
                .andExpect(jsonPath("$._embedded..eventoId").value(10));
    }

    @Test
    void listarSesiones_cuandoServicioFalla_debeRetornar500() throws Exception {
        when(validacionService.listarSesiones()).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/validacion/sesiones"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void obtenerSesion_cuandoExiste_debeRetornar200() throws Exception {
        when(validacionService.obtenerSesionPorId(1L)).thenReturn(sesion);

        mockMvc.perform(get("/api/validacion/sesiones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.eventoId").value(10))
                .andExpect(jsonPath("$.nombrePortero").value("Juan Perez"));
    }

    @Test
    void obtenerSesion_cuandoNoExiste_debeRetornar404() throws Exception {
        when(validacionService.obtenerSesionPorId(99L))
                .thenThrow(new ResourceNotFoundException("Sesion no encontrada con id: 99"));

        mockMvc.perform(get("/api/validacion/sesiones/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarPorEvento_cuandoHayDatos_debeRetornar200() throws Exception {
        when(validacionService.listarSesionesPorEvento(10L)).thenReturn(List.of(sesion));

        mockMvc.perform(get("/api/validacion/sesiones/evento/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").isMap())
                .andExpect(jsonPath("$._embedded..eventoId").value(10));
    }

    @Test
    void listarPorEvento_cuandoServicioFalla_debeRetornar500() throws Exception {
        when(validacionService.listarSesionesPorEvento(10L))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/validacion/sesiones/evento/10"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void abrirSesion_conDatosValidos_debeRetornar201() throws Exception {
        when(validacionService.abrirSesion(any(SesionDTO.class))).thenReturn(sesion);

        mockMvc.perform(post("/api/validacion/sesiones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventoId\":10,\"nombrePortero\":\"Juan Perez\",\"puestoAcceso\":\"Puerta Principal\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.eventoId").value(10))
                .andExpect(jsonPath("$.nombrePortero").value("Juan Perez"));
    }

    @Test
    void abrirSesion_conDatosInvalidos_debeRetornar400() throws Exception {
        mockMvc.perform(post("/api/validacion/sesiones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventoId\":10}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cerrarSesion_cuandoExiste_debeRetornar200() throws Exception {
        SesionValidacion cerrada = new SesionValidacion();
        cerrada.setId(1L);
        cerrada.setEventoId(10L);
        cerrada.setNombrePortero("Juan Perez");
        cerrada.setPuestoAcceso("Puerta Principal");
        cerrada.setEstado("CERRADA");
        cerrada.setTotalEscaneados(5);
        cerrada.setCerradaEn(LocalDateTime.now());

        when(validacionService.cerrarSesion(1L)).thenReturn(cerrada);

        mockMvc.perform(patch("/api/validacion/sesiones/1/cerrar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CERRADA"));
    }

    @Test
    void cerrarSesion_cuandoNoExiste_debeRetornar404() throws Exception {
        when(validacionService.cerrarSesion(99L))
                .thenThrow(new ResourceNotFoundException("Sesion no encontrada con id: 99"));

        mockMvc.perform(patch("/api/validacion/sesiones/99/cerrar"))
                .andExpect(status().isNotFound());
    }

    @Test
    void escanear_conTicketValido_debeRetornar201() throws Exception {
        when(validacionService.escanearTicket(eq(1L), any(ValidacionDTO.class))).thenReturn(validacion);

        mockMvc.perform(post("/api/validacion/sesiones/1/escanear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoQR\":\"QR-12345\",\"ticketId\":100}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultado").value("VALIDO"))
                .andExpect(jsonPath("$.ticketId").value(100))
                .andExpect(jsonPath("$.codigoQR").value("QR-12345"));
    }

    @Test
    void escanear_conDatosInvalidos_debeRetornar400() throws Exception {
        mockMvc.perform(post("/api/validacion/sesiones/1/escanear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ticketId\":100}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerValidaciones_cuandoHayDatos_debeRetornar200() throws Exception {
        when(validacionService.obtenerValidacionesPorSesion(1L)).thenReturn(List.of(validacion));

        mockMvc.perform(get("/api/validacion/sesiones/1/validaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").isMap())
                .andExpect(jsonPath("$._embedded..resultado").value("VALIDO"));
    }

    @Test
    void obtenerValidaciones_cuandoSesionNoExiste_debeRetornar404() throws Exception {
        when(validacionService.obtenerValidacionesPorSesion(99L))
                .thenThrow(new ResourceNotFoundException("Sesion no encontrada con id: 99"));

        mockMvc.perform(get("/api/validacion/sesiones/99/validaciones"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarPorResultado_cuandoHayDatos_debeRetornar200() throws Exception {
        when(validacionService.listarPorResultado("VALIDO")).thenReturn(List.of(validacion));

        mockMvc.perform(get("/api/validacion/resultado/VALIDO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").isMap())
                .andExpect(jsonPath("$._embedded..resultado").value("VALIDO"));
    }

    @Test
    void listarPorResultado_cuandoServicioFalla_debeRetornar500() throws Exception {
        when(validacionService.listarPorResultado("INVALIDO"))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/validacion/resultado/INVALIDO"))
                .andExpect(status().isInternalServerError());
    }
}
