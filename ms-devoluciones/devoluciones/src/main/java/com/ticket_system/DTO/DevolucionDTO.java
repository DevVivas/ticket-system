package com.ticket_system.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DevolucionDTO {

    @NotNull(message = "El ID de la venta es obligatorio")
    private Long ventaId;

    @NotNull(message = "El ID del ticket es obligatorio")
    private Long ticketId;

    @NotBlank(message = "El motivo de la devolución es obligatorio")
    private String motivo;

    @NotNull(message = "El monto a devolver es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
    private Double montoDevolucion;

    @NotBlank(message = "El tipo de devolución es obligatorio")
    private String tipoDevolucion;
}