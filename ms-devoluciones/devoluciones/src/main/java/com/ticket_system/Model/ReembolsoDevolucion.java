package com.ticket_system.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reembolsos_devolucion")
public class ReembolsoDevolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacion ManyToOne con Devolucion
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devolucion_id", nullable = false)
    private Devolucion devolucion;

    @NotNull(message = "El monto del reembolso es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
    @Column(nullable = false)
    private Double montoReembolso;

    // Metodo: TRANSFERENCIA, TARJETA, CREDITO_TIENDA
    @NotBlank(message = "El método de reembolso es obligatorio")
    @Column(nullable = false)
    private String metodoReembolso;

    // Estado: PENDIENTE, PROCESADO, FALLIDO
    @Column(nullable = false)
    private String estadoReembolso;

    private String referenciaBancaria;

    @Column(updatable = false)
    private LocalDateTime creadoEn;

    private LocalDateTime procesadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        this.estadoReembolso = "PENDIENTE";
    }
}
