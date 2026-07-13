package com.ticket_system.promotores;

import com.ticket_system.promotores.Assembler.ComisionPromotorAssembler;
import com.ticket_system.promotores.Assembler.PromotorAssembler;
import com.ticket_system.promotores.Controller.PromotorController;
import com.ticket_system.promotores.Exception.BusinessException;
import com.ticket_system.promotores.Exception.ResourceNotFoundException;
import com.ticket_system.promotores.Model.ComisionPromotor;
import com.ticket_system.promotores.Model.Promotor;
import com.ticket_system.promotores.Service.PromotorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PromotorController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({PromotorAssembler.class, ComisionPromotorAssembler.class})
class PromotorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PromotorService promotorService;

    private Promotor promotor;
    private ComisionPromotor comision;

    @BeforeEach
    void setUp() {
        promotor = new Promotor();
        promotor.setId(1L);
        promotor.setNombre("Promotor Test");
        promotor.setEmail("promotor@test.com");
        promotor.setTelefono("1234567890");
        promotor.setPorcentajeComision(10.0);
        promotor.setEstado("ACTIVO");
        promotor.setComisiones(null);

        comision = new ComisionPromotor();
        comision.setId(1L);
        comision.setPromotor(promotor);
        comision.setVentaId(1L);
        comision.setMontoVenta(100000.0);
        comision.setPorcentajeAplicado(10.0);
        comision.setMontoComision(10000.0);
        comision.setEstadoComision("PENDIENTE");
    }

    @Test
    void listarTodos_debeRetornarLista() throws Exception {
        when(promotorService.listarTodos()).thenReturn(List.of(promotor));

        mockMvc.perform(get("/api/promotores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    void listarTodos_cuandoVacia_debeRetornar200() throws Exception {
        when(promotorService.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/promotores"))
                .andExpect(status().isOk());
    }

    @Test
    void listarActivos_debeRetornarLista() throws Exception {
        when(promotorService.listarActivos()).thenReturn(List.of(promotor));

        mockMvc.perform(get("/api/promotores/activos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists());
    }

    @Test
    void listarActivos_cuandoVacia_debeRetornar200() throws Exception {
        when(promotorService.listarActivos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/promotores/activos"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarPromotor() throws Exception {
        when(promotorService.obtenerPorId(1L)).thenReturn(promotor);

        mockMvc.perform(get("/api/promotores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Promotor Test"))
                .andExpect(jsonPath("$.email").value("promotor@test.com"))
                .andExpect(jsonPath("$.telefono").value("1234567890"))
                .andExpect(jsonPath("$.porcentajeComision").value(10.0))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeRetornar404() throws Exception {
        when(promotorService.obtenerPorId(99L))
                .thenThrow(new ResourceNotFoundException("Promotor no encontrado con id: 99"));

        mockMvc.perform(get("/api/promotores/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void crear_conDatosValidos_debeRetornar201() throws Exception {
        when(promotorService.crear(any())).thenReturn(promotor);

        mockMvc.perform(post("/api/promotores")
                        .contentType("application/json")
                        .content("{\"nombre\":\"Promotor Test\",\"email\":\"promotor@test.com\",\"telefono\":\"1234567890\",\"porcentajeComision\":10.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Promotor Test"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    void crear_conDatosInvalidos_debeRetornar400() throws Exception {
        mockMvc.perform(post("/api/promotores")
                        .contentType("application/json")
                        .content("{\"nombre\":\"\",\"email\":\"invalido\",\"telefono\":\"\",\"porcentajeComision\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void actualizar_conDatosValidos_debeRetornarPromotor() throws Exception {
        when(promotorService.actualizar(eq(1L), any())).thenReturn(promotor);

        mockMvc.perform(put("/api/promotores/1")
                        .contentType("application/json")
                        .content("{\"nombre\":\"Promotor Test\",\"email\":\"promotor@test.com\",\"telefono\":\"1234567890\",\"porcentajeComision\":10.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Promotor Test"));
    }

    @Test
    void actualizar_conDatosInvalidos_debeRetornar400() throws Exception {
        mockMvc.perform(put("/api/promotores/1")
                        .contentType("application/json")
                        .content("{\"nombre\":\"\",\"email\":\"\",\"telefono\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void desactivar_cuandoSinComisionesPendientes_debeRetornar204() throws Exception {
        doNothing().when(promotorService).desactivar(1L);

        mockMvc.perform(delete("/api/promotores/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void desactivar_conComisionesPendientes_debeRetornar422() throws Exception {
        doThrow(new BusinessException("No se puede desactivar el promotor porque tiene comisiones pendientes."))
                .when(promotorService).desactivar(1L);

        mockMvc.perform(delete("/api/promotores/1"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Business Error"));
    }

    @Test
    void obtenerComisiones_debeRetornarLista() throws Exception {
        when(promotorService.obtenerComisionesPorPromotor(1L)).thenReturn(List.of(comision));

        mockMvc.perform(get("/api/promotores/1/comisiones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    void obtenerComisiones_cuandoVacia_debeRetornar200() throws Exception {
        when(promotorService.obtenerComisionesPorPromotor(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/promotores/1/comisiones"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerTotalPendiente_debeRetornarTotal() throws Exception {
        when(promotorService.obtenerTotalPendientePorPromotor(1L)).thenReturn(10000.0);

        mockMvc.perform(get("/api/promotores/1/comisiones/pendiente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promotorId").value(1))
                .andExpect(jsonPath("$.totalPendiente").value(10000.0))
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    void obtenerTotalPendiente_cuandoPromotorNoExiste_debeRetornar404() throws Exception {
        when(promotorService.obtenerTotalPendientePorPromotor(99L))
                .thenThrow(new ResourceNotFoundException("Promotor no encontrado con id: 99"));

        mockMvc.perform(get("/api/promotores/99/comisiones/pendiente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void registrarComision_conDatosValidos_debeRetornar201() throws Exception {
        when(promotorService.registrarComision(eq(1L), any())).thenReturn(comision);

        mockMvc.perform(post("/api/promotores/1/comisiones")
                        .contentType("application/json")
                        .content("{\"ventaId\":1,\"montoVenta\":100000.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ventaId").value(1))
                .andExpect(jsonPath("$.montoComision").value(10000.0))
                .andExpect(jsonPath("$.estadoComision").value("PENDIENTE"));
    }

    @Test
    void registrarComision_conDatosInvalidos_debeRetornar400() throws Exception {
        mockMvc.perform(post("/api/promotores/1/comisiones")
                        .contentType("application/json")
                        .content("{\"ventaId\":null,\"montoVenta\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void pagarComision_cuandoPendiente_debeRetornarComision() throws Exception {
        ComisionPromotor pagada = new ComisionPromotor();
        pagada.setId(1L);
        pagada.setPromotor(promotor);
        pagada.setVentaId(1L);
        pagada.setMontoVenta(100000.0);
        pagada.setPorcentajeAplicado(10.0);
        pagada.setMontoComision(10000.0);
        pagada.setEstadoComision("PAGADA");

        when(promotorService.pagarComision(1L)).thenReturn(pagada);

        mockMvc.perform(patch("/api/promotores/comisiones/1/pagar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estadoComision").value("PAGADA"));
    }

    @Test
    void pagarComision_cuandoNoPendiente_debeRetornar422() throws Exception {
        when(promotorService.pagarComision(1L))
                .thenThrow(new BusinessException("Solo se pueden pagar comisiones en estado PENDIENTE."));

        mockMvc.perform(patch("/api/promotores/comisiones/1/pagar"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Business Error"));
    }

    @Test
    void anularComision_cuandoPendiente_debeRetornarComision() throws Exception {
        ComisionPromotor anulada = new ComisionPromotor();
        anulada.setId(1L);
        anulada.setPromotor(promotor);
        anulada.setVentaId(1L);
        anulada.setMontoVenta(100000.0);
        anulada.setPorcentajeAplicado(10.0);
        anulada.setMontoComision(10000.0);
        anulada.setEstadoComision("ANULADA");

        when(promotorService.anularComision(1L)).thenReturn(anulada);

        mockMvc.perform(patch("/api/promotores/comisiones/1/anular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estadoComision").value("ANULADA"));
    }

    @Test
    void anularComision_cuandoPagada_debeRetornar422() throws Exception {
        when(promotorService.anularComision(1L))
                .thenThrow(new BusinessException("No se puede anular una comisión ya pagada."));

        mockMvc.perform(patch("/api/promotores/comisiones/1/anular"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }
}
