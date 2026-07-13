package com.ticket_system.promotores;

import com.ticket_system.promotores.DTO.ComisionDTO;
import com.ticket_system.promotores.DTO.PromotorDTO;
import com.ticket_system.promotores.Exception.BusinessException;
import com.ticket_system.promotores.Exception.ResourceNotFoundException;
import com.ticket_system.promotores.Model.ComisionPromotor;
import com.ticket_system.promotores.Model.Promotor;
import com.ticket_system.promotores.Repository.ComisionPromotorRepository;
import com.ticket_system.promotores.Repository.PromotorRepository;
import com.ticket_system.promotores.Service.PromotorService;
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
class PromotorServiceTest {

    @Mock
    private PromotorRepository promotorRepository;

    @Mock
    private ComisionPromotorRepository comisionRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private PromotorService promotorService;

    private Promotor promotor;
    private PromotorDTO promotorDTO;
    private ComisionDTO comisionDTO;

    @BeforeEach
    void setUp() {
        promotor = new Promotor();
        promotor.setId(1L);
        promotor.setNombre("Pedro Promotor");
        promotor.setEmail("pedro@promotores.cl");
        promotor.setTelefono("+56987654321");
        promotor.setPorcentajeComision(10.0);
        promotor.setEstado("ACTIVO");

        promotorDTO = new PromotorDTO();
        promotorDTO.setNombre("Pedro Promotor");
        promotorDTO.setEmail("pedro@promotores.cl");
        promotorDTO.setTelefono("+56987654321");
        promotorDTO.setPorcentajeComision(10.0);

        comisionDTO = new ComisionDTO();
        comisionDTO.setVentaId(1L);
        comisionDTO.setMontoVenta(50000.0);
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarPromotor() {
        when(promotorRepository.findById(1L)).thenReturn(Optional.of(promotor));

        Promotor resultado = promotorService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("Pedro Promotor", resultado.getNombre());
        verify(promotorRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeLanzarResourceNotFoundException() {
        when(promotorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> promotorService.obtenerPorId(99L));
    }

    @Test
    void crear_cuandoEmailNoExiste_debeCrearPromotor() {
        when(promotorRepository.findByEmail("pedro@promotores.cl"))
            .thenReturn(Optional.empty());
        when(promotorRepository.save(any(Promotor.class))).thenReturn(promotor);

        Promotor resultado = promotorService.crear(promotorDTO);

        assertNotNull(resultado);
        assertEquals("Pedro Promotor", resultado.getNombre());
        verify(promotorRepository, times(1)).save(any(Promotor.class));
    }

    @Test
    void crear_cuandoEmailYaExiste_debeLanzarBusinessException() {
        when(promotorRepository.findByEmail("pedro@promotores.cl"))
            .thenReturn(Optional.of(promotor));

        assertThrows(BusinessException.class,
            () -> promotorService.crear(promotorDTO));

        verify(promotorRepository, never()).save(any());
    }

    @Test
    void desactivar_cuandoNoTieneComisionesPendientes_debeDesactivar() {
        when(promotorRepository.findById(1L)).thenReturn(Optional.of(promotor));
        when(comisionRepository.sumComisionesPendientesByPromotorId(1L)).thenReturn(null);
        when(promotorRepository.save(any(Promotor.class))).thenReturn(promotor);

        promotorService.desactivar(1L);

        assertEquals("INACTIVO", promotor.getEstado());
        verify(promotorRepository, times(1)).save(promotor);
    }

    @Test
    void desactivar_cuandoTieneComisionesPendientes_debeLanzarBusinessException() {
        when(promotorRepository.findById(1L)).thenReturn(Optional.of(promotor));
        when(comisionRepository.sumComisionesPendientesByPromotorId(1L)).thenReturn(5000.0);

        assertThrows(BusinessException.class,
            () -> promotorService.desactivar(1L));

        verify(promotorRepository, never()).save(any());
    }

    @Test
    void listarActivos_debeRetornarSoloActivos() {
        when(promotorRepository.findByEstado("ACTIVO")).thenReturn(List.of(promotor));

        List<Promotor> resultado = promotorService.listarActivos();

        assertEquals(1, resultado.size());
        assertEquals("ACTIVO", resultado.get(0).getEstado());
    }

    @Test
    void pagarComision_cuandoEstaPendiente_debePagar() {
        ComisionPromotor comision = new ComisionPromotor();
        comision.setId(1L);
        comision.setEstadoComision("PENDIENTE");

        when(comisionRepository.findById(1L)).thenReturn(Optional.of(comision));
        when(comisionRepository.save(any(ComisionPromotor.class))).thenReturn(comision);

        ComisionPromotor resultado = promotorService.pagarComision(1L);

        assertEquals("PAGADA", resultado.getEstadoComision());
        verify(comisionRepository, times(1)).save(any(ComisionPromotor.class));
    }

    @Test
    void pagarComision_cuandoYaEstaPagada_debeLanzarBusinessException() {
        ComisionPromotor comision = new ComisionPromotor();
        comision.setId(1L);
        comision.setEstadoComision("PAGADA");

        when(comisionRepository.findById(1L)).thenReturn(Optional.of(comision));

        assertThrows(BusinessException.class,
            () -> promotorService.pagarComision(1L));

        verify(comisionRepository, never()).save(any());
    }
}