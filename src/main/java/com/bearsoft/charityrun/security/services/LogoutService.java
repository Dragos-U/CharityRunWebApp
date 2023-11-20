package com.bearsoft.charityrun.security.services;

import com.bearsoft.charityrun.repositories.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
@Slf4j
@Service
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    @Override
    @Transactional
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String bearer = "Bearer ";
        if (authHeader == null || !authHeader.startsWith(bearer)) {
            log.warn("Authorization header is missing or does not start with Bearer");
            return;
        }
        jwt = authHeader.substring(bearer.length());
        try{
            var storedToken = tokenRepository.findByToken(jwt)
                    .orElse(null);
            if (storedToken != null) {
                storedToken.setExpired(true);
                storedToken.setRevoked(true);
                tokenRepository.save(storedToken);
                log.info("Token successfully revoked for jwt: {}", jwt);
                sendResponse(response, HttpServletResponse.SC_OK, "Logout successful");
            } else {
                log.warn("Attempted to logout with an unrecognized jwt: {}", jwt);
                sendResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Unrecognized JWT");
            }
        } catch (Exception e){
            log.error("Error during logout process for jwt: {}", jwt, e);
            sendResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }
    private void sendResponse(HttpServletResponse response, int status, String message) {
        response.setStatus(status);
        response.setContentType("application/json");
        try {
            PrintWriter out = response.getWriter();
            out.print("{\"message\": \"" + message + "\"}");
            out.flush();
        } catch (IOException e) {
            log.error("Error writing to response", e);
        }
    }
}