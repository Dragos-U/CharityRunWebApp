package com.bearsoft.charityrun.services.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import com.bearsoft.charityrun.models.SecurityAppUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtFilterService {

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(SecurityAppUser securityAppUser) {
        return generateToken(new HashMap<>(), securityAppUser);
    }
    public String generateToken(
            Map<String, Object> extraClaims,
            SecurityAppUser securityAppUser
    ) {
        return buildToken(extraClaims, securityAppUser, jwtExpiration);
    }

    public String generateRefreshToken(SecurityAppUser securityAppUser) {
        return buildToken(new HashMap<>(), securityAppUser, refreshExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            SecurityAppUser securityAppUser,
            long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(securityAppUser.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, SecurityAppUser securityAppUser) {
        final String username = extractUsername(token);
        return (username.equals(securityAppUser.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // when we try to decode a token we need a signing key.
    // sigining key - secret used to sign the token.
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
