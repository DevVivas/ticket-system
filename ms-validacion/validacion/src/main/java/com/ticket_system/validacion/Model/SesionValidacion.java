package com.ticket_system.validacion.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "sesiones_validacion")
public class SesionValidacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID del evento es obligatorio")
    @Column(nullable = false)
    private Long eventoId;

    @NotBlank(message = "El nombre del portero es obligatorio")
    @Column(nullable = false)
    private String nombrePortero;

    @NotBlank(message = "El puesto de acceso es obligatorio")
    @Column(nullable = false)
    private String puestoAcceso;

    // Estado: ACTIVA, CERRADA
    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private Integer totalEscaneados;

    @Column(updatable = false)
    private LocalDateTime iniciadaEn;

    private LocalDateTime cerradaEn;

    @ToString.Exclude
    @OneToMany(mappedBy = "sesion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ValidacionTicket> validaciones;

    @PrePersist
    protected void onCreate() {
        this.iniciadaEn = LocalDateTime.now();
        this.estado = "ACTIVA";
        this.totalEscaneados = 0;
    }
}