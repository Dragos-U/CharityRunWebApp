package com.bearsoft.charityrun.controllers;

import com.bearsoft.charityrun.models.dtos.AppUserDTO;
import com.bearsoft.charityrun.models.dtos.ChangePasswordDTO;
import com.bearsoft.charityrun.services.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('ROLE_USER')")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    @GetMapping
    public ResponseEntity<AppUserDTO> retrieveCurrentlyLoggedUserData(Principal connectedAppUser) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(appUserService.getConnectedAppUserData(connectedAppUser));
    }

    @PutMapping
    public ResponseEntity<AppUserDTO> updateCurrentlyLoggedUserData(
            @RequestBody AppUserDTO appUserDTO,
            Principal connectedAppUser) {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(appUserService.updateConnectedAppUserData(appUserDTO, connectedAppUser));
    }

    @PatchMapping
    public ResponseEntity<String> changeCurrentlyLoggedUserPassword(
            @RequestBody ChangePasswordDTO changePasswordDTO,
            Principal connectedAppUser) {
        appUserService.changeConnectedAppUserPassword(changePasswordDTO, connectedAppUser);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body("Your password was successfully changed.");
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<String> deleteCurrentlyLoggedUser(
            @PathVariable String email,
            Principal connectedAppUser) {
        boolean isUserDeleted = appUserService.deletedConnectedAppUser(email, connectedAppUser);
        return ResponseEntity.status(HttpStatus.OK).body("User with email: "+ email+ " deleted ? "+ isUserDeleted);
    }
}