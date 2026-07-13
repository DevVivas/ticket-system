package com.ticket_system.auth;

import com.ticket_system.auth.Security.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String SECRET = "claveSecretaSuperSeguraParaTicketSystem2026QueDebeTenerAlMenos256Bits";
    private static final long EXPIRATION = 86400000L;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", EXPIRATION);
    }

    @Test
    void generateTokenCreatesValidToken() {
        String token = jwtUtil.generateToken("testuser", "ADMIN");

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtUtil.isValidToken(token));
    }

    @Test
    void extractUsernameReturnsCorrectUsername() {
        String token = jwtUtil.generateToken("testuser", "ADMIN");

        String username = jwtUtil.extractUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    void extractRolReturnsCorrectRole() {
        String token = jwtUtil.generateToken("testuser", "VENDEDOR");

        String rol = jwtUtil.extractRol(token);

        assertEquals("VENDEDOR", rol);
    }

    @Test
    void isValidTokenReturnsTrueForValidToken() {
        String token = jwtUtil.generateToken("testuser", "ADMIN");

        boolean valid = jwtUtil.isValidToken(token);

        assertTrue(valid);
    }

    @Test
    void isValidTokenReturnsFalseForExpiredToken() {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", "ADMIN");
        String expiredToken = Jwts.builder()
                .claims(claims)
                .subject("testuser")
                .issuedAt(new Date(System.currentTimeMillis() - 20000))
                .expiration(new Date(System.currentTimeMillis() - 10000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        boolean valid = jwtUtil.isValidToken(expiredToken);

        assertFalse(valid);
    }

    @Test
    void isValidTokenReturnsFalseForTamperedToken() {
        SecretKey differentKey = Keys.hmacShaKeyFor("differentSecretKeyThatIsAtLeast256BitsLongForTesting".getBytes(StandardCharsets.UTF_8));
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", "ADMIN");
        String tamperedToken = Jwts.builder()
                .claims(claims)
                .subject("testuser")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(differentKey, SignatureAlgorithm.HS256)
                .compact();

        boolean valid = jwtUtil.isValidToken(tamperedToken);

        assertFalse(valid);
    }
}
