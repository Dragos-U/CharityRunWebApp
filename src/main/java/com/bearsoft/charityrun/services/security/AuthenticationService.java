package com.bearsoft.charityrun.services.security;

import com.bearsoft.charityrun.exceptions.appuser.AppUserAlreadyExistsException;
import com.bearsoft.charityrun.exceptions.appuser.PasswordDoesNotMatchException;
import com.bearsoft.charityrun.exceptions.appuser.AppUserNotFoundException;
import com.bearsoft.charityrun.exceptions.appuser.TokenNotFoundException;
import com.bearsoft.charityrun.exceptions.email.EmailSendingException;
import com.bearsoft.charityrun.models.domain.dtos.AuthenticationRequestDTO;
import com.bearsoft.charityrun.models.domain.dtos.AuthenticationResponseDTO;
import com.bearsoft.charityrun.models.security.SecurityAppUser;
import com.bearsoft.charityrun.models.domain.dtos.AppUserDTO;
import com.bearsoft.charityrun.models.domain.entities.AppUser;
import com.bearsoft.charityrun.models.domain.entities.Role;
import com.bearsoft.charityrun.models.domain.enums.RoleType;
import com.bearsoft.charityrun.models.validation.OnCreate;
import com.bearsoft.charityrun.repositories.AppUserRepository;
import com.bearsoft.charityrun.services.notifications.interfaces.EmailService;
import com.bearsoft.charityrun.validators.ObjectsValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService implements com.bearsoft.charityrun.services.security.interfaces.AuthenticationService {

    public static final String USERNAME_NOT_FOUND = "Username not found.";
    public static final String ERROR_DURING_EMAIL_SENDING = "Error during email sending.";
    private static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHENTICATION_FAILED = "Authentication failed.";
    public static final String WRONG_CREDENTIALS = " Wrong credentials.";

    private final AppUserRepository appUserRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtFilterService jwtFilterService;
    private final PasswordEncoder passwordEncoder;

    private final ObjectsValidator<AppUserDTO> appUserDTOValidator;
    private final ObjectsValidator<AuthenticationRequestDTO> authenticationRequestDTOObjectsValidator;


    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    @Transactional
    @Override
    public AuthenticationResponseDTO registerAppUser(AppUserDTO appUserDTO) {
        appUserDTOValidator.validate(appUserDTO, OnCreate.class);

        Optional<AppUser> existingUser = appUserRepository.findAppUsersByEmail(appUserDTO.getEmail());
        if (existingUser.isPresent()) {
            throw new AppUserAlreadyExistsException("Email address is already used.");
        }

        var appUser = AppUser.builder()
                .firstName(appUserDTO.getFirstName())
                .lastName(appUserDTO.getLastName())
                .email(appUserDTO.getEmail())
                .password(passwordEncoder.encode(appUserDTO.getPassword()))
                .roles(new HashSet<>(List.of(Role.builder().roleType(RoleType.ROLE_USER).build())))
                .address(appUserDTO.getAddress())
                .build();
        appUserRepository.save(appUser);

        var securityAppUser = new SecurityAppUser(appUser);

        try {
            String subject = "Welcome to our event.";
            emailService.sendAppUserRegistrationEmail(appUserDTO, subject);
        } catch (EmailSendingException e) {
            log.error(ERROR_DURING_EMAIL_SENDING);
            throw new EmailSendingException(ERROR_DURING_EMAIL_SENDING);
        }
        return generateTokens(securityAppUser);
    }

    @Override
    public AuthenticationResponseDTO loginAppUser(AuthenticationRequestDTO authenticationRequestDTO) {
        authenticationRequestDTOObjectsValidator.validate(authenticationRequestDTO);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequestDTO.getEmail(),
                            authenticationRequestDTO.getPassword()));
        } catch (UsernameNotFoundException usernameNotFoundException) {
            log.error(AUTHENTICATION_FAILED + USERNAME_NOT_FOUND + " {}", usernameNotFoundException.getMessage());
            throw new AppUserNotFoundException(USERNAME_NOT_FOUND);
        } catch (BadCredentialsException badCredentialsException) {
            log.error(AUTHENTICATION_FAILED + WRONG_CREDENTIALS + " {}", badCredentialsException.getMessage());
            throw new PasswordDoesNotMatchException(WRONG_CREDENTIALS);
        }

        var appUser = appUserRepository.findAppUsersByEmail(authenticationRequestDTO.getEmail())
                .orElseThrow(() -> new AppUserNotFoundException(USERNAME_NOT_FOUND));
        var securityAppUser = new SecurityAppUser(appUser);

        return generateTokens(securityAppUser);
    }

    @Override
    public void refreshLoggedUserToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        String refreshToken = extractTokenFromRequest(request, BEARER_PREFIX);
        if (refreshToken == null) {
            throw new TokenNotFoundException("The request has no token in the header.");
        }

        String userEmail = jwtFilterService.extractUsername(refreshToken);
        Optional<AppUser> appUserOptional = appUserRepository.findAppUsersByEmail(userEmail);

        if (appUserOptional.isPresent() && isTokenValidForUser(refreshToken, appUserOptional.get())) {
            var securityAppUser = new SecurityAppUser(appUserOptional.get());
            String accessToken = jwtFilterService.generateToken(securityAppUser);
            sendAuthenticationResponse(accessToken, refreshToken, response);
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
        var securityAppUser = new SecurityAppUser(appUser);
        return jwtFilterService.isTokenValid(token, securityAppUser);
    }

    private void sendAuthenticationResponse(String accessToken, String refreshToken, HttpServletResponse response) throws IOException {
        var authResponse = AuthenticationResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
    }

    private AuthenticationResponseDTO generateTokens(SecurityAppUser securityAppUser) {
        String jwtToken = jwtFilterService.generateToken(securityAppUser);
        log.info("New token was generated");
        String refreshToken = jwtFilterService.generateRefreshToken(securityAppUser);
        log.info("New refresh was generated");

        return AuthenticationResponseDTO.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }
}