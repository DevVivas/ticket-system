package com.ticket_system.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReembolsoDTO {

    @NotNull(message = "El monto del reembolso es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
    private Double montoReembolso;

    @NotBlank(message = "El método de reembolso es obligatorio")
    private String metodoReembolso;

    private String referenciaBancaria;
}
