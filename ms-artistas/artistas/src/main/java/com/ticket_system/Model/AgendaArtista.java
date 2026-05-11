package com.ticket_system.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "agenda_artistas")
public class AgendaArtista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artista_id", nullable = false)
    private Artista artista;

    @NotNull(message = "El ID del evento es obligatorio")
    @Column(nullable = false)
    private Long eventoId;

    @NotBlank(message = "El nombre del evento es obligatorio")
    @Column(nullable = false)
    private String nombreEvento;

    @NotNull(message = "La fecha de presentacion es obligatoria")
    @Column(nullable = false)
    private LocalDateTime fechaPresentacion;

    @NotBlank(message = "El lugar es obligatorio")
    @Column(nullable = false)
    private String lugar;

    @Column(nullable = false)
    private String estadoAgenda;

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        this.estadoAgenda = "PENDIENTE";
    }
}