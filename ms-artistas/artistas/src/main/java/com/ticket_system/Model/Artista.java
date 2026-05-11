package com.ticket_system.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "artistas")
public class Artista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del artista es obligatorio")
    @Size(max = 150, message = "El nombre no puede exceder 150 caracteres")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El género musical es obligatorio")
    @Column(nullable = false)
    private String genero;

    @NotBlank(message = "La nacionalidad es obligatoria")
    @Column(nullable = false)
    private String nacionalidad;

    @Column(columnDefinition = "TEXT")
    private String biografia;

    @Size(max = 300)
    private String imagenUrl;

    @Size(max = 300)
    private String sitioWeb;

    @Column(nullable = false)
    private String estado;

    @Column(updatable = false)
    private LocalDateTime creadoEn;

    private LocalDateTime actualizadoEn;

    @OneToMany(mappedBy = "artista", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AgendaArtista> agenda;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
        this.estado = "ACTIVO";
    }

    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }
}