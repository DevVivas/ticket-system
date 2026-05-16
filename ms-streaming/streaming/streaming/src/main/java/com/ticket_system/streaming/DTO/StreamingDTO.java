package com.ticket_system.streaming.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StreamingDTO {

    @NotNull(message = "El ID del evento es obligatorio")
    private Long eventoId;

    @NotBlank(message = "El nombre del stream es obligatorio")
    private String nombreStream;

    @NotBlank(message = "La URL del stream es obligatoria")
    private String urlStream;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaFin;

    @NotNull(message = "La capacidad maxima es obligatoria")
    @Min(value = 1)
    private Integer capacidadMaxima;

    private String descripcion;
}