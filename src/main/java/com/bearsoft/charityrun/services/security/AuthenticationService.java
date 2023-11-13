package com.bearsoft.charityrun.services.security;

import com.bearsoft.charityrun.models.dtos.AuthenticationRequestDTO;
import com.bearsoft.charityrun.models.dtos.AuthenticationResponseDTO;
import com.bearsoft.charityrun.models.SecurityAppUser;
import com.bearsoft.charityrun.models.dtos.AppUserDTO;
import com.bearsoft.charityrun.models.entities.AppUser;
import com.bearsoft.charityrun.models.entities.RefreshToken;
import com.bearsoft.charityrun.models.entities.Role;
import com.bearsoft.charityrun.models.entities.Token;
import com.bearsoft.charityrun.models.enums.RoleType;
import com.bearsoft.charityrun.models.enums.TokenType;
import com.bearsoft.charityrun.repositories.AppUserRepository;
import com.bearsoft.charityrun.repositories.RefreshTokenRepository;
import com.bearsoft.charityrun.repositories.TokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AppUserRepository appUserRepository;
    private final TokenRepository tokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtFilterService jwtFilterService;
    private final PasswordEncoder passwordEncoder;

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER  = "Authorization";

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public AuthenticationResponseDTO registerAppUser(AppUserDTO appUserDTO) {
        var appUser = AppUser.builder()
                .firstName(appUserDTO.getFirstName())
                .lastName(appUserDTO.getLastName())
                .email(appUserDTO.getEmail())
                .password(passwordEncoder.encode(appUserDTO.getPassword()))
                .roles(new HashSet<>(List.of(Role.builder().roleType(RoleType.ROLE_USER).build())))
                .address(appUserDTO.getAddress())
                .build();
        appUserRepository.save(appUser);

        SecurityAppUser securityAppUser = new SecurityAppUser(appUser);
        var jwtToken = jwtFilterService.generateToken(securityAppUser);
        var refreshToken = jwtFilterService.generateRefreshToken(securityAppUser);

        saveJwtTokenToRepo(appUser, jwtToken);
        saveRefreshTokenToRepo(appUser, refreshToken);

        return AuthenticationResponseDTO.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponseDTO authenticateAppUser(AuthenticationRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));
        var appUser = appUserRepository.findAppUsersByEmail(request.getEmail())
                .orElseThrow();

        SecurityAppUser securityAppUser = new SecurityAppUser(appUser);
        var jwtToken = jwtFilterService.generateToken(securityAppUser);
        var refreshToken = jwtFilterService.generateRefreshToken(securityAppUser);
        revokeAllAppUserTokens(appUser);
        saveJwtTokenToRepo(appUser, jwtToken);

        return AuthenticationResponseDTO.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void revokeAllAppUserTokens(AppUser appUser) {
        var validAppUserToken = tokenRepository.findAllValidTokensByUser(appUser.getId());
        if (validAppUserToken.isEmpty())
            return;
        validAppUserToken.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validAppUserToken);
    }

    private void saveJwtTokenToRepo(AppUser appUser, String jwtToken) {
        var token = Token.builder()
                .appUser(appUser)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    private void saveRefreshTokenToRepo(AppUser appUser, String refreshToken) {
        var token = RefreshToken.builder()
                .appUser(appUser)
                .token(refreshToken)
                .expiryDate(Instant.now().plusMillis(refreshExpiration))
                .build();
        refreshTokenRepository.save(token);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        String refreshToken = extractTokenFromRequest(request, BEARER_PREFIX);
        if (refreshToken == null) {
            return;
        }

        String userEmail = jwtFilterService.extractUsername(refreshToken);
        Optional<AppUser> appUserOptional = appUserRepository.findAppUsersByEmail(userEmail);

        if (appUserOptional.isPresent() && isTokenValidForUser(refreshToken, appUserOptional.get())) {
            SecurityAppUser securityAppUser = new SecurityAppUser(appUserOptional.get());
            String accessToken = jwtFilterService.generateToken(securityAppUser);
            updateAndSaveTokens(appUserOptional.get(), accessToken, refreshToken, response);
        }
    }

    private String extractTokenFromRequest(
            HttpServletRequest request,
            String tokenPrefix) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(tokenPrefix)) {
            return authHeader.substring(tokenPrefix.length());
        }
        return null;
    }

    private boolean isTokenValidForUser(String token, AppUser appUser) {
        SecurityAppUser securityAppUser = new SecurityAppUser(appUser);
        return jwtFilterService.isTokenValid(token, securityAppUser);
    }

    private void updateAndSaveTokens(AppUser appUser, String accessToken, String refreshToken, HttpServletResponse response) throws IOException {
        revokeAllAppUserTokens(appUser);
        saveJwtTokenToRepo(appUser, accessToken);
        sendAuthenticationResponse(accessToken, refreshToken, response);
    }

    private void sendAuthenticationResponse(String accessToken, String refreshToken, HttpServletResponse response) throws IOException {
        var authResponse = AuthenticationResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
    }
}