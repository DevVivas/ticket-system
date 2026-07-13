package com.ticket_system.preventa;

import com.ticket_system.preventa.Assembler.CodigoBeneficioAssembler;
import com.ticket_system.preventa.Controller.PreventaController;
import com.ticket_system.preventa.DTO.CodigoBeneficioDTO;
import com.ticket_system.preventa.Model.CodigoBeneficio;
import com.ticket_system.preventa.Service.PreventaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PreventaController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CodigoBeneficioAssembler.class)
class PreventaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PreventaService preventaService;

    private CodigoBeneficio codigo;

    @BeforeEach
    void setUp() {
        codigo = new CodigoBeneficio();
        codigo.setId(1L);
        codigo.setCodigo("PROMO2026");
        codigo.setTipo("DESCUENTO");
        codigo.setPorcentajeDescuento(20.0);
        codigo.setUsoMaximo(100);
        codigo.setUsoActual(0);
        codigo.setEventoId(1L);
        codigo.setActivo(true);
        codigo.setFechaExpiracion(LocalDateTime.of(2026, 12, 31, 23, 59));
    }

    @Test
    void getAll_cuandoHayDatos_debeRetornar200() throws Exception {
        when(preventaService.obtenerTodos()).thenReturn(List.of(codigo));

        mockMvc.perform(get("/api/preventa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").isMap())
                .andExpect(jsonPath("$._embedded..codigo").value("PROMO2026"));
    }

    @Test
    void getAll_cuandoServicioFalla_debeRetornar500() throws Exception {
        when(preventaService.obtenerTodos()).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/preventa"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getById_cuandoExiste_debeRetornar200() throws Exception {
        when(preventaService.obtenerPorId(1L)).thenReturn(Optional.of(codigo));

        mockMvc.perform(get("/api/preventa/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigo").value("PROMO2026"))
                .andExpect(jsonPath("$.tipo").value("DESCUENTO"));
    }

    @Test
    void getById_cuandoNoExiste_debeRetornar404() throws Exception {
        when(preventaService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/preventa/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_conDatosValidos_debeRetornar201() throws Exception {
        when(preventaService.crear(any(CodigoBeneficioDTO.class))).thenReturn(codigo);

        mockMvc.perform(post("/api/preventa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"PROMO2026\",\"tipo\":\"DESCUENTO\",\"porcentajeDescuento\":20.0,\"usoMaximo\":100,\"eventoId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigo").value("PROMO2026"))
                .andExpect(jsonPath("$.tipo").value("DESCUENTO"));
    }

    @Test
    void create_conDatosInvalidos_debeRetornar400() throws Exception {
        mockMvc.perform(post("/api/preventa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"DESCUENTO\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_conDatosValidos_debeRetornar200() throws Exception {
        when(preventaService.actualizar(eq(1L), any(CodigoBeneficioDTO.class))).thenReturn(codigo);

        mockMvc.perform(put("/api/preventa/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"PROMO2026\",\"tipo\":\"DESCUENTO\",\"porcentajeDescuento\":20.0,\"usoMaximo\":100,\"eventoId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.codigo").value("PROMO2026"));
    }

    @Test
    void update_cuandoNoExiste_debeRetornar404() throws Exception {
        when(preventaService.actualizar(eq(99L), any(CodigoBeneficioDTO.class)))
                .thenThrow(new RuntimeException("Codigo no encontrado"));

        mockMvc.perform(put("/api/preventa/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"PROMO2026\",\"tipo\":\"DESCUENTO\",\"porcentajeDescuento\":20.0,\"usoMaximo\":100,\"eventoId\":1}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_cuandoExiste_debeRetornar204() throws Exception {
        doNothing().when(preventaService).eliminar(1L);

        mockMvc.perform(delete("/api/preventa/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_cuandoNoExiste_debeRetornar404() throws Exception {
        doThrow(new RuntimeException("Codigo no encontrado"))
                .when(preventaService).eliminar(99L);

        mockMvc.perform(delete("/api/preventa/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void validar_codigoValido_debeRetornar200() throws Exception {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("valido", true);
        resultado.put("descuento", 20.0);
        resultado.put("tipo", "DESCUENTO");
        resultado.put("mensaje", "Codigo aplicado correctamente");

        when(preventaService.validarCodigo("PROMO2026")).thenReturn(resultado);

        mockMvc.perform(post("/api/preventa/validar").param("codigo", "PROMO2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(true))
                .andExpect(jsonPath("$.descuento").value(20.0))
                .andExpect(jsonPath("$.tipo").value("DESCUENTO"));
    }

    @Test
    void validar_codigoInvalido_debeRetornar404() throws Exception {
        when(preventaService.validarCodigo("INVALIDO"))
                .thenThrow(new RuntimeException("Codigo no valido"));

        mockMvc.perform(post("/api/preventa/validar").param("codigo", "INVALIDO"))
                .andExpect(status().isNotFound());
    }

    @Test
    void desactivar_cuandoExiste_debeRetornar200() throws Exception {
        CodigoBeneficio desactivado = new CodigoBeneficio();
        desactivado.setId(1L);
        desactivado.setCodigo("PROMO2026");
        desactivado.setTipo("DESCUENTO");
        desactivado.setActivo(false);

        when(preventaService.desactivar(1L)).thenReturn(desactivado);

        mockMvc.perform(patch("/api/preventa/1/desactivar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false))
                .andExpect(jsonPath("$.codigo").value("PROMO2026"));
    }

    @Test
    void desactivar_cuandoNoExiste_debeRetornar404() throws Exception {
        when(preventaService.desactivar(99L))
                .thenThrow(new RuntimeException("Codigo no encontrado"));

        mockMvc.perform(patch("/api/preventa/99/desactivar"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByEvento_cuandoHayDatos_debeRetornar200() throws Exception {
        when(preventaService.obtenerPorEvento(1L)).thenReturn(List.of(codigo));

        mockMvc.perform(get("/api/preventa/evento/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").isMap())
                .andExpect(jsonPath("$._embedded..eventoId").value(1));
    }

    @Test
    void getByEvento_cuandoServicioFalla_debeRetornar500() throws Exception {
        when(preventaService.obtenerPorEvento(99L))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/api/preventa/evento/99"))
                .andExpect(status().isInternalServerError());
    }
}
