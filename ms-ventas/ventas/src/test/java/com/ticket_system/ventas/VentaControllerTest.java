package com.ticket_system.ventas;

import com.ticket_system.ventas.Assembler.ItemVentaAssembler;
import com.ticket_system.ventas.Assembler.VentaAssembler;
import com.ticket_system.ventas.Controller.VentaController;
import com.ticket_system.ventas.DTO.ItemVentaDTO;
import com.ticket_system.ventas.DTO.VentaDTO;
import com.ticket_system.ventas.Exception.BusinessException;
import com.ticket_system.ventas.Exception.ResourceNotFoundException;
import com.ticket_system.ventas.Model.ItemVenta;
import com.ticket_system.ventas.Model.Venta;
import com.ticket_system.ventas.Service.VentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VentaController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({VentaAssembler.class, ItemVentaAssembler.class})
class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VentaService ventaService;

    @MockitoBean
    private VentaAssembler ventaAssembler;

    private Venta venta;
    private VentaDTO ventaDTO;

    @BeforeEach
    void setUp() {
        ItemVenta item = new ItemVenta();
        item.setId(1L);
        item.setTicketId(10L);
        item.setPrecioUnitario(50000.0);
        item.setCantidad(1);
        item.setVenta(venta);

        venta = new Venta();
        venta.setId(1L);
        venta.setCompradorId(1L);
        venta.setEventoId(1L);
        venta.setMetodoPago("TARJETA");
        venta.setFechaVenta(LocalDateTime.of(2026, 7, 10, 14, 30));
        venta.setMontoTotal(50000.0);
        venta.setEstado("COMPLETADA");
        venta.setItems(List.of(item));

        item.setVenta(venta);

        ItemVentaDTO itemDTO = new ItemVentaDTO();
        itemDTO.setTicketId(10L);
        itemDTO.setPrecioUnitario(50000.0);
        itemDTO.setCantidad(1);

        ventaDTO = new VentaDTO();
        ventaDTO.setCompradorId(1L);
        ventaDTO.setEventoId(1L);
        ventaDTO.setMetodoPago("TARJETA");
        ventaDTO.setItems(List.of(itemDTO));

        EntityModel<Venta> ventaModel = EntityModel.of(venta);
        when(ventaAssembler.toModel(any(Venta.class))).thenReturn(ventaModel);
        when(ventaAssembler.toCollectionModel(any())).thenReturn(CollectionModel.of(List.of(ventaModel)));
    }

    @Test
    void getAll_hayVentas_debeRetornarLista() throws Exception {
        when(ventaService.obtenerTodos()).thenReturn(List.of(venta));

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.ventaList", hasSize(1)))
                .andExpect(jsonPath("$._embedded.ventaList[0].metodoPago").value("TARJETA"));
    }

    @Test
    void getAll_noHayVentas_debeRetornarListaVacia() throws Exception {
        when(ventaService.obtenerTodos()).thenReturn(List.of());
        when(ventaAssembler.toCollectionModel(any())).thenReturn(CollectionModel.of(List.of()));

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk());
    }

    @Test
    void getById_existe_debeRetornarVenta() throws Exception {
        when(ventaService.obtenerPorId(1L)).thenReturn(venta);

        mockMvc.perform(get("/api/ventas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metodoPago").value("TARJETA"))
                .andExpect(jsonPath("$.estado").value("COMPLETADA"));
    }

    @Test
    void getById_noExiste_debeRetornar404() throws Exception {
        when(ventaService.obtenerPorId(99L))
                .thenThrow(new ResourceNotFoundException("Venta no encontrada con id: 99"));

        mockMvc.perform(get("/api/ventas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByComprador_existe_debeRetornarLista() throws Exception {
        when(ventaService.obtenerPorComprador(1L)).thenReturn(List.of(venta));

        mockMvc.perform(get("/api/ventas/comprador/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.ventaList", hasSize(1)));
    }

    @Test
    void getByComprador_noHayVentas_debeRetornarListaVacia() throws Exception {
        when(ventaService.obtenerPorComprador(99L)).thenReturn(List.of());
        when(ventaAssembler.toCollectionModel(any())).thenReturn(CollectionModel.of(List.of()));

        mockMvc.perform(get("/api/ventas/comprador/99"))
                .andExpect(status().isOk());
    }

    @Test
    void getByEvento_existe_debeRetornarLista() throws Exception {
        when(ventaService.obtenerPorEvento(1L)).thenReturn(List.of(venta));

        mockMvc.perform(get("/api/ventas/evento/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.ventaList", hasSize(1)));
    }

    @Test
    void getByEvento_noHayVentas_debeRetornarListaVacia() throws Exception {
        when(ventaService.obtenerPorEvento(99L)).thenReturn(List.of());
        when(ventaAssembler.toCollectionModel(any())).thenReturn(CollectionModel.of(List.of()));

        mockMvc.perform(get("/api/ventas/evento/99"))
                .andExpect(status().isOk());
    }

    @Test
    void create_datosValidos_debeRetornar201() throws Exception {
        when(ventaService.crear(any(VentaDTO.class))).thenReturn(venta);

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"compradorId\":1,\"eventoId\":1,\"metodoPago\":\"TARJETA\",\"items\":[{\"ticketId\":10,\"precioUnitario\":50000.0,\"cantidad\":1}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.metodoPago").value("TARJETA"))
                .andExpect(jsonPath("$.estado").value("COMPLETADA"));
    }

    @Test
    void create_datosInvalidos_debeRetornar400() throws Exception {
        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cancelar_completada_debeCancelar() throws Exception {
        Venta cancelada = new Venta();
        cancelada.setId(1L);
        cancelada.setCompradorId(1L);
        cancelada.setEventoId(1L);
        cancelada.setMetodoPago("TARJETA");
        cancelada.setMontoTotal(50000.0);
        cancelada.setEstado("RECHAZADA");
        when(ventaService.cancelar(1L)).thenReturn(cancelada);

        mockMvc.perform(patch("/api/ventas/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RECHAZADA"));
    }

    @Test
    void cancelar_yaRechazada_debeRetornar422() throws Exception {
        when(ventaService.cancelar(1L))
                .thenThrow(new BusinessException("La venta ya fue rechazada anteriormente."));

        mockMvc.perform(patch("/api/ventas/1/cancelar"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void delete_existe_debeRetornar204() throws Exception {
        doNothing().when(ventaService).eliminar(1L);

        mockMvc.perform(delete("/api/ventas/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_noExiste_debeRetornar404() throws Exception {
        doThrow(new ResourceNotFoundException("Venta no encontrada")).when(ventaService).eliminar(99L);

        mockMvc.perform(delete("/api/ventas/99"))
                .andExpect(status().isNotFound());
    }
}
