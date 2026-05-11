package com.demo.sharewave.util;

//import java.awt.RenderingHints.Key;
import javax.crypto.SecretKey;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    // Yeh secret key hai — kisi ko mat batana!
    private String secret = "sharewave_secret_key_minimum_32_chars";

    // Token banao
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)          // user ka email daalo
                .setIssuedAt(new Date())    // kab banaya
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 ghante
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Token se email nikalo
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Token valid hai ya nahi
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // expire ho gaya ya invalid hai
        }
    }

    // Secret key convert karo
    private Key getSignKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
