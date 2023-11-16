package com.bearsoft.charityrun.controllers;

import com.bearsoft.charityrun.models.dtos.AuthenticationRequestDTO;
import com.bearsoft.charityrun.models.dtos.AuthenticationResponseDTO;
import com.bearsoft.charityrun.models.dtos.AppUserDTO;
import com.bearsoft.charityrun.services.security.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    public ResponseEntity<AuthenticationResponseDTO> register(@RequestBody AppUserDTO appUserDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.registerAppUser(appUserDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> register(@RequestBody AuthenticationRequestDTO authRequestDTO){
        return ResponseEntity.ok(authenticationService.authenticateAppUser(authRequestDTO));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        authenticationService.refreshToken(request, response);
    }
}
