package com.bearsoft.charityrun.security;

import com.bearsoft.charityrun.models.SecurityAppUser;
import com.bearsoft.charityrun.services.AppUserService;
import com.bearsoft.charityrun.services.JwtService;
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

    private final JwtService jwtService;
    private final UserDetailsService appUserService;    // can ? I can use UserDetails interface

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
        userEmail = jwtService.extractUsername(jwt);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.appUserService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, (SecurityAppUser) userDetails)) {
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
