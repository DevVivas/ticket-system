package com.ticket_system.recintos.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class SectorDTO {

    @NotBlank(message = "El nombre del sector es obligatorio")
    private String nombre;

    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    private int capacidad;

    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private double precioBase;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
    public double getPrecioBase() { return precioBase; }
    public void setPrecioBase(double precioBase) { this.precioBase = precioBase; }
}
