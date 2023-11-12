package com.bearsoft.charityrun.config.security;

import com.bearsoft.charityrun.models.SecurityAppUser;
import com.bearsoft.charityrun.repositories.TokenRepository;
import com.bearsoft.charityrun.services.security.JwtFilterService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenRepository tokenRepository;
    private final JwtFilterService jwtFilterService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,       // our intercepted request from which we can extract data
            @NonNull HttpServletResponse response,     // our response to which we can provide data with oncePerRequestFilter
            @NonNull FilterChain filterChain)          // the filter chain, list of the other filters we need to execute.
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        final String bearer = "Bearer ";

        if (authHeader == null || !authHeader.startsWith(bearer)) {
            filterChain.doFilter(request, response); // pass the request and response to the next filter
            return;
        }

        jwt = authHeader.substring(bearer.length());
        userEmail = jwtFilterService.extractUsername(jwt);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            var isTokenValid = tokenRepository.findByToken(jwt)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);

            if (jwtFilterService.isTokenValid(jwt, (SecurityAppUser) userDetails) && isTokenValid) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response); // pass the hand to our next filter
    }
}
