package com.ticket_system.streaming.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "accesos_streaming")
public class AccesoStreaming {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacion ManyToOne con Streaming
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "streaming_id", nullable = false)
    private Streaming streaming;

    @NotNull(message = "El ID del ticket es obligatorio")
    @Column(nullable = false)
    private Long ticketId;

    @NotBlank(message = "El nombre del espectador es obligatorio")
    @Column(nullable = false)
    private String nombreEspectador;

    @NotBlank(message = "El email del espectador es obligatorio")
    @Email(message = "El email no es válido")
    @Column(nullable = false)
    private String emailEspectador;

    // Codigo unico de acceso generado automaticamente
    @Column(nullable = false, unique = true)
    private String codigoAcceso;

    // Estado: ACTIVO, USADO, REVOCADO
    @Column(nullable = false)
    private String estadoAcceso;

    @Column(updatable = false)
    private LocalDateTime creadoEn;

    private LocalDateTime usadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        this.estadoAcceso = "ACTIVO";
        // Genera codigo unico de acceso
        this.codigoAcceso = "STR-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 9999);
    }
}