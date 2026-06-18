package com.ticket_system.validacion;

import com.ticket_system.validacion.DTO.SesionDTO;
import com.ticket_system.validacion.DTO.ValidacionDTO;
import com.ticket_system.validacion.Exception.BusinessException;
import com.ticket_system.validacion.Exception.ResourceNotFoundException;
import com.ticket_system.validacion.Model.SesionValidacion;
import com.ticket_system.validacion.Model.ValidacionTicket;
import com.ticket_system.validacion.Repository.SesionValidacionRepository;
import com.ticket_system.validacion.Repository.ValidacionTicketRepository;
import com.ticket_system.validacion.Service.ValidacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

// import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidacionServiceTest {

    @Mock
    private SesionValidacionRepository sesionRepository;

    @Mock
    private ValidacionTicketRepository validacionRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private ValidacionService validacionService;

    private SesionValidacion sesion;
    private SesionDTO sesionDTO;
    private ValidacionDTO validacionDTO;

    @BeforeEach
    void setUp() {
        sesion = new SesionValidacion();
        sesion.setId(1L);
        sesion.setEventoId(1L);
        sesion.setNombrePortero("Juan Guardian");
        sesion.setPuestoAcceso("Puerta Norte");
        sesion.setEstado("ACTIVA");
        sesion.setTotalEscaneados(0);

        sesionDTO = new SesionDTO();
        sesionDTO.setEventoId(1L);
        sesionDTO.setNombrePortero("Juan Guardian");
        sesionDTO.setPuestoAcceso("Puerta Norte");

        validacionDTO = new ValidacionDTO();
        validacionDTO.setCodigoQR("QR-123");
        validacionDTO.setTicketId(1L);
    }

    // ─── TEST: obtenerSesionPorId ─────────────────────────────────────────────

    @Test
    void obtenerSesionPorId_cuandoExiste_debeRetornarSesion() {
        when(sesionRepository.findById(1L)).thenReturn(Optional.of(sesion));

        SesionValidacion resultado = validacionService.obtenerSesionPorId(1L);

        assertNotNull(resultado);
        assertEquals("Juan Guardian", resultado.getNombrePortero());
        verify(sesionRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerSesionPorId_cuandoNoExiste_debeLanzarResourceNotFoundException() {
        when(sesionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> validacionService.obtenerSesionPorId(99L));
    }

    // ─── TEST: cerrarSesion ───────────────────────────────────────────────────

    @Test
    void cerrarSesion_cuandoEstaActiva_debeCerrar() {
        when(sesionRepository.findById(1L)).thenReturn(Optional.of(sesion));
        when(sesionRepository.save(any(SesionValidacion.class))).thenReturn(sesion);

        SesionValidacion resultado = validacionService.cerrarSesion(1L);

        assertEquals("CERRADA", resultado.getEstado());
        verify(sesionRepository, times(1)).save(any(SesionValidacion.class));
    }

    @Test
    void cerrarSesion_cuandoYaEstaCerrada_debeLanzarBusinessException() {
        sesion.setEstado("CERRADA");
        when(sesionRepository.findById(1L)).thenReturn(Optional.of(sesion));

        assertThrows(BusinessException.class,
            () -> validacionService.cerrarSesion(1L));

        verify(sesionRepository, never()).save(any());
    }

    // ─── TEST: escanearTicket ─────────────────────────────────────────────────

    @Test
    void escanearTicket_cuandoSesionCerrada_debeLanzarBusinessException() {
        sesion.setEstado("CERRADA");
        when(sesionRepository.findById(1L)).thenReturn(Optional.of(sesion));

        assertThrows(BusinessException.class,
            () -> validacionService.escanearTicket(1L, validacionDTO));
    }

    @Test
    void escanearTicket_cuandoTicketYaUsado_debeRegistrarResultadoYaUsado() {
        ValidacionTicket validacionExistente = new ValidacionTicket();
        validacionExistente.setTicketId(1L);
        validacionExistente.setResultado("VALIDO");

        ValidacionTicket nuevaValidacion = new ValidacionTicket();
        nuevaValidacion.setResultado("YA_USADO");

        when(sesionRepository.findById(1L)).thenReturn(Optional.of(sesion));
        when(validacionRepository.findByTicketIdAndResultado(1L, "VALIDO"))
            .thenReturn(Optional.of(validacionExistente));
        when(sesionRepository.save(any())).thenReturn(sesion);
        when(validacionRepository.save(any(ValidacionTicket.class)))
            .thenReturn(nuevaValidacion);

        ValidacionTicket resultado = validacionService.escanearTicket(1L, validacionDTO);

        assertEquals("YA_USADO", resultado.getResultado());
    }

    // ─── TEST: listarSesiones ─────────────────────────────────────────────────

    @Test
    void listarSesiones_debeRetornarTodasLasSesiones() {
        when(sesionRepository.findAll()).thenReturn(List.of(sesion));

        List<SesionValidacion> resultado = validacionService.listarSesiones();

        assertEquals(1, resultado.size());
        verify(sesionRepository, times(1)).findAll();
    }
}