package com.bearsoft.charityrun.config.security;

import com.bearsoft.charityrun.services.security.JwtFilterService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilterConfig extends OncePerRequestFilter {

    public static final String JWT_TOKEN_EXPIRED = "JWT token expired.";
    private final JwtFilterService jwtFilterService;
    private final UserDetailsService userDetailsService;

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            final String authHeader = request.getHeader(AUTHORIZATION_HEADER);
            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = authHeader.substring(BEARER_PREFIX.length());
            String userEmail = jwtFilterService.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                authenticateUser(request, userEmail);
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            log.error(JWT_TOKEN_EXPIRED + " {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(JWT_TOKEN_EXPIRED + " -> ".concat(e.getMessage()));
        }
    }

    private void authenticateUser(
            HttpServletRequest request,
            String userEmail) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        setAuthenticationToken(request, userDetails);
    }

    private void setAuthenticationToken(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}