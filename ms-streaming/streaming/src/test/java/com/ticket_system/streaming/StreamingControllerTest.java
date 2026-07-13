package com.ticket_system.streaming;

import com.ticket_system.streaming.Assembler.AccesoStreamingAssembler;
import com.ticket_system.streaming.Assembler.StreamingAssembler;
import com.ticket_system.streaming.Controller.StreamingController;
import com.ticket_system.streaming.Exception.BusinessException;
import com.ticket_system.streaming.Exception.ResourceNotFoundException;
import com.ticket_system.streaming.Model.AccesoStreaming;
import com.ticket_system.streaming.Model.Streaming;
import com.ticket_system.streaming.Service.StreamingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StreamingController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({StreamingAssembler.class, AccesoStreamingAssembler.class})
class StreamingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StreamingService streamingService;

    private Streaming streaming;
    private AccesoStreaming acceso;

    @BeforeEach
    void setUp() {
        streaming = new Streaming();
        streaming.setId(1L);
        streaming.setEventoId(1L);
        streaming.setNombreStream("Karol G en Vivo");
        streaming.setUrlStream("https://stream.karolg.com/live");
        streaming.setFechaInicio(LocalDateTime.of(2026, 12, 15, 20, 0));
        streaming.setFechaFin(LocalDateTime.of(2026, 12, 15, 23, 0));
        streaming.setCapacidadMaxima(10000);
        streaming.setCapacidadDisponible(10000);
        streaming.setEstado("PROGRAMADO");
        streaming.setAccesos(null);

        acceso = new AccesoStreaming();
        acceso.setId(1L);
        acceso.setStreaming(streaming);
        acceso.setTicketId(1L);
        acceso.setNombreEspectador("Juan Perez");
        acceso.setEmailEspectador("juan@email.com");
        acceso.setCodigoAcceso("STR-123456");
        acceso.setEstadoAcceso("ACTIVO");
    }

    @Test
    void listarTodos_debeRetornarLista() throws Exception {
        when(streamingService.listarTodos()).thenReturn(List.of(streaming));

        mockMvc.perform(get("/api/streamings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    void listarTodos_cuandoVacia_debeRetornar200() throws Exception {
        when(streamingService.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/streamings"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarStreaming() throws Exception {
        when(streamingService.obtenerPorId(1L)).thenReturn(streaming);

        mockMvc.perform(get("/api/streamings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.eventoId").value(1))
                .andExpect(jsonPath("$.nombreStream").value("Karol G en Vivo"))
                .andExpect(jsonPath("$.urlStream").value("https://stream.karolg.com/live"))
                .andExpect(jsonPath("$.capacidadMaxima").value(10000))
                .andExpect(jsonPath("$.estado").value("PROGRAMADO"));
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeRetornar404() throws Exception {
        when(streamingService.obtenerPorId(99L))
                .thenThrow(new ResourceNotFoundException("Streaming no encontrado con id: 99"));

        mockMvc.perform(get("/api/streamings/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void listarPorEstado_debeRetornarLista() throws Exception {
        when(streamingService.listarPorEstado("PROGRAMADO")).thenReturn(List.of(streaming));

        mockMvc.perform(get("/api/streamings/estado/PROGRAMADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists());
    }

    @Test
    void listarPorEstado_cuandoVacia_debeRetornar200() throws Exception {
        when(streamingService.listarPorEstado("FINALIZADO")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/streamings/estado/FINALIZADO"))
                .andExpect(status().isOk());
    }

    @Test
    void crear_conDatosValidos_debeRetornar201() throws Exception {
        when(streamingService.crear(any())).thenReturn(streaming);

        mockMvc.perform(post("/api/streamings")
                        .contentType("application/json")
                        .content("{\"eventoId\":1,\"nombreStream\":\"Karol G en Vivo\",\"urlStream\":\"https://stream.karolg.com/live\",\"fechaInicio\":\"2026-12-15T20:00:00\",\"fechaFin\":\"2026-12-15T23:00:00\",\"capacidadMaxima\":10000}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.eventoId").value(1))
                .andExpect(jsonPath("$.nombreStream").value("Karol G en Vivo"))
                .andExpect(jsonPath("$.estado").value("PROGRAMADO"));
    }

    @Test
    void crear_conDatosInvalidos_debeRetornar400() throws Exception {
        mockMvc.perform(post("/api/streamings")
                        .contentType("application/json")
                        .content("{\"eventoId\":null,\"nombreStream\":\"\",\"urlStream\":\"\",\"fechaInicio\":null,\"fechaFin\":null,\"capacidadMaxima\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void actualizar_conDatosValidos_debeRetornarStreaming() throws Exception {
        when(streamingService.actualizar(eq(1L), any())).thenReturn(streaming);

        mockMvc.perform(put("/api/streamings/1")
                        .contentType("application/json")
                        .content("{\"eventoId\":1,\"nombreStream\":\"Karol G en Vivo\",\"urlStream\":\"https://stream.karolg.com/live\",\"fechaInicio\":\"2026-12-15T20:00:00\",\"fechaFin\":\"2026-12-15T23:00:00\",\"capacidadMaxima\":10000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombreStream").value("Karol G en Vivo"));
    }

    @Test
    void actualizar_conDatosInvalidos_debeRetornar400() throws Exception {
        mockMvc.perform(put("/api/streamings/1")
                        .contentType("application/json")
                        .content("{\"eventoId\":null,\"nombreStream\":\"\",\"urlStream\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void iniciar_cuandoProgramado_debeRetornarStreaming() throws Exception {
        Streaming enVivo = new Streaming();
        enVivo.setId(1L);
        enVivo.setEventoId(1L);
        enVivo.setNombreStream("Karol G en Vivo");
        enVivo.setUrlStream("https://stream.karolg.com/live");
        enVivo.setFechaInicio(LocalDateTime.of(2026, 12, 15, 20, 0));
        enVivo.setFechaFin(LocalDateTime.of(2026, 12, 15, 23, 0));
        enVivo.setCapacidadMaxima(10000);
        enVivo.setCapacidadDisponible(10000);
        enVivo.setEstado("EN_VIVO");
        enVivo.setAccesos(null);

        when(streamingService.iniciarStream(1L)).thenReturn(enVivo);

        mockMvc.perform(patch("/api/streamings/1/iniciar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("EN_VIVO"));
    }

    @Test
    void iniciar_cuandoNoProgramado_debeRetornar422() throws Exception {
        when(streamingService.iniciarStream(1L))
                .thenThrow(new BusinessException("Solo se pueden iniciar streamings en estado PROGRAMADO."));

        mockMvc.perform(patch("/api/streamings/1/iniciar"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Business Error"));
    }

    @Test
    void finalizar_cuandoEnVivo_debeRetornarStreaming() throws Exception {
        Streaming finalizado = new Streaming();
        finalizado.setId(1L);
        finalizado.setEventoId(1L);
        finalizado.setNombreStream("Karol G en Vivo");
        finalizado.setUrlStream("https://stream.karolg.com/live");
        finalizado.setFechaInicio(LocalDateTime.of(2026, 12, 15, 20, 0));
        finalizado.setFechaFin(LocalDateTime.of(2026, 12, 15, 23, 0));
        finalizado.setCapacidadMaxima(10000);
        finalizado.setCapacidadDisponible(10000);
        finalizado.setEstado("FINALIZADO");
        finalizado.setAccesos(null);

        when(streamingService.finalizarStream(1L)).thenReturn(finalizado);

        mockMvc.perform(patch("/api/streamings/1/finalizar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("FINALIZADO"));
    }

    @Test
    void finalizar_cuandoNoEnVivo_debeRetornar422() throws Exception {
        when(streamingService.finalizarStream(1L))
                .thenThrow(new BusinessException("Solo se pueden finalizar streamings EN_VIVO."));

        mockMvc.perform(patch("/api/streamings/1/finalizar"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void cancelar_cuandoNoFinalizado_debeRetornarStreaming() throws Exception {
        Streaming cancelado = new Streaming();
        cancelado.setId(1L);
        cancelado.setEventoId(1L);
        cancelado.setNombreStream("Karol G en Vivo");
        cancelado.setUrlStream("https://stream.karolg.com/live");
        cancelado.setFechaInicio(LocalDateTime.of(2026, 12, 15, 20, 0));
        cancelado.setFechaFin(LocalDateTime.of(2026, 12, 15, 23, 0));
        cancelado.setCapacidadMaxima(10000);
        cancelado.setCapacidadDisponible(10000);
        cancelado.setEstado("CANCELADO");
        cancelado.setAccesos(null);

        when(streamingService.cancelarStream(1L)).thenReturn(cancelado);

        mockMvc.perform(patch("/api/streamings/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("CANCELADO"));
    }

    @Test
    void cancelar_cuandoFinalizado_debeRetornar422() throws Exception {
        when(streamingService.cancelarStream(1L))
                .thenThrow(new BusinessException("No se puede cancelar un streaming ya finalizado."));

        mockMvc.perform(patch("/api/streamings/1/cancelar"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void obtenerAccesos_debeRetornarLista() throws Exception {
        when(streamingService.obtenerAccesosPorStreaming(1L)).thenReturn(List.of(acceso));

        mockMvc.perform(get("/api/streamings/1/accesos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    void obtenerAccesos_cuandoVacia_debeRetornar200() throws Exception {
        when(streamingService.obtenerAccesosPorStreaming(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/streamings/1/accesos"))
                .andExpect(status().isOk());
    }

    @Test
    void generarAcceso_conDatosValidos_debeRetornar201() throws Exception {
        when(streamingService.generarAcceso(eq(1L), any())).thenReturn(acceso);

        mockMvc.perform(post("/api/streamings/1/accesos")
                        .contentType("application/json")
                        .content("{\"ticketId\":1,\"nombreEspectador\":\"Juan Perez\",\"emailEspectador\":\"juan@email.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ticketId").value(1))
                .andExpect(jsonPath("$.nombreEspectador").value("Juan Perez"))
                .andExpect(jsonPath("$.codigoAcceso").value("STR-123456"))
                .andExpect(jsonPath("$.estadoAcceso").value("ACTIVO"));
    }

    @Test
    void generarAcceso_conDatosInvalidos_debeRetornar400() throws Exception {
        mockMvc.perform(post("/api/streamings/1/accesos")
                        .contentType("application/json")
                        .content("{\"ticketId\":null,\"nombreEspectador\":\"\",\"emailEspectador\":\"invalido\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void validarAcceso_cuandoActivo_debeRetornarAcceso() throws Exception {
        AccesoStreaming validado = new AccesoStreaming();
        validado.setId(1L);
        validado.setStreaming(streaming);
        validado.setTicketId(1L);
        validado.setNombreEspectador("Juan Perez");
        validado.setEmailEspectador("juan@email.com");
        validado.setCodigoAcceso("STR-123456");
        validado.setEstadoAcceso("USADO");

        when(streamingService.validarAcceso("STR-123456")).thenReturn(validado);

        mockMvc.perform(patch("/api/streamings/accesos/validar/STR-123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estadoAcceso").value("USADO"));
    }

    @Test
    void validarAcceso_cuandoNoActivo_debeRetornar422() throws Exception {
        when(streamingService.validarAcceso("STR-USED"))
                .thenThrow(new BusinessException("El código de acceso ya fue usado o está revocado."));

        mockMvc.perform(patch("/api/streamings/accesos/validar/STR-USED"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void revocarAcceso_cuandoActivo_debeRetornarAcceso() throws Exception {
        AccesoStreaming revocado = new AccesoStreaming();
        revocado.setId(1L);
        revocado.setStreaming(streaming);
        revocado.setTicketId(1L);
        revocado.setNombreEspectador("Juan Perez");
        revocado.setEmailEspectador("juan@email.com");
        revocado.setCodigoAcceso("STR-123456");
        revocado.setEstadoAcceso("REVOCADO");

        when(streamingService.revocarAcceso(1L)).thenReturn(revocado);

        mockMvc.perform(patch("/api/streamings/accesos/1/revocar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estadoAcceso").value("REVOCADO"));
    }

    @Test
    void revocarAcceso_cuandoUsado_debeRetornar422() throws Exception {
        when(streamingService.revocarAcceso(1L))
                .thenThrow(new BusinessException("No se puede revocar un acceso ya usado."));

        mockMvc.perform(patch("/api/streamings/accesos/1/revocar"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }
}
