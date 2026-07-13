package com.ticketsystem.gateway.Filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString().substring(0, 8);
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        String clientIp = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        String username = request.getHeader("X-User-Username");

        long startTime = System.currentTimeMillis();

        // Agrega el requestId al header de respuesta
        response.addHeader("X-Request-Id", requestId);

        logger.info("[{}] >>> INICIO {} {} | IP: {} | User: {} | UA: {}",
                requestId, method, uri + (query != null ? "?" + query : ""),
                clientIp,
                username != null ? username : "anonymous",
                userAgent != null ? userAgent : "-");

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();

            if (status >= 500) {
                logger.error("[{}] <<< FIN {} {} | Status: {} | Duracion: {}ms",
                        requestId, method, uri, status, duration);
            } else if (status >= 400) {
                logger.warn("[{}] <<< FIN {} {} | Status: {} | Duracion: {}ms",
                        requestId, method, uri, status, duration);
            } else {
                logger.info("[{}] <<< FIN {} {} | Status: {} | Duracion: {}ms",
                        requestId, method, uri, status, duration);
            }
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
