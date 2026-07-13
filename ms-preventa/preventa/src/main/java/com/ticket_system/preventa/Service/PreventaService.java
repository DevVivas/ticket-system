package com.ticket_system.preventa.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ticket_system.preventa.DTO.CodigoBeneficioDTO;
import com.ticket_system.preventa.Model.CodigoBeneficio;
import com.ticket_system.preventa.Repository.CodigoBeneficioRepository;

@Service
public class PreventaService {

    private static final Logger logger = LoggerFactory.getLogger(PreventaService.class);

    private final CodigoBeneficioRepository codigoRepository;

    PreventaService(CodigoBeneficioRepository codigoRepository) {
        this.codigoRepository = codigoRepository;
    }

    public List<CodigoBeneficio> obtenerTodos() {
        logger.info("Obteniendo todos los códigos de beneficio");
        return codigoRepository.findAll();
    }

    public Optional<CodigoBeneficio> obtenerPorId(Long id) {
        logger.info("Buscando código de beneficio con id: {}", id);
        return codigoRepository.findById(id);
    }

    public CodigoBeneficio crear(CodigoBeneficioDTO dto) {
        logger.info("Creando código de beneficio: {}", dto.getCodigo());
        CodigoBeneficio codigo = new CodigoBeneficio();
        codigo.setCodigo(dto.getCodigo());
        codigo.setTipo(dto.getTipo());
        codigo.setPorcentajeDescuento(dto.getPorcentajeDescuento());
        codigo.setUsoMaximo(dto.getUsoMaximo());
        codigo.setUsoActual(0);
        codigo.setFechaExpiracion(dto.getFechaExpiracion());
        codigo.setEventoId(dto.getEventoId());
        codigo.setActivo(true);
        CodigoBeneficio guardado = codigoRepository.save(codigo);
        logger.info("Código creado con id: {}", guardado.getId());
        return guardado;
    }

    public CodigoBeneficio actualizar(Long id, CodigoBeneficioDTO dto) {
        logger.info("Actualizando código de beneficio con id: {}", id);
        CodigoBeneficio codigo = codigoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Código no encontrado con id: " + id));
        codigo.setCodigo(dto.getCodigo());
        codigo.setTipo(dto.getTipo());
        codigo.setPorcentajeDescuento(dto.getPorcentajeDescuento());
        codigo.setUsoMaximo(dto.getUsoMaximo());
        codigo.setFechaExpiracion(dto.getFechaExpiracion());
        codigo.setEventoId(dto.getEventoId());
        return codigoRepository.save(codigo);
    }

    public void eliminar(Long id) {
        logger.info("Eliminando código de beneficio con id: {}", id);
        if (!codigoRepository.existsById(id)) {
            throw new RuntimeException("Código no encontrado con id: " + id);
        }
        codigoRepository.deleteById(id);
    }

    public Map<String, Object> validarCodigo(String codigo) {
        logger.info("Validando código de beneficio: {}", codigo);
        Map<String, Object> resultado = new HashMap<>();

        CodigoBeneficio cb = codigoRepository.findByCodigo(codigo)
            .orElseThrow(() -> new RuntimeException("Código no válido: " + codigo));

        if (!cb.isActivo()) {
            resultado.put("valido", false);
            resultado.put("mensaje", "Código inactivo");
            return resultado;
        }
        if (cb.getUsoActual() >= cb.getUsoMaximo()) {
            resultado.put("valido", false);
            resultado.put("mensaje", "Código agotado");
            return resultado;
        }
        if (cb.getFechaExpiracion() != null && LocalDateTime.now().isAfter(cb.getFechaExpiracion())) {
            resultado.put("valido", false);
            resultado.put("mensaje", "Código expirado");
            return resultado;
        }

        cb.setUsoActual(cb.getUsoActual() + 1);
        codigoRepository.save(cb);

        resultado.put("valido", true);
        resultado.put("descuento", cb.getPorcentajeDescuento());
        resultado.put("tipo", cb.getTipo());
        resultado.put("mensaje", "Código aplicado correctamente");
        logger.info("Código {} aplicado correctamente", codigo);
        return resultado;
    }

    public CodigoBeneficio desactivar(Long id) {
        logger.warn("Desactivando código de beneficio con id: {}", id);
        CodigoBeneficio cb = codigoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Código no encontrado con id: " + id));
        cb.setActivo(false);
        return codigoRepository.save(cb);
    }

    public List<CodigoBeneficio> obtenerPorEvento(Long eventoId) {
        logger.info("Obteniendo códigos del evento: {}", eventoId);
        return codigoRepository.findByEventoId(eventoId);
    }
}