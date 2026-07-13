package com.ticket_system.auth.Service;

import com.ticket_system.auth.DTO.AuthResponseDTO;
import com.ticket_system.auth.DTO.LoginDTO;
import com.ticket_system.auth.DTO.RegisterDTO;
import com.ticket_system.auth.Exception.BusinessException;
import com.ticket_system.auth.Model.Usuario;
import com.ticket_system.auth.Repository.UsuarioRepository;
import com.ticket_system.auth.Security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private static final List<String> ROLES_VALIDOS = List.of("ADMIN", "VENDEDOR", "PORTERO");

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Usuario registrar(RegisterDTO dto) {
        logger.info("[AUTH] Registrando nuevo usuario: {}", dto.getUsername());

        // Regla de negocio: username unico
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException("El username ya está en uso: " + dto.getUsername());
        }

        // Regla de negocio: email unico
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("El email ya está registrado: " + dto.getEmail());
        }

        // Regla de negocio: rol valido
        if (!ROLES_VALIDOS.contains(dto.getRol().toUpperCase())) {
            throw new BusinessException("Rol inválido. Roles permitidos: " + ROLES_VALIDOS);
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        // BCrypt encripta el password antes de guardarlo - nunca se guarda en texto plano
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setEmail(dto.getEmail());
        usuario.setRol(dto.getRol().toUpperCase());

        Usuario guardado = usuarioRepository.save(usuario);
        logger.info("[AUTH] Usuario registrado con id: {} y rol: {}", guardado.getId(), guardado.getRol());
        return guardado;
    }

    public AuthResponseDTO login(LoginDTO dto) {
        logger.info("[AUTH] Intento de login para usuario: {}", dto.getUsername());

        Usuario usuario = usuarioRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Usuario o contraseña incorrectos"));

        // Compara el password en texto plano contra el hash guardado
        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            logger.warn("[AUTH] Password incorrecto para usuario: {}", dto.getUsername());
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
        }

        if (!usuario.isActivo()) {
            throw new BusinessException("El usuario está inactivo. Contacte al administrador.");
        }

        String token = jwtUtil.generateToken(usuario.getUsername(), usuario.getRol());
        logger.info("[AUTH] Login exitoso para usuario: {} con rol: {}", usuario.getUsername(), usuario.getRol());

        return new AuthResponseDTO(token, usuario.getUsername(), usuario.getRol(), "Bearer");
    }

    public boolean validarToken(String token) {
        logger.info("[AUTH] Validando token");
        return jwtUtil.isValidToken(token);
    }
}
