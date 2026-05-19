package com.ticket_system.streaming.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AccesoDTO {

    @NotNull(message = "El ID del ticket es obligatorio")
    private Long ticketId;

    @NotBlank(message = "El nombre del espectador es obligatorio")
    private String nombreEspectador;

    @NotBlank(message = "El email del espectador es obligatorio")
    @Email(message = "El email no es válido")
    private String emailEspectador;
}