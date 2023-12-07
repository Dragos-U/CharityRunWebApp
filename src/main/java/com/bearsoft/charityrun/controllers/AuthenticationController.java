package com.bearsoft.charityrun.controllers;

import com.bearsoft.charityrun.models.domain.dtos.AuthenticationRequestDTO;
import com.bearsoft.charityrun.models.domain.dtos.AuthenticationResponseDTO;
import com.bearsoft.charityrun.models.domain.dtos.AppUserDTO;
import com.bearsoft.charityrun.services.security.interfaces.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/authentication")
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(
            description = "Post endpoint for registration",
            summary = "A client can register as an App User",
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201"
                    ),
                    @ApiResponse(
                            description = "Bad request",
                            responseCode = "401"
                    )
            }
    )
    @PostMapping("/registration")
    public ResponseEntity<AuthenticationResponseDTO> registerAppUser(
            @RequestBody AppUserDTO appUserDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.registerAppUser(appUserDTO));
    }

    @Operation(
            description = "Post endpoint for login",
            summary = "AppUser can login using valid credentials ",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Bad request",
                            responseCode = "401"
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> loginAppUser(
            @RequestBody AuthenticationRequestDTO authRequestDTO) {
        return ResponseEntity.ok(authenticationService.loginAppUser(authRequestDTO));
    }

    @PostMapping("/refresh-token")
    public void refreshLoggedUserToken(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        authenticationService.refreshLoggedUserToken(request, response);
    }
}
