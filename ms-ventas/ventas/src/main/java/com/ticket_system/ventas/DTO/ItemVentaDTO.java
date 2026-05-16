package com.ticket_system.ventas.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ItemVentaDTO {

    @NotNull(message = "El ticketId es obligatorio")
    private Long ticketId;

    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private double precioUnitario;

    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private int cantidad;

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
