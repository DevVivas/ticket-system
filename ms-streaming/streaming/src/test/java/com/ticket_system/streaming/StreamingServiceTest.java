package test.java.com.ticket_system.streaming;

import com.ticket_system.streaming.DTO.AccesoDTO;
import com.ticket_system.streaming.DTO.StreamingDTO;
import com.ticket_system.streaming.Exception.BusinessException;
import com.ticket_system.streaming.Exception.ResourceNotFoundException;
import com.ticket_system.streaming.Model.AccesoStreaming;
import com.ticket_system.streaming.Model.Streaming;
import com.ticket_system.streaming.Repository.AccesoStreamingRepository;
import com.ticket_system.streaming.Repository.StreamingRepository;
import com.ticket_system.streaming.Service.StreamingService;
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
class StreamingServiceTest {

    @Mock
    private StreamingRepository streamingRepository;

    @Mock
    private AccesoStreamingRepository accesoRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private StreamingService streamingService;

    private Streaming streaming;
    private StreamingDTO streamingDTO;
    private AccesoDTO accesoDTO;

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

        streamingDTO = new StreamingDTO();
        streamingDTO.setEventoId(1L);
        streamingDTO.setNombreStream("Karol G en Vivo");
        streamingDTO.setUrlStream("https://stream.karolg.com/live");
        streamingDTO.setFechaInicio(LocalDateTime.of(2026, 12, 15, 20, 0));
        streamingDTO.setFechaFin(LocalDateTime.of(2026, 12, 15, 23, 0));
        streamingDTO.setCapacidadMaxima(10000);

        accesoDTO = new AccesoDTO();
        accesoDTO.setTicketId(1L);
        accesoDTO.setNombreEspectador("Juan Perez");
        accesoDTO.setEmailEspectador("juan@email.com");
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarStreaming() {
        when(streamingRepository.findById(1L)).thenReturn(Optional.of(streaming));

        Streaming resultado = streamingService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("Karol G en Vivo", resultado.getNombreStream());
        verify(streamingRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeLanzarResourceNotFoundException() {
        when(streamingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> streamingService.obtenerPorId(99L));
    }

    @Test
    void crear_cuandoEventoNoTieneStreaming_debeCrear() {
        when(streamingRepository.findByEventoId(1L)).thenReturn(Optional.empty());
        when(streamingRepository.save(any(Streaming.class))).thenReturn(streaming);

        Streaming resultado = streamingService.crear(streamingDTO);

        assertNotNull(resultado);
        assertEquals("PROGRAMADO", resultado.getEstado());
        verify(streamingRepository, times(1)).save(any(Streaming.class));
    }

    @Test
    void crear_cuandoEventoYaTieneStreaming_debeLanzarBusinessException() {
        when(streamingRepository.findByEventoId(1L)).thenReturn(Optional.of(streaming));

        assertThrows(BusinessException.class,
            () -> streamingService.crear(streamingDTO));

        verify(streamingRepository, never()).save(any());
    }

    @Test
    void iniciarStream_cuandoEstaProgramado_debeIniciar() {
        when(streamingRepository.findById(1L)).thenReturn(Optional.of(streaming));
        when(streamingRepository.save(any(Streaming.class))).thenReturn(streaming);

        Streaming resultado = streamingService.iniciarStream(1L);

        assertEquals("EN_VIVO", resultado.getEstado());
        verify(streamingRepository, times(1)).save(any(Streaming.class));
    }

    @Test
    void iniciarStream_cuandoNoEstaProgramado_debeLanzarBusinessException() {
        streaming.setEstado("EN_VIVO");
        when(streamingRepository.findById(1L)).thenReturn(Optional.of(streaming));

        assertThrows(BusinessException.class,
            () -> streamingService.iniciarStream(1L));

        verify(streamingRepository, never()).save(any());
    }

    @Test
    void generarAcceso_cuandoHayCapacidad_debeGenerarAcceso() {
        AccesoStreaming acceso = new AccesoStreaming();
        acceso.setCodigoAcceso("STR-123456");

        when(streamingRepository.findById(1L)).thenReturn(Optional.of(streaming));
        when(accesoRepository.findByTicketId(1L)).thenReturn(Optional.empty());
        when(accesoRepository.save(any(AccesoStreaming.class))).thenReturn(acceso);
        when(streamingRepository.save(any(Streaming.class))).thenReturn(streaming);

        AccesoStreaming resultado = streamingService.generarAcceso(1L, accesoDTO);

        assertNotNull(resultado);
        verify(accesoRepository, times(1)).save(any(AccesoStreaming.class));
    }

    @Test
    void generarAcceso_cuandoSinCapacidad_debeLanzarBusinessException() {
        streaming.setCapacidadDisponible(0);

        when(streamingRepository.findById(1L)).thenReturn(Optional.of(streaming));
        when(accesoRepository.findByTicketId(1L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
            () -> streamingService.generarAcceso(1L, accesoDTO));

        verify(accesoRepository, never()).save(any());
    }

    @Test
    void listarTodos_debeRetornarTodosLosStreamings() {
        when(streamingRepository.findAll()).thenReturn(List.of(streaming));

        List<Streaming> resultado = streamingService.listarTodos();

        assertEquals(1, resultado.size());
        verify(streamingRepository, times(1)).findAll();
    }
}