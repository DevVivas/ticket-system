package com.ticket_system.promotores.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ComisionDTO {

    @NotNull(message = "El ID de la venta es obligatorio")
    private Long ventaId;

    @NotNull(message = "El monto de la venta es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
    private Double montoVenta;
}