package com.ticket_system.auth.Controller;

import com.ticket_system.auth.DTO.AuthResponseDTO;
import com.ticket_system.auth.DTO.LoginDTO;
import com.ticket_system.auth.DTO.RegisterDTO;
import com.ticket_system.auth.Model.Usuario;
import com.ticket_system.auth.Service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@Valid @RequestBody RegisterDTO dto) {
        logger.info("[AUTH] POST /api/auth/register - username: {}", dto.getUsername());
        Usuario creado = authService.registrar(dto);
        // No devolvemos el password encriptado en la respuesta
        creado.setPassword(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        logger.info("[AUTH] POST /api/auth/login - username: {}", dto.getUsername());
        AuthResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    // POST /api/auth/validate
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validate(@RequestHeader("Authorization") String authHeader) {
        logger.info("[AUTH] POST /api/auth/validate");
        String token = authHeader.replace("Bearer ", "");
        boolean valido = authService.validarToken(token);

        Map<String, Object> response = new HashMap<>();
        response.put("valido", valido);
        return ResponseEntity.ok(response);
    }
}
