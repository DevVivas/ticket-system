package com.ticket_system.promotores.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comisiones_promotor")
public class ComisionPromotor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotor_id", nullable = false)
    private Promotor promotor;

    @NotNull(message = "El ID de la venta es obligatorio")
    @Column(nullable = false)
    private Long ventaId;

    @NotNull(message = "El monto de la venta es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
    @Column(nullable = false)
    private Double montoVenta;

    @Column(nullable = false)
    private Double porcentajeAplicado;

    @Column(nullable = false)
    private Double montoComision;

    // Estado: PENDIENTE, PAGADA, ANULADA
    @Column(nullable = false)
    private String estadoComision;

    @Column(updatable = false)
    private LocalDateTime creadoEn;

    private LocalDateTime pagadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        this.estadoComision = "PENDIENTE";
    }
}