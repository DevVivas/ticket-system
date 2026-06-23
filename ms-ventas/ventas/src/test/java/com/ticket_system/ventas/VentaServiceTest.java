package com.ticket_system.ventas;

// import com.ticket_system.ventas.DTO.ItemVentaDTO;
// import com.ticket_system.ventas.DTO.VentaDTO;
import com.ticket_system.ventas.Model.ItemVenta;
import com.ticket_system.ventas.Model.Venta;
import com.ticket_system.ventas.Repository.VentaRepository;
import com.ticket_system.ventas.Service.VentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private VentaService ventaService;

    private Venta venta;

    @BeforeEach
    void setUp() {
        ItemVenta item = new ItemVenta();
        item.setTicketId(1L);
        item.setPrecioUnitario(50000.0);
        item.setCantidad(1);

        venta = new Venta();
        venta.setId(1L);
        venta.setCompradorId(1L);
        venta.setEventoId(1L);
        venta.setMetodoPago("TARJETA");
        venta.setFechaVenta(LocalDateTime.now());
        venta.setEstado("COMPLETADA");
        venta.setMontoTotal(50000.0);
        venta.setItems(List.of(item));
    }

    // ─── TEST: obtenerPorId ───────────────────────────────────────────────────

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarVenta() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        Venta resultado = ventaService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("COMPLETADA", resultado.getEstado());
        assertEquals(50000.0, resultado.getMontoTotal());
        verify(ventaRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeLanzarExcepcion() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
            () -> ventaService.obtenerPorId(99L));
    }

    // ─── TEST: cancelar ───────────────────────────────────────────────────────

    @Test
    void cancelar_cuandoEstaCompletada_debeCancelar() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);

        Venta resultado = ventaService.cancelar(1L);

        assertEquals("RECHAZADA", resultado.getEstado());
        verify(ventaRepository, times(1)).save(any(Venta.class));
    }

    @Test
    void cancelar_cuandoYaEstaRechazada_debeLanzarExcepcion() {
        venta.setEstado("RECHAZADA");
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));

        assertThrows(RuntimeException.class,
            () -> ventaService.cancelar(1L));

        verify(ventaRepository, never()).save(any());
    }

    // ─── TEST: eliminar ───────────────────────────────────────────────────────

    @Test
    void eliminar_cuandoExiste_debeEliminar() {
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        doNothing().when(ventaRepository).deleteById(1L);

        ventaService.eliminar(1L);

        verify(ventaRepository, times(1)).deleteById(1L);
    }

    // ─── TEST: obtenerPorComprador ────────────────────────────────────────────

    @Test
    void obtenerPorComprador_debeRetornarVentasDelComprador() {
        when(ventaRepository.findByCompradorId(1L)).thenReturn(List.of(venta));

        List<Venta> resultado = ventaService.obtenerPorComprador(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getCompradorId());
    }

    // ─── TEST: obtenerTodos ───────────────────────────────────────────────────

    @Test
    void obtenerTodos_debeRetornarListaDeVentas() {
        when(ventaRepository.findAll()).thenReturn(List.of(venta));

        List<Venta> resultado = ventaService.obtenerTodos();

        assertEquals(1, resultado.size());
        verify(ventaRepository, times(1)).findAll();
    }
}