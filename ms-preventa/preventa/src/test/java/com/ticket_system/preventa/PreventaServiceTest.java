package test.java.com.ticket_system.preventa;

import com.ticket_system.preventa.DTO.CodigoBeneficioDTO;
import com.ticket_system.preventa.Exception.BusinessException;
import com.ticket_system.preventa.Exception.ResourceNotFoundException;
import com.ticket_system.preventa.Model.CodigoBeneficio;
import com.ticket_system.preventa.Repository.CodigoBeneficioRepository;
import com.ticket_system.preventa.Service.PreventaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreventaServiceTest {

    @Mock
    private CodigoBeneficioRepository codigoBeneficioRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private PreventaService preventaService;

    private CodigoBeneficio codigo;
    private CodigoBeneficioDTO codigoDTO;

    @BeforeEach
    void setUp() {
        codigo = new CodigoBeneficio();
        codigo.setId(1L);
        codigo.setCodigo("PROMO2026");
        codigo.setPorcentajeDescuento(20.0);
        codigo.setEventoId(1L);
        codigo.setTipo("DESCUENTO");
        codigo.setUsoMaximo(100);
        codigo.setUsoActual(0);
        codigo.setActivo(true);

        codigoDTO = new CodigoBeneficioDTO();
        codigoDTO.setCodigo("PROMO2026");
        codigoDTO.setPorcentajeDescuento(20.0);
        codigoDTO.setEventoId(1L);
        codigoDTO.setTipo("DESCUENTO");
        codigoDTO.setUsoMaximo(100);
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarCodigo() {
        when(codigoBeneficioRepository.findById(1L)).thenReturn(Optional.of(codigo));

        Optional<CodigoBeneficio> resultado = preventaService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("PROMO2026", resultado.get().getCodigo());
        verify(codigoBeneficioRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeRetornarVacio() {
        when(codigoBeneficioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<CodigoBeneficio> resultado = preventaService.obtenerPorId(99L);

        assertFalse(resultado.isPresent());
    }

    @Test
    void crear_debeCrearCodigoBeneficio() {
        when(codigoBeneficioRepository.save(any(CodigoBeneficio.class))).thenReturn(codigo);

        CodigoBeneficio resultado = preventaService.crear(codigoDTO);

        assertNotNull(resultado);
        assertEquals("PROMO2026", resultado.getCodigo());
        verify(codigoBeneficioRepository, times(1)).save(any(CodigoBeneficio.class));
    }

    @Test
    void desactivar_cuandoExiste_debeDesactivar() {
        when(codigoBeneficioRepository.findById(1L)).thenReturn(Optional.of(codigo));
        when(codigoBeneficioRepository.save(any(CodigoBeneficio.class))).thenReturn(codigo);

        CodigoBeneficio resultado = preventaService.desactivar(1L);

        assertFalse(resultado.isActivo());
        verify(codigoBeneficioRepository, times(1)).save(any(CodigoBeneficio.class));
    }

    @Test
    void obtenerTodos_debeRetornarTodosLosCodigos() {
        when(codigoBeneficioRepository.findAll()).thenReturn(List.of(codigo));

        List<CodigoBeneficio> resultado = preventaService.obtenerTodos();

        assertEquals(1, resultado.size());
        verify(codigoBeneficioRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorEvento_debeRetornarCodigosDelEvento() {
        when(codigoBeneficioRepository.findByEventoId(1L)).thenReturn(List.of(codigo));

        List<CodigoBeneficio> resultado = preventaService.obtenerPorEvento(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getEventoId());
    }
}