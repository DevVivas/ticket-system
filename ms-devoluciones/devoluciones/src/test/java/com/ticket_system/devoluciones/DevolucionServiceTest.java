package test.java.com.ticket_system.devoluciones;

import com.ticket_system.DTO.DevolucionDTO;
import com.ticket_system.Exception.BusinessException;
import com.ticket_system.Exception.ResourceNotFoundException;
import com.ticket_system.Model.Devolucion;
import com.ticket_system.Model.ReembolsoDevolucion;
import com.ticket_system.Repository.DevolucionRepository;
import com.ticket_system.Repository.ReembolsoDevolucionRepository;
import com.ticket_system.Service.DevolucionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DevolucionServiceTest {

    @Mock
    private DevolucionRepository devolucionRepository;

    @Mock
    private ReembolsoDevolucionRepository reembolsoRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private DevolucionService devolucionService;

    private Devolucion devolucion;
    private DevolucionDTO devolucionDTO;

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

        devolucionDTO = new DevolucionDTO();
        devolucionDTO.setVentaId(1L);
        devolucionDTO.setTicketId(1L);
        devolucionDTO.setMotivo("Evento cancelado");
        devolucionDTO.setMontoDevolucion(50000.0);
        devolucionDTO.setTipoDevolucion("EVENTO_CANCELADO");
    }

    // ─── TEST: obtenerPorId ───────────────────────────────────────────────────

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarDevolucion() {
        when(devolucionRepository.findById(1L)).thenReturn(Optional.of(devolucion));

        Devolucion resultado = devolucionService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("PENDIENTE", resultado.getEstado());
        verify(devolucionRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeLanzarResourceNotFoundException() {
        when(devolucionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> devolucionService.obtenerPorId(99L));
    }

    // ─── TEST: aprobar ────────────────────────────────────────────────────────

    @Test
    void aprobar_cuandoEstaPendiente_debeAprobar() {
        when(devolucionRepository.findById(1L)).thenReturn(Optional.of(devolucion));
        when(devolucionRepository.save(any(Devolucion.class))).thenReturn(devolucion);

        Devolucion resultado = devolucionService.aprobar(1L);

        assertEquals("APROBADA", resultado.getEstado());
        verify(devolucionRepository, times(1)).save(any(Devolucion.class));
    }

    @Test
    void aprobar_cuandoNoEstaPendiente_debeLanzarBusinessException() {
        devolucion.setEstado("APROBADA");
        when(devolucionRepository.findById(1L)).thenReturn(Optional.of(devolucion));

        assertThrows(BusinessException.class,
            () -> devolucionService.aprobar(1L));

        verify(devolucionRepository, never()).save(any());
    }

    // ─── TEST: rechazar ───────────────────────────────────────────────────────

    @Test
    void rechazar_cuandoEstaPendiente_debeRechazar() {
        when(devolucionRepository.findById(1L)).thenReturn(Optional.of(devolucion));
        when(devolucionRepository.save(any(Devolucion.class))).thenReturn(devolucion);

        Devolucion resultado = devolucionService.rechazar(1L, "No aplica");

        assertEquals("RECHAZADA", resultado.getEstado());
        verify(devolucionRepository, times(1)).save(any(Devolucion.class));
    }

    @Test
    void rechazar_cuandoNoEstaPendiente_debeLanzarBusinessException() {
        devolucion.setEstado("RECHAZADA");
        when(devolucionRepository.findById(1L)).thenReturn(Optional.of(devolucion));

        assertThrows(BusinessException.class,
            () -> devolucionService.rechazar(1L, "No aplica"));

        verify(devolucionRepository, never()).save(any());
    }

    // ─── TEST: listarTodas ────────────────────────────────────────────────────

    @Test
    void listarTodas_debeRetornarTodasLasDevoluciones() {
        when(devolucionRepository.findAll()).thenReturn(List.of(devolucion));

        List<Devolucion> resultado = devolucionService.listarTodas();

        assertEquals(1, resultado.size());
        verify(devolucionRepository, times(1)).findAll();
    }
}