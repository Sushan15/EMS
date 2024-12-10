package com.pelatro.signup.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;

@Component
public class JwtUtils {

    private final JwtConfig jwtConfig;

    // Constructor injection for JwtConfig
    public JwtUtils(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    // Generates JWT Token
    public String generateJwtToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtConfig.getExpirationMs()))
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret())
                .compact();
    }

    // Retrieves JWT Token from Authorization header
    public String getJwtFromCookies(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // Remove "Bearer " part from token
        }
        return null;
    }

    // Extracts the username from the JWT Token
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtConfig.getSecret())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Validates the JWT Token
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtConfig.getSecret()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // Handle exception (e.g., token expired, incorrect signature)
            return false;
        }
    }
}
