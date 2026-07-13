package com.ticketsystem.gateway.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;

    JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // Headers de autenticacion
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                if (jwtUtil.isValidToken(token)) {
                    String username = jwtUtil.extractUsername(token);
                    String rol = jwtUtil.extractRol(token);

                    logger.debug("[JWT] Token valido para usuario '{}' con rol '{}' en {} {}",
                            username, rol, method, path);

                    // Agrega headers con info del usuario para los microservicios downstream
                    jakarta.servlet.http.HttpServletRequestWrapper wrappedRequest =
                            new jakarta.servlet.http.HttpServletRequestWrapper(request) {
                                @Override
                                public String getHeader(String name) {
                                    if ("X-User-Username".equals(name)) return username;
                                    if ("X-User-Rol".equals(name)) return rol;
                                    return super.getHeader(name);
                                }
                            };

                    filterChain.doFilter(wrappedRequest, response);
                } else {
                    logger.warn("[JWT] Token invalido o expirado en {} {}", method, path);
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Token invalido o expirado\"}");
                }
            } catch (Exception e) {
                logger.error("[JWT] Error al validar token en {} {}: {}", method, path, e.getMessage());
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Token invalido\"}");
            }
        } else {
            // Sin token - permitir pasar (algunos endpoints pueden ser publicos)
            filterChain.doFilter(request, response);
        }
    }
}
