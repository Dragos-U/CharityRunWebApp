package com.bearsoft.charityrun.services.security;

import com.bearsoft.charityrun.repositories.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        // we want to invalidate the token. get the token from the request,
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String bearer = "Bearer ";
        if (authHeader == null || !authHeader.startsWith(bearer)) {
            return;
        }
        jwt = authHeader.substring(bearer.length());
        var storedToken = tokenRepository.findByToken(jwt)
                .orElse(null);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
        }
    }
}