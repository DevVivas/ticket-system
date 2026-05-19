package com.ticket_system.validacion.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "validaciones_ticket")
public class ValidacionTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_id", nullable = false)
    private SesionValidacion sesion;

    @NotBlank(message = "El codigo QR es obligatorio")
    @Column(nullable = false)
    private String codigoQR;

    @NotNull(message = "El ID del ticket es obligatorio")
    @Column(nullable = false)
    private Long ticketId;

    // Resultado: VALIDO, INVALIDO, YA_USADO, EVENTO_INCORRECTO
    @Column(nullable = false)
    private String resultado;

    private String detalleResultado;

    @Column(updatable = false)
    private LocalDateTime escaneadoEn;

    @PrePersist
    protected void onCreate() {
        this.escaneadoEn = LocalDateTime.now();
    }
}