package com.ticket_system.tickets.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class TicketDTO {

    @NotNull(message = "El eventoId es obligatorio")
    private Long eventoId;

    @NotNull(message = "El sectorId es obligatorio")
    private Long sectorId;

    @NotNull(message = "El compradorId es obligatorio")
    private Long compradorId;

    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private double precio;

    public Long getEventoId() { return eventoId; }
    public void setEventoId(Long eventoId) { this.eventoId = eventoId; }
    public Long getSectorId() { return sectorId; }
    public void setSectorId(Long sectorId) { this.sectorId = sectorId; }
    public Long getCompradorId() { return compradorId; }
    public void setCompradorId(Long compradorId) { this.compradorId = compradorId; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
}
