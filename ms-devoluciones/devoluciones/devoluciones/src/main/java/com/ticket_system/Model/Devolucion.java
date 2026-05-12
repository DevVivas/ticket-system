package com.ticket_system.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "devoluciones")
public class Devolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID de la venta es obligatorio")
    @Column(nullable = false)
    private Long ventaId;

    @NotNull(message = "El ID del ticket es obligatorio")
    @Column(nullable = false)
    private Long ticketId;

    @NotBlank(message = "El motivo de la devolución es obligatorio")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String motivo;

    @NotNull(message = "El monto a devolver es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
    @Column(nullable = false)
    private Double montoDevolucion;

    // Estado: PENDIENTE, APROBADA, RECHAZADA, COMPLETADA
    @Column(nullable = false)
    private String estado;

    // Tipo: CANCELACION, ERROR_COBRO, EVENTO_CANCELADO, OTRO
    @NotBlank(message = "El tipo de devolución es obligatorio")
    @Column(nullable = false)
    private String tipoDevolucion;

    @Column(updatable = false)
    private LocalDateTime creadoEn;

    private LocalDateTime actualizadoEn;

    // Relacion OneToMany con ReembolsoDevolucion
    @ToString.Exclude
    @OneToMany(mappedBy = "devolucion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReembolsoDevolucion> reembolsos;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        this.actualizadoEn = LocalDateTime.now();
        this.estado = "PENDIENTE";
    }

    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEn = LocalDateTime.now();
    }
}