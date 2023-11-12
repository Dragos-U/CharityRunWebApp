package com.bearsoft.charityrun.services;

import com.bearsoft.charityrun.models.dtos.AuthenticationRequestDTO;
import com.bearsoft.charityrun.models.dtos.AuthenticationResponseDTO;
import com.bearsoft.charityrun.models.SecurityAppUser;
import com.bearsoft.charityrun.models.dtos.AppUserDTO;
import com.bearsoft.charityrun.models.entities.AppUser;
import com.bearsoft.charityrun.models.entities.Role;
import com.bearsoft.charityrun.models.entities.Token;
import com.bearsoft.charityrun.models.enums.TokenType;
import com.bearsoft.charityrun.repositories.AppUserRepository;
import com.bearsoft.charityrun.repositories.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AppUserRepository appUserRepository;
    private final TokenRepository tokenRepository;
    private final JwtFilterService jwtFilterService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponseDTO registerAppUser(AppUserDTO appUserDTO) {
        var appUser = AppUser.builder()
                .firstName(appUserDTO.getFirstName())
                .lastName(appUserDTO.getLastName())
                .email(appUserDTO.getEmail())
                .password(passwordEncoder.encode(appUserDTO.getPassword()))
                .roles(new HashSet<>(List.of(Role.builder().name("USER").build())))
                .address(appUserDTO.getAddress())
                .build();
        appUserRepository.save(appUser);
        SecurityAppUser securityAppUser = new SecurityAppUser(appUser);

        var jwtToken = jwtFilterService.generateToken(securityAppUser);

        saveUserToken(appUser, jwtToken);

        return AuthenticationResponseDTO.builder()
                .accessToken(jwtToken)
                .build();
    }

    public AuthenticationResponseDTO authenticateAppUser(AuthenticationRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));
        var appUser = appUserRepository.findAppUsersByEmail(request.getEmail())
                .orElseThrow(); // handle this exception

        SecurityAppUser securityAppUser = new SecurityAppUser(appUser);
        var jwtToken = jwtFilterService.generateToken(securityAppUser);
        revokeAllAppUserTokens(appUser);
        saveUserToken(appUser, jwtToken);

        return AuthenticationResponseDTO.builder()
                .accessToken(jwtToken)
                .build();
    }

    private void revokeAllAppUserTokens(AppUser appUser){
        var validAppUserToken = tokenRepository.findAllValidTokensByUser(appUser.getId());
        if(validAppUserToken.isEmpty())
            return;
        validAppUserToken.forEach(t->{
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validAppUserToken);
    }

    private void saveUserToken(AppUser appUser, String jwtToken) {
        var token = Token.builder()
                .appUser(appUser)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }
}
