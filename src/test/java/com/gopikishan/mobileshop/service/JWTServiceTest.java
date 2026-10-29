package com.gopikishan.mobileshop.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JWTServiceTest {

    private JWTService jwtService;

    private final String secretKey =
            "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

    @BeforeEach
    void setUp() {
        jwtService = new JWTService();

        ReflectionTestUtils.setField(
                jwtService,
                "secretKey",
                secretKey
        );
    }

    @Test
    void generateTokenShouldGenerateValidToken() {
        String email = "gopi@example.com";

        String token = jwtService.generateToken(email);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractEmailShouldReturnEmailFromToken() {
        String email = "gopi@example.com";

        String token = jwtService.generateToken(email);

        String extractedEmail = jwtService.extractEmail(token);

        assertEquals(email, extractedEmail);
    }

    @Test
    void isTokenValidShouldReturnTrueForMatchingEmail() {
        String email = "gopi@example.com";

        String token = jwtService.generateToken(email);

        boolean isValid = jwtService.isTokenValid(token, email);

        assertTrue(isValid);
    }

    @Test
    void isTokenValidShouldReturnFalseForDifferentEmail() {
        String token = jwtService.generateToken("gopi@example.com");

        boolean isValid = jwtService.isTokenValid(
                token,
                "other@example.com"
        );

        assertFalse(isValid);
    }

    @Test
    void extractEmailShouldThrowExceptionForInvalidToken() {
        String invalidToken = "this-is-not-a-valid-jwt";

        assertThrows(
                Exception.class,
                () -> jwtService.extractEmail(invalidToken)
        );
    }

    @Test
    void isTokenValidShouldThrowExceptionForInvalidToken() {
        String invalidToken = "invalid-token";

        assertThrows(
                Exception.class,
                () -> jwtService.isTokenValid(
                        invalidToken,
                        "gopi@example.com"
                )
        );
    }

    @Test
    void generateTokenShouldContainExpiration() {
        String token = jwtService.generateToken("gopi@example.com");

        Date expiration = Jwts.parser()
                .verifyWith(
                        Keys.hmacShaKeyFor(
                                Decoders.BASE64.decode(secretKey)
                        )
                )
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }



}