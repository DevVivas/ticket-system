package com.ticket_system.promotores.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PromotorDTO {

    @NotBlank(message = "El nombre del promotor es obligatorio")
    @Size(max = 150)
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @NotNull(message = "El porcentaje de comisión es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false)
    @DecimalMax(value = "100.0")
    private Double porcentajeComision;
}