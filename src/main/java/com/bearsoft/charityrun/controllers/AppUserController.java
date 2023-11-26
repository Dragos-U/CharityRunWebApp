package com.bearsoft.charityrun.controllers;

import com.bearsoft.charityrun.models.domain.dtos.AppUserDTO;
import com.bearsoft.charityrun.models.domain.dtos.ChangePasswordDTO;
import com.bearsoft.charityrun.services.AppUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class AppUserController {

    private final AppUserService appUserService;
    private final MessageSource messageSource;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<AppUserDTO> getLoggedAppUserData(Principal connectedAppUser) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(appUserService.getConnectedAppUserData(connectedAppUser));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AppUserDTO>> getAllAppUsers(){
        return ResponseEntity.status(HttpStatus.OK).body(appUserService.getAllAppUsers());
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<AppUserDTO> updateLoggedAppUserData(
            @RequestBody AppUserDTO appUserDTO,
            Principal connectedAppUser) {
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(appUserService.updateConnectedAppUserData(appUserDTO, connectedAppUser));
    }

    @PatchMapping("/me")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<String> changeLoggedAppUserPassword(
            @RequestHeader(value="Accept-language", required = false) Locale locale,
            @RequestBody ChangePasswordDTO changePasswordDTO,
            Principal connectedAppUser) {
        appUserService.changeConnectedAppUserPassword(changePasswordDTO, connectedAppUser);
        String successMessage = messageSource.getMessage(
                "password.change.success", null, LocaleContextHolder.getLocale());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(successMessage);
    }

    @DeleteMapping("/me/{email}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> deletedLoggedAppUser(
            @PathVariable String email,
            Principal connectedAppUser) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(appUserService.deletedConnectedAppUser(email, connectedAppUser));
    }

    @GetMapping("/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AppUserDTO> getUserByEmail(
            @PathVariable String email){
        return ResponseEntity.status(HttpStatus.OK)
                .body(appUserService.getAppUserByUsername(email));
    }

    @DeleteMapping("/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteUserByEmail(
            @PathVariable String email){
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(appUserService.deleteAppUserByEmail(email));
    }
}