package com.bearsoft.charityrun.controllers;

import com.bearsoft.charityrun.models.dtos.AuthenticationRequestDTO;
import com.bearsoft.charityrun.models.dtos.AuthenticationResponseDTO;
import com.bearsoft.charityrun.models.dtos.AppUserDTO;
import com.bearsoft.charityrun.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    public ResponseEntity<AuthenticationResponseDTO> register(@RequestBody AppUserDTO appUserDTO){
        return ResponseEntity.ok(authenticationService.registerAppUser(appUserDTO));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> register(@RequestBody AuthenticationRequestDTO authRequestDTO){
        return ResponseEntity.ok(authenticationService.authenticateAppUser(authRequestDTO));
    }

    @GetMapping("/test-authentication")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello from secured endpoint");
    }
}
