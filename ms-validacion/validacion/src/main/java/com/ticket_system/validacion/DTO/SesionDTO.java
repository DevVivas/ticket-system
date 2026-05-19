package com.ticket_system.validacion.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SesionDTO {

    @NotNull(message = "El ID del evento es obligatorio")
    private Long eventoId;

    @NotBlank(message = "El nombre del portero es obligatorio")
    private String nombrePortero;

    @NotBlank(message = "El puesto de acceso es obligatorio")
    private String puestoAcceso;
}