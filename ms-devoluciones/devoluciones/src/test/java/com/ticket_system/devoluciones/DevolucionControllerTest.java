package com.ticket_system.devoluciones;

import com.ticket_system.Assembler.DevolucionAssembler;
import com.ticket_system.Assembler.ReembolsoDevolucionAssembler;
import com.ticket_system.Controller.DevolucionController;
import com.ticket_system.Exception.BusinessException;
import com.ticket_system.Exception.ResourceNotFoundException;
import com.ticket_system.Model.Devolucion;
import com.ticket_system.Model.ReembolsoDevolucion;
import com.ticket_system.Service.DevolucionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DevolucionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({DevolucionAssembler.class, ReembolsoDevolucionAssembler.class})
class DevolucionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DevolucionService devolucionService;

    private Devolucion devolucion;
    private ReembolsoDevolucion reembolso;

    @BeforeEach
    void setUp() {
        devolucion = new Devolucion();
        devolucion.setId(1L);
        devolucion.setVentaId(1L);
        devolucion.setTicketId(1L);
        devolucion.setMotivo("Evento cancelado");
        devolucion.setMontoDevolucion(50000.0);
        devolucion.setTipoDevolucion("EVENTO_CANCELADO");
        devolucion.setEstado("PENDIENTE");
        devolucion.setReembolsos(null);

        reembolso = new ReembolsoDevolucion();
        reembolso.setId(1L);
        reembolso.setDevolucion(devolucion);
        reembolso.setMontoReembolso(50000.0);
        reembolso.setMetodoReembolso("TRANSFERENCIA");
        reembolso.setEstadoReembolso("PENDIENTE");
        reembolso.setReferenciaBancaria("REF-001");
    }

    @Test
    void listarTodas_debeRetornarLista() throws Exception {
        when(devolucionService.listarTodas()).thenReturn(List.of(devolucion));

        mockMvc.perform(get("/api/devoluciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    void listarTodas_cuandoVacia_debeRetornar200() throws Exception {
        when(devolucionService.listarTodas()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/devoluciones"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarDevolucion() throws Exception {
        when(devolucionService.obtenerPorId(1L)).thenReturn(devolucion);

        mockMvc.perform(get("/api/devoluciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ventaId").value(1))
                .andExpect(jsonPath("$.ticketId").value(1))
                .andExpect(jsonPath("$.motivo").value("Evento cancelado"))
                .andExpect(jsonPath("$.montoDevolucion").value(50000.0))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeRetornar404() throws Exception {
        when(devolucionService.obtenerPorId(99L))
                .thenThrow(new ResourceNotFoundException("Devolución no encontrada con id: 99"));

        mockMvc.perform(get("/api/devoluciones/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void listarPorEstado_debeRetornarLista() throws Exception {
        when(devolucionService.listarPorEstado("PENDIENTE")).thenReturn(List.of(devolucion));

        mockMvc.perform(get("/api/devoluciones/estado/PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists());
    }

    @Test
    void listarPorEstado_cuandoVacia_debeRetornar200() throws Exception {
        when(devolucionService.listarPorEstado("COMPLETADA")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/devoluciones/estado/COMPLETADA"))
                .andExpect(status().isOk());
    }

    @Test
    void listarPorVenta_debeRetornarLista() throws Exception {
        when(devolucionService.listarPorVenta(1L)).thenReturn(List.of(devolucion));

        mockMvc.perform(get("/api/devoluciones/venta/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists());
    }

    @Test
    void listarPorVenta_cuandoVacia_debeRetornar200() throws Exception {
        when(devolucionService.listarPorVenta(99L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/devoluciones/venta/99"))
                .andExpect(status().isOk());
    }

    @Test
    void crear_conDatosValidos_debeRetornar201() throws Exception {
        when(devolucionService.crear(any())).thenReturn(devolucion);

        mockMvc.perform(post("/api/devoluciones")
                        .contentType("application/json")
                        .content("{\"ventaId\":1,\"ticketId\":1,\"motivo\":\"Evento cancelado\",\"montoDevolucion\":50000.0,\"tipoDevolucion\":\"EVENTO_CANCELADO\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ventaId").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void crear_conDatosInvalidos_debeRetornar400() throws Exception {
        mockMvc.perform(post("/api/devoluciones")
                        .contentType("application/json")
                        .content("{\"ventaId\":null,\"ticketId\":null,\"motivo\":\"\",\"montoDevolucion\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void aprobar_cuandoPendiente_debeRetornarDevolucion() throws Exception {
        Devolucion aprobada = new Devolucion();
        aprobada.setId(1L);
        aprobada.setVentaId(1L);
        aprobada.setTicketId(1L);
        aprobada.setMotivo("Evento cancelado");
        aprobada.setMontoDevolucion(50000.0);
        aprobada.setTipoDevolucion("EVENTO_CANCELADO");
        aprobada.setEstado("APROBADA");
        aprobada.setReembolsos(null);

        when(devolucionService.aprobar(1L)).thenReturn(aprobada);

        mockMvc.perform(patch("/api/devoluciones/1/aprobar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("APROBADA"));
    }

    @Test
    void aprobar_cuandoNoPendiente_debeRetornar422() throws Exception {
        when(devolucionService.aprobar(1L))
                .thenThrow(new BusinessException("Solo se pueden aprobar devoluciones en estado PENDIENTE."));

        mockMvc.perform(patch("/api/devoluciones/1/aprobar"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Business Error"));
    }

    @Test
    void rechazar_conMotivo_debeRetornarDevolucion() throws Exception {
        Devolucion rechazada = new Devolucion();
        rechazada.setId(1L);
        rechazada.setVentaId(1L);
        rechazada.setTicketId(1L);
        rechazada.setMotivo("Evento cancelado | Rechazo: No aplica");
        rechazada.setMontoDevolucion(50000.0);
        rechazada.setTipoDevolucion("EVENTO_CANCELADO");
        rechazada.setEstado("RECHAZADA");
        rechazada.setReembolsos(null);

        when(devolucionService.rechazar(eq(1L), eq("No aplica"))).thenReturn(rechazada);

        mockMvc.perform(patch("/api/devoluciones/1/rechazar")
                        .contentType("application/json")
                        .content("{\"motivo\":\"No aplica\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("RECHAZADA"));
    }

    @Test
    void rechazar_cuandoNoPendiente_debeRetornar422() throws Exception {
        when(devolucionService.rechazar(eq(1L), any()))
                .thenThrow(new BusinessException("Solo se pueden rechazar devoluciones en estado PENDIENTE."));

        mockMvc.perform(patch("/api/devoluciones/1/rechazar")
                        .contentType("application/json")
                        .content("{\"motivo\":\"No aplica\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void completar_cuandoAprobada_debeRetornarDevolucion() throws Exception {
        Devolucion completada = new Devolucion();
        completada.setId(1L);
        completada.setVentaId(1L);
        completada.setTicketId(1L);
        completada.setMotivo("Evento cancelado");
        completada.setMontoDevolucion(50000.0);
        completada.setTipoDevolucion("EVENTO_CANCELADO");
        completada.setEstado("COMPLETADA");
        completada.setReembolsos(null);

        when(devolucionService.completar(1L)).thenReturn(completada);

        mockMvc.perform(patch("/api/devoluciones/1/completar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("COMPLETADA"));
    }

    @Test
    void completar_cuandoNoAprobada_debeRetornar422() throws Exception {
        when(devolucionService.completar(1L))
                .thenThrow(new BusinessException("Solo se pueden completar devoluciones en estado APROBADA."));

        mockMvc.perform(patch("/api/devoluciones/1/completar"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    void obtenerReembolsos_debeRetornarLista() throws Exception {
        when(devolucionService.obtenerReembolsosPorDevolucion(1L)).thenReturn(List.of(reembolso));

        mockMvc.perform(get("/api/devoluciones/1/reembolsos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists())
                .andExpect(jsonPath("$._links.self").exists());
    }

    @Test
    void obtenerReembolsos_cuandoVacia_debeRetornar200() throws Exception {
        when(devolucionService.obtenerReembolsosPorDevolucion(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/devoluciones/1/reembolsos"))
                .andExpect(status().isOk());
    }

    @Test
    void agregarReembolso_conDatosValidos_debeRetornar201() throws Exception {
        when(devolucionService.agregarReembolso(eq(1L), any())).thenReturn(reembolso);

        mockMvc.perform(post("/api/devoluciones/1/reembolsos")
                        .contentType("application/json")
                        .content("{\"montoReembolso\":50000.0,\"metodoReembolso\":\"TRANSFERENCIA\",\"referenciaBancaria\":\"REF-001\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.montoReembolso").value(50000.0))
                .andExpect(jsonPath("$.metodoReembolso").value("TRANSFERENCIA"));
    }

    @Test
    void agregarReembolso_conDatosInvalidos_debeRetornar400() throws Exception {
        mockMvc.perform(post("/api/devoluciones/1/reembolsos")
                        .contentType("application/json")
                        .content("{\"montoReembolso\":null,\"metodoReembolso\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void procesarReembolso_cuandoPendiente_debeRetornarReembolso() throws Exception {
        ReembolsoDevolucion procesado = new ReembolsoDevolucion();
        procesado.setId(1L);
        procesado.setDevolucion(devolucion);
        procesado.setMontoReembolso(50000.0);
        procesado.setMetodoReembolso("TRANSFERENCIA");
        procesado.setEstadoReembolso("PROCESADO");

        when(devolucionService.procesarReembolso(1L)).thenReturn(procesado);

        mockMvc.perform(patch("/api/devoluciones/reembolsos/1/procesar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estadoReembolso").value("PROCESADO"));
    }

    @Test
    void procesarReembolso_cuandoNoExiste_debeRetornar404() throws Exception {
        when(devolucionService.procesarReembolso(99L))
                .thenThrow(new ResourceNotFoundException("Reembolso no encontrado con id: 99"));

        mockMvc.perform(patch("/api/devoluciones/reembolsos/99/procesar"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
