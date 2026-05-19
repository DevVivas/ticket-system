package com.ticket_system.validacion.DTO;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ValidacionDTO {

    @NotBlank(message = "El codigo QR es obligatorio")
    private String codigoQR;

    @NotNull(message = "El ID del ticket es obligatorio")
    private Long ticketId;
}