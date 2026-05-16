package com.ticket_system.streaming.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "streamings")
public class Streaming {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID del evento es obligatorio")
    @Column(nullable = false, unique = true)
    private Long eventoId;

    @NotBlank(message = "El nombre del stream es obligatorio")
    @Column(nullable = false)
    private String nombreStream;

    @NotBlank(message = "La URL del stream es obligatoria")
    @Column(nullable = false)
    private String urlStream;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Column(nullable = false)
    private LocalDateTime fechaFin;

    @NotNull(message = "La capacidad maxima es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    @Column(nullable = false)
    private Integer capacidadMaxima;

    @Column(nullable = false)
    private Integer capacidadDisponible;

    // Estado: PROGRAMADO, EN_VIVO, FINALIZADO, CANCELADO
    @Column(nullable = false)
    private String estado;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(updatable = false)
    private LocalDateTime creadoEn;

    private LocalDateTime actualizadoEn;

    // Relacion OneToMany con AccesoStreaming
    @ToString.Exclude
    @OneToMany(mappedBy = "streaming", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccesoStreaming> accesos;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
        this.estado = "PROGRAMADO";
        this.capacidadDisponible = this.capacidadMaxima;
    }

    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }
}