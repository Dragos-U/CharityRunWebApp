package com.bearsoft.charityrun.services;

import com.bearsoft.charityrun.models.authentication.AuthenticationRequest;
import com.bearsoft.charityrun.models.authentication.AuthenticationResponse;
import com.bearsoft.charityrun.models.SecurityAppUser;
import com.bearsoft.charityrun.models.dtos.AppUserDTO;
import com.bearsoft.charityrun.models.entities.AppUser;
import com.bearsoft.charityrun.models.entities.Role;
import com.bearsoft.charityrun.repositories.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AppUserRepository appUserRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse registerAppUser(AppUserDTO appUserDTO) {
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

        var jwtToken = jwtService.generateToken(securityAppUser);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticateAppUser(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));
        var appUser = appUserRepository.findAppUsersByEmail(request.getEmail())
                .orElseThrow(); // handle this exception

        SecurityAppUser securityAppUser = new SecurityAppUser(appUser);
        var jwtToken = jwtService.generateToken(securityAppUser);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }
}
