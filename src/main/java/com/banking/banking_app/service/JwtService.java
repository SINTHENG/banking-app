package com.banking.banking_app.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    //  Generate Token
    public String generateToken(UserDetails user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + expiration)
                )
                .signWith(getKey())
                .compact();
    }

    //  ទាញ Email ពី Token
    public String extractEmail(String token) {
        return getClaims(token).getSubject(); //  getClaims (មាន s)
    }

    //  ពិនិត្យ Token Valid?
    public boolean isTokenValid(String token, UserDetails user) {
        return extractEmail(token).equals(user.getUsername())
                && !isExpired(token);
    }

    // ====== Private Methods ======

    private boolean isExpired(String token) {
        return getClaims(token)
                .getExpiration()
                .before(new Date());
    }

    //  getClaims (មាន s)
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    //  Return SecretKey (javax.crypto.SecretKey)
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}