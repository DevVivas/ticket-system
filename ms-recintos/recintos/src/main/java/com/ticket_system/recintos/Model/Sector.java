package com.ticket_system.recintos.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "sectores")
public class Sector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del sector es obligatorio")
    private String nombre; // VIP, PLATEA, GALERIA

    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    private int capacidad;

    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private double precioBase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recinto_id", nullable = false)
    private Recinto recinto;

    public Sector() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
    public double getPrecioBase() { return precioBase; }
    public void setPrecioBase(double precioBase) { this.precioBase = precioBase; }
    public Recinto getRecinto() { return recinto; }
    public void setRecinto(Recinto recinto) { this.recinto = recinto; }
}
