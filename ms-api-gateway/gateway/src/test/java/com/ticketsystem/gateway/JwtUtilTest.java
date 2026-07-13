package com.ticketsystem.gateway;

import com.ticketsystem.gateway.Security.JwtUtil;
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

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
    }

    private String generateToken(String username, String rol) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", rol);
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void extractUsernameReturnsCorrectUsername() {
        String token = generateToken("testuser", "ADMIN");

        String username = jwtUtil.extractUsername(token);

        assertEquals("testuser", username);
    }

    @Test
    void extractRolReturnsCorrectRole() {
        String token = generateToken("testuser", "PORTERO");

        String rol = jwtUtil.extractRol(token);

        assertEquals("PORTERO", rol);
    }

    @Test
    void isValidTokenReturnsTrueForValidToken() {
        String token = generateToken("testuser", "ADMIN");

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
    void isValidTokenReturnsFalseForInvalidSignature() {
        SecretKey differentKey = Keys.hmacShaKeyFor("anotherSecretKeyThatIsDefinitelyDifferentAnd256Bits".getBytes(StandardCharsets.UTF_8));
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", "ADMIN");
        String tokenWithBadSignature = Jwts.builder()
                .claims(claims)
                .subject("testuser")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(differentKey, SignatureAlgorithm.HS256)
                .compact();

        boolean valid = jwtUtil.isValidToken(tokenWithBadSignature);

        assertFalse(valid);
    }

    @Test
    void isValidTokenReturnsFalseForMalformedToken() {
        boolean valid = jwtUtil.isValidToken("not.a.valid.jwt.token");

        assertFalse(valid);
    }
}
