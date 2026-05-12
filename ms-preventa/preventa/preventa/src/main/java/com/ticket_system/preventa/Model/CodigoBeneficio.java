package com.ticket_system.preventa.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "codigos_beneficio")
public class CodigoBeneficio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @NotBlank(message = "El tipo es obligatorio")
    private String tipo; // DESCUENTO, 2X1, ACCESO_ANTICIPADO

    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    @DecimalMax(value = "100.0", message = "El descuento no puede superar el 100%")
    private double porcentajeDescuento;

    @Min(value = 1, message = "El uso máximo debe ser mayor a 0")
    private int usoMaximo;

    private int usoActual;

    private LocalDateTime fechaExpiracion;

    private Long eventoId;

    private boolean activo;

    public CodigoBeneficio() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public double getPorcentajeDescuento() { return porcentajeDescuento; }
    public void setPorcentajeDescuento(double porcentajeDescuento) { this.porcentajeDescuento = porcentajeDescuento; }
    public int getUsoMaximo() { return usoMaximo; }
    public void setUsoMaximo(int usoMaximo) { this.usoMaximo = usoMaximo; }
    public int getUsoActual() { return usoActual; }
    public void setUsoActual(int usoActual) { this.usoActual = usoActual; }
    public LocalDateTime getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(LocalDateTime fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
