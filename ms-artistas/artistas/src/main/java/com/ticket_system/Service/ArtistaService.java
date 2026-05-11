package com.ticket_system.Service;

import com.ticket_system.DTO.AgendaDTO;
import com.ticket_system.DTO.ArtistaDTO;
import com.ticket_system.Exception.BusinessException;
import com.ticket_system.Exception.ResourceNotFoundException;
import com.ticket_system.Model.AgendaArtista;
import com.ticket_system.Model.Artista;
import com.ticket_system.Repository.AgendaArtistaRepository;
import com.ticket_system.Repository.ArtistaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;

@Service
public class ArtistaService {

    private static final Logger logger = LoggerFactory.getLogger(ArtistaService.class);

    @Autowired
    private ArtistaRepository artistaRepository;

    @Autowired
    private AgendaArtistaRepository agendaArtistaRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${ms.eventos.url}")
    private String msEventosUrl;

    // ─── CRUD ARTISTAS ────────────────────────────────────────────────────────

    public List<Artista> listarTodos() {
        logger.info("[ARTISTAS] Listando todos los artistas");
        return artistaRepository.findAll();
    }

    public Artista obtenerPorId(Long id) {
        logger.info("[ARTISTAS] Buscando artista con id: {}", id);
        return artistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artista no encontrado con id: " + id));
    }

    public Artista crear(ArtistaDTO dto) {
        logger.info("[ARTISTAS] Creando artista: {}", dto.getNombre());

        // Regla de negocio: no puede haber dos artistas con el mismo nombre
        boolean nombreExacto = artistaRepository
                .findByNombreContainingIgnoreCase(dto.getNombre())
                .stream()
                .anyMatch(a -> a.getNombre().equalsIgnoreCase(dto.getNombre()));

        if (nombreExacto) {
            throw new BusinessException("Ya existe un artista con el nombre: " + dto.getNombre());
        }

        Artista artista = new Artista();
        artista.setNombre(dto.getNombre());
        artista.setGenero(dto.getGenero());
        artista.setNacionalidad(dto.getNacionalidad());
        artista.setBiografia(dto.getBiografia());
        artista.setImagenUrl(dto.getImagenUrl());
        artista.setSitioWeb(dto.getSitioWeb());

        Artista guardado = artistaRepository.save(artista);
        logger.info("[ARTISTAS] Artista creado con id: {}", guardado.getId());
        return guardado;
    }

    public Artista actualizar(Long id, ArtistaDTO dto) {
        logger.info("[ARTISTAS] Actualizando artista con id: {}", id);
        Artista artista = obtenerPorId(id);
        artista.setNombre(dto.getNombre());
        artista.setGenero(dto.getGenero());
        artista.setNacionalidad(dto.getNacionalidad());
        artista.setBiografia(dto.getBiografia());
        artista.setImagenUrl(dto.getImagenUrl());
        artista.setSitioWeb(dto.getSitioWeb());

        Artista actualizado = artistaRepository.save(artista);
        logger.info("[ARTISTAS] Artista actualizado con id: {}", actualizado.getId());
        return actualizado;
    }

    public void eliminar(Long id) {
        logger.info("[ARTISTAS] Desactivando artista con id: {}", id);
        Artista artista = obtenerPorId(id);

        // Regla de negocio: no eliminar si tiene agenda confirmada
        long confirmadas = agendaArtistaRepository.findByArtistaId(id)
                .stream()
                .filter(a -> "CONFIRMADO".equals(a.getEstadoAgenda()))
                .count();

        if (confirmadas > 0) {
            throw new BusinessException("No se puede eliminar el artista porque tiene " +
                    confirmadas + " presentación(es) confirmada(s).");
        }

        artista.setEstado("INACTIVO");
        artistaRepository.save(artista);
        logger.info("[ARTISTAS] Artista desactivado con id: {}", id);
    }

    public List<Artista> listarPorGenero(String genero) {
        logger.info("[ARTISTAS] Filtrando artistas por genero: {}", genero);
        return artistaRepository.findByGeneroIgnoreCase(genero);
    }

    public List<Artista> listarActivos() {
        logger.info("[ARTISTAS] Listando artistas activos");
        return artistaRepository.findByEstado("ACTIVO");
    }

    // ─── CRUD AGENDA ──────────────────────────────────────────────────────────

    public AgendaArtista agregarAgenda(Long artistaId, AgendaDTO dto) {
        logger.info("[ARTISTAS] Agregando agenda al artista id: {}", artistaId);
        Artista artista = obtenerPorId(artistaId);

        // Regla de negocio: artista debe estar activo
        if (!"ACTIVO".equals(artista.getEstado())) {
            throw new BusinessException("No se puede agendar un artista inactivo.");
        }

        // Comunicacion con ms-eventos para verificar que el evento existe
        try {
            webClientBuilder.build()
                    .get()
                    .uri(msEventosUrl + "/api/eventos/" + dto.getEventoId())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            logger.info("[ARTISTAS] Evento {} verificado en ms-eventos", dto.getEventoId());
        } catch (Exception e) {
            logger.warn("[ARTISTAS] No se pudo verificar el evento en ms-eventos: {}", e.getMessage());
        }

        AgendaArtista agenda = new AgendaArtista();
        agenda.setArtista(artista);
        agenda.setEventoId(dto.getEventoId());
        agenda.setNombreEvento(dto.getNombreEvento());
        agenda.setFechaPresentacion(dto.getFechaPresentacion());
        agenda.setLugar(dto.getLugar());
        agenda.setNotas(dto.getNotas());

        AgendaArtista guardada = agendaArtistaRepository.save(agenda);
        logger.info("[ARTISTAS] Agenda creada con id: {}", guardada.getId());
        return guardada;
    }

    public List<AgendaArtista> obtenerAgendaPorArtista(Long artistaId) {
        logger.info("[ARTISTAS] Obteniendo agenda del artista id: {}", artistaId);
        obtenerPorId(artistaId); // valida que existe
        return agendaArtistaRepository.findByArtistaId(artistaId);
    }

    public AgendaArtista confirmarAgenda(Long agendaId) {
        logger.info("[ARTISTAS] Confirmando agenda id: {}", agendaId);
        AgendaArtista agenda = agendaArtistaRepository.findById(agendaId)
                .orElseThrow(() -> new ResourceNotFoundException("Agenda no encontrada con id: " + agendaId));

        if ("CANCELADO".equals(agenda.getEstadoAgenda())) {
            throw new BusinessException("No se puede confirmar una agenda cancelada.");
        }

        agenda.setEstadoAgenda("CONFIRMADO");
        logger.info("[ARTISTAS] Agenda {} confirmada", agendaId);
        return agendaArtistaRepository.save(agenda);
    }

    public AgendaArtista cancelarAgenda(Long agendaId) {
        logger.info("[ARTISTAS] Cancelando agenda id: {}", agendaId);
        AgendaArtista agenda = agendaArtistaRepository.findById(agendaId)
                .orElseThrow(() -> new ResourceNotFoundException("Agenda no encontrada con id: " + agendaId));

        if ("CANCELADO".equals(agenda.getEstadoAgenda())) {
            throw new BusinessException("La agenda ya está cancelada.");
        }

        agenda.setEstadoAgenda("CANCELADO");
        logger.info("[ARTISTAS] Agenda {} cancelada", agendaId);
        return agendaArtistaRepository.save(agenda);
    }
}