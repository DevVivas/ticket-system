package com.ticketsystem.gateway;

import com.ticketsystem.gateway.Security.JwtAuthenticationFilter;
import com.ticketsystem.gateway.Security.JwtUtil;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void validBearerTokenSetsHeadersAndContinuesFilterChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token-abc");
        request.setRequestURI("/api/eventos");
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtil.isValidToken("valid-token-abc")).thenReturn(true);
        when(jwtUtil.extractUsername("valid-token-abc")).thenReturn("adminuser");
        when(jwtUtil.extractRol("valid-token-abc")).thenReturn("ADMIN");

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        ArgumentCaptor<jakarta.servlet.http.HttpServletRequest> requestCaptor =
                ArgumentCaptor.forClass(jakarta.servlet.http.HttpServletRequest.class);
        verify(filterChain).doFilter(requestCaptor.capture(), eq(response));

        jakarta.servlet.http.HttpServletRequest capturedRequest = requestCaptor.getValue();
        assertEquals("adminuser", capturedRequest.getHeader("X-User-Username"));
        assertEquals("ADMIN", capturedRequest.getHeader("X-User-Rol"));
        assertEquals(200, response.getStatus());
    }

    @Test
    void missingAuthorizationHeaderContinuesFilterChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/auth/login");
        request.setMethod("POST");
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(same(request), same(response));
        assertEquals(200, response.getStatus());
    }

    @Test
    void invalidTokenReturns401() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");
        request.setRequestURI("/api/eventos");
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtil.isValidToken("invalid-token")).thenReturn(false);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(), any());
        assertEquals(401, response.getStatus());
        assertEquals("application/json", response.getContentType());
    }

    @Test
    void expiredTokenReturns401() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer expired-token");
        request.setRequestURI("/api/tickets");
        request.setMethod("GET");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtil.isValidToken("expired-token")).thenReturn(false);

        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(), any());
        assertEquals(401, response.getStatus());
    }
}
