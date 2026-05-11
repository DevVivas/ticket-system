package com.ticket_system.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ArtistaDTO {

    @NotBlank(message = "El nombre del artista es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    private String nombre;

    @NotBlank(message = "El género musical es obligatorio")
    private String genero;

    @NotBlank(message = "La nacionalidad es obligatoria")
    private String nacionalidad;

    private String biografia;

    @Size(max = 300)
    private String imagenUrl;

    @Size(max = 300)
    private String sitioWeb;
}