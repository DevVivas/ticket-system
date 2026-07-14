package com.ticket_system.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket_system.auth.Controller.AuthController;
import com.ticket_system.auth.DTO.AuthResponseDTO;
import com.ticket_system.auth.DTO.LoginDTO;
import com.ticket_system.auth.DTO.RegisterDTO;
import com.ticket_system.auth.Exception.BusinessException;
import com.ticket_system.auth.Model.Usuario;
import com.ticket_system.auth.Service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void loginSuccess() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password123");

        AuthResponseDTO responseDTO = new AuthResponseDTO("jwt-token", "testuser", "ADMIN", "Bearer");
        when(authService.login(any(LoginDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.rol").value("ADMIN"))
                .andExpect(jsonPath("$.tipo").value("Bearer"));
    }

    @Test
    void loginInvalidCredentials() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("wrongpassword");

        when(authService.login(any(LoginDTO.class)))
                .thenThrow(new BadCredentialsException("Usuario o contraseña incorrectos"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerSuccess() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("newuser");
        registerDTO.setPassword("password123");
        registerDTO.setEmail("new@test.com");
        registerDTO.setRol("ADMIN");

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("newuser");
        usuario.setEmail("new@test.com");
        usuario.setRol("ADMIN");

        when(authService.registrar(any(RegisterDTO.class))).thenReturn(usuario);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("new@test.com"))
                .andExpect(jsonPath("$.rol").value("ADMIN"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void registerDuplicateUser() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("existinguser");
        registerDTO.setPassword("password123");
        registerDTO.setEmail("existing@test.com");
        registerDTO.setRol("ADMIN");

        when(authService.registrar(any(RegisterDTO.class)))
                .thenThrow(new BusinessException("El username ya está en uso: existinguser"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void validateValidToken() throws Exception {
        when(authService.validarToken("valid-token")).thenReturn(true);

        mockMvc.perform(post("/api/auth/validate")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(true));
    }

    @Test
    void validateInvalidToken() throws Exception {
        when(authService.validarToken("invalid-token")).thenReturn(false);

        mockMvc.perform(post("/api/auth/validate")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(false));
    }
}
