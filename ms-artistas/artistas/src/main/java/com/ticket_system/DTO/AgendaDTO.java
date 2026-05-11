package com.ticket_system.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AgendaDTO {

    @NotNull(message = "El ID del evento es obligatorio")
    private Long eventoId;

    @NotBlank(message = "El nombre del evento es obligatorio")
    private String nombreEvento;

    @NotNull(message = "La fecha de presentacion es obligatoria")
    private LocalDateTime fechaPresentacion;

    @NotBlank(message = "El lugar es obligatorio")
    private String lugar;

    private String notas;
}