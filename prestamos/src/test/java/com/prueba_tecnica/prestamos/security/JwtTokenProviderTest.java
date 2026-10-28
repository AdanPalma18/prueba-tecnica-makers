package com.prueba_tecnica.prestamos.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Base64;

import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    @Test
    void generatesAndValidatesTokenForEmail() {
        JwtTokenProvider provider = new JwtTokenProvider(
                Base64.getEncoder().encodeToString(new byte[32]), 60_000);

        String token = provider.generateToken("persona@example.com");

        assertTrue(provider.validateToken(token));
        assertEquals("persona@example.com", provider.getEmailFromToken(token));
    }

    @Test
    void rejectsTokenSignedWithAnotherKey() {
        byte[] otherKey = new byte[32];
        otherKey[0] = 1;
        JwtTokenProvider provider = new JwtTokenProvider(
                Base64.getEncoder().encodeToString(new byte[32]), 60_000);
        JwtTokenProvider otherProvider = new JwtTokenProvider(
                Base64.getEncoder().encodeToString(otherKey), 60_000);

        String token = otherProvider.generateToken("persona@example.com");

        assertFalse(provider.validateToken(token));
    }
}
