package com.ticket_system.auth.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El username es obligatorio")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "El password es obligatorio")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "El email es obligatorio")
    @Column(nullable = false, unique = true)
    private String email;

    // Rol: ADMIN, VENDEDOR, PORTERO
    @NotBlank(message = "El rol es obligatorio")
    @Column(nullable = false)
    private String rol;

    @Column(nullable = false)
    private boolean activo;

    @Column(updatable = false)
    private LocalDateTime creadoEn;

    @PrePersist
    protected void onCreate() {
        this.creadoEn = LocalDateTime.now();
        this.activo = true;
    }
}
