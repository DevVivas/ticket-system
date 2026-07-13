package com.ticket_system.recintos;

import com.ticket_system.recintos.Assembler.RecintoAssembler;
import com.ticket_system.recintos.Assembler.SectorAssembler;
import com.ticket_system.recintos.Controller.RecintoController;
import com.ticket_system.recintos.DTO.RecintoDTO;
import com.ticket_system.recintos.DTO.SectorDTO;
import com.ticket_system.recintos.Exception.BusinessException;
import com.ticket_system.recintos.Exception.ResourceNotFoundException;
import com.ticket_system.recintos.Model.Recinto;
import com.ticket_system.recintos.Model.Sector;
import com.ticket_system.recintos.Service.RecintoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecintoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({RecintoAssembler.class, SectorAssembler.class})
class RecintoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecintoService recintoService;

    private Recinto recinto;
    private RecintoDTO recintoDTO;
    private Sector sector;
    private SectorDTO sectorDTO;

    @BeforeEach
    void setUp() {
        recinto = new Recinto();
        recinto.setId(1L);
        recinto.setNombre("Estadio Nacional");
        recinto.setDireccion("Av. Grecia 2001");
        recinto.setCapacidadMaxima(5000);

        recintoDTO = new RecintoDTO();
        recintoDTO.setNombre("Estadio Nacional");
        recintoDTO.setDireccion("Av. Grecia 2001");
        recintoDTO.setCapacidadMaxima(5000);

        sector = new Sector();
        sector.setId(1L);
        sector.setNombre("VIP");
        sector.setCapacidad(500);
        sector.setPrecioBase(75000.0);
        sector.setRecinto(recinto);

        sectorDTO = new SectorDTO();
        sectorDTO.setNombre("VIP");
        sectorDTO.setCapacidad(500);
        sectorDTO.setPrecioBase(75000.0);
    }

    @Test
    void getAll_hayRecintos_debeRetornarLista() throws Exception {
        when(recintoService.obtenerTodos()).thenReturn(List.of(recinto));

        mockMvc.perform(get("/api/recintos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.recintos", hasSize(1)))
                .andExpect(jsonPath("$._embedded.recintos[0].nombre").value("Estadio Nacional"));
    }

    @Test
    void getAll_noHayRecintos_debeRetornarListaVacia() throws Exception {
        when(recintoService.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/recintos"))
                .andExpect(status().isOk());
    }

    @Test
    void getById_existe_debeRetornarRecinto() throws Exception {
        when(recintoService.obtenerPorId(1L)).thenReturn(recinto);

        mockMvc.perform(get("/api/recintos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Estadio Nacional"))
                .andExpect(jsonPath("$.direccion").value("Av. Grecia 2001"));
    }

    @Test
    void getById_noExiste_debeRetornar404() throws Exception {
        when(recintoService.obtenerPorId(99L))
                .thenThrow(new ResourceNotFoundException("Recinto no encontrado con id: 99"));

        mockMvc.perform(get("/api/recintos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_datosValidos_debeRetornar201() throws Exception {
        when(recintoService.crear(any(RecintoDTO.class))).thenReturn(recinto);

        mockMvc.perform(post("/api/recintos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Estadio Nacional\",\"direccion\":\"Av. Grecia 2001\",\"capacidadMaxima\":5000}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Estadio Nacional"));
    }

    @Test
    void create_datosInvalidos_debeRetornar400() throws Exception {
        mockMvc.perform(post("/api/recintos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_existe_debeRetornarRecinto() throws Exception {
        when(recintoService.actualizar(eq(1L), any(RecintoDTO.class))).thenReturn(recinto);

        mockMvc.perform(put("/api/recintos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Estadio Nacional\",\"direccion\":\"Av. Grecia 2001\",\"capacidadMaxima\":5000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Estadio Nacional"));
    }

    @Test
    void update_noExiste_debeRetornar404() throws Exception {
        when(recintoService.actualizar(eq(99L), any(RecintoDTO.class)))
                .thenThrow(new ResourceNotFoundException("Recinto no encontrado"));

        mockMvc.perform(put("/api/recintos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Estadio Nacional\",\"direccion\":\"Av. Grecia 2001\",\"capacidadMaxima\":5000}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_existe_debeRetornar204() throws Exception {
        doNothing().when(recintoService).eliminar(1L);

        mockMvc.perform(delete("/api/recintos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_noExiste_debeRetornar404() throws Exception {
        doThrow(new ResourceNotFoundException("Recinto no encontrado")).when(recintoService).eliminar(99L);

        mockMvc.perform(delete("/api/recintos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addSector_datosValidos_debeRetornar201() throws Exception {
        when(recintoService.agregarSector(eq(1L), any(SectorDTO.class))).thenReturn(sector);

        mockMvc.perform(post("/api/recintos/1/sectores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"VIP\",\"capacidad\":500,\"precioBase\":75000.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("VIP"));
    }

    @Test
    void addSector_errorCapacidad_debeRetornar400() throws Exception {
        when(recintoService.agregarSector(eq(1L), any(SectorDTO.class)))
                .thenThrow(new BusinessException("La capacidad del sector supera la capacidad maxima del recinto"));

        mockMvc.perform(post("/api/recintos/1/sectores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"VIP\",\"capacidad\":500,\"precioBase\":75000.0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSectores_existe_debeRetornarLista() throws Exception {
        when(recintoService.obtenerSectores(1L)).thenReturn(List.of(sector));

        mockMvc.perform(get("/api/recintos/1/sectores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.sectors", hasSize(1)))
                .andExpect(jsonPath("$._embedded.sectors[0].nombre").value("VIP"));
    }

    @Test
    void getSectores_noExisteRecinto_debeRetornar404() throws Exception {
        when(recintoService.obtenerSectores(99L))
                .thenThrow(new ResourceNotFoundException("Recinto no encontrado"));

        mockMvc.perform(get("/api/recintos/99/sectores"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSector_existe_debeRetornar204() throws Exception {
        doNothing().when(recintoService).eliminarSector(1L);

        mockMvc.perform(delete("/api/recintos/sectores/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteSector_noExiste_debeRetornar404() throws Exception {
        doThrow(new ResourceNotFoundException("Sector no encontrado")).when(recintoService).eliminarSector(99L);

        mockMvc.perform(delete("/api/recintos/sectores/99"))
                .andExpect(status().isNotFound());
    }
}
