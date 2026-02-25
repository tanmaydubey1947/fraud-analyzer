package com.fraud_analyzer.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private final String secretKey = "veryStrongSecretKeyChangeInProd"; //TODO: Move to config
    private final long validityInMs = 15 * 60 * 1000; //TODO: Move to config

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // 1️⃣ Generate JWT
    public String generateToken(CustomUserPrincipal principal) {

        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userUuid", principal.getUserUuid());
        claims.put("orgUuid", principal.getOrgUuid());
        claims.put("permissions",
                principal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );

        return Jwts.builder()
                .setSubject(principal.getUsername())
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 2️⃣ Validate JWT
    public boolean validate(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    // 3️⃣ Extract Claims
    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 4️⃣ Extract Token From Header
    public String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    // 5️⃣ Build Authentication Object From Token
    public Authentication getAuthentication(String token) {

        Claims claims = getClaims(token);

        String email = claims.getSubject();
        String orgUuid = claims.get("orgUuid", String.class);

        List<String> permissions =
                claims.get("permissions", List.class);

        List<SimpleGrantedAuthority> authorities =
                permissions.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        return new UsernamePasswordAuthenticationToken(
                email,
                null,
                authorities
        );
    }
}