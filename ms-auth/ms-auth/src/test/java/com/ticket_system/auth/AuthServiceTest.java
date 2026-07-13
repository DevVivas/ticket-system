package com.ticket_system.auth;

import com.ticket_system.auth.DTO.AuthResponseDTO;
import com.ticket_system.auth.DTO.LoginDTO;
import com.ticket_system.auth.DTO.RegisterDTO;
import com.ticket_system.auth.Exception.BusinessException;
import com.ticket_system.auth.Model.Usuario;
import com.ticket_system.auth.Repository.UsuarioRepository;
import com.ticket_system.auth.Security.JwtUtil;
import com.ticket_system.auth.Service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void loginWithValidCredentials() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("password123");

        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        usuario.setPassword("encodedPassword");
        usuario.setEmail("test@test.com");
        usuario.setRol("ADMIN");
        usuario.setActivo(true);

        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("testuser", "ADMIN")).thenReturn("jwt-token-123");

        AuthResponseDTO response = authService.login(dto);

        assertNotNull(response);
        assertEquals("jwt-token-123", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("ADMIN", response.getRol());
        assertEquals("Bearer", response.getTipo());
        verify(jwtUtil).generateToken("testuser", "ADMIN");
    }

    @Test
    void loginWithInvalidCredentials() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("wrongpassword");

        Usuario usuario = new Usuario();
        usuario.setUsername("testuser");
        usuario.setPassword("encodedPassword");
        usuario.setRol("ADMIN");
        usuario.setActivo(true);

        when(usuarioRepository.findByUsername("testuser")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> authService.login(dto));

        assertEquals("Usuario o contraseña incorrectos", exception.getMessage());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void registerNewUser() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("newuser");
        dto.setPassword("password123");
        dto.setEmail("new@test.com");
        dto.setRol("ADMIN");

        when(usuarioRepository.existsByUsername("newuser")).thenReturn(false);
        when(usuarioRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        Usuario savedUsuario = new Usuario();
        savedUsuario.setId(1L);
        savedUsuario.setUsername("newuser");
        savedUsuario.setPassword("encodedPassword");
        savedUsuario.setEmail("new@test.com");
        savedUsuario.setRol("ADMIN");
        savedUsuario.setActivo(true);

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(savedUsuario);

        Usuario result = authService.registrar(dto);

        assertNotNull(result);
        assertEquals("newuser", result.getUsername());
        assertEquals("new@test.com", result.getEmail());
        assertEquals("ADMIN", result.getRol());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void registerDuplicateUsername() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUsername("existinguser");
        dto.setPassword("password123");
        dto.setEmail("new@test.com");
        dto.setRol("ADMIN");

        when(usuarioRepository.existsByUsername("existinguser")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> authService.registrar(dto));

        assertTrue(exception.getMessage().contains("existinguser"));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void validateValidToken() {
        when(jwtUtil.isValidToken("valid-token")).thenReturn(true);

        boolean result = authService.validarToken("valid-token");

        assertTrue(result);
        verify(jwtUtil).isValidToken("valid-token");
    }

    @Test
    void validateInvalidToken() {
        when(jwtUtil.isValidToken("expired-token")).thenReturn(false);

        boolean result = authService.validarToken("expired-token");

        assertFalse(result);
        verify(jwtUtil).isValidToken("expired-token");
    }
}
