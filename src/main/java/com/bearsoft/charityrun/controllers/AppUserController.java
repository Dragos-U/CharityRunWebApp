package com.bearsoft.charityrun.controllers;

import com.bearsoft.charityrun.aspects.RateLimited;
import com.bearsoft.charityrun.models.domain.dtos.AppUserDTO;
import com.bearsoft.charityrun.models.domain.dtos.ChangePasswordDTO;
import com.bearsoft.charityrun.models.domain.dtos.RegistrationResponseDTO;
import com.bearsoft.charityrun.models.domain.enums.CourseType;
import com.bearsoft.charityrun.models.domain.enums.GenderType;
import com.bearsoft.charityrun.services.models.interfaces.AppUserService;
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

    @RateLimited
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<AppUserDTO> getLoggedAppUserData(Principal connectedAppUser) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(appUserService.getConnectedAppUserData(connectedAppUser));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AppUserDTO>> getAllAppUsers() {
        return ResponseEntity.status(HttpStatus.OK).body(appUserService.getAllAppUsers());
    }

    @GetMapping("/registered/{eventId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<RegistrationResponseDTO>> getRegisteredUsers(
            @PathVariable("eventId") Long eventId,
            @RequestParam("courseType") CourseType courseType,
            @RequestParam("gender") GenderType gender,
            @RequestParam(value = "minAge", required = false) Integer minAge,
            @RequestParam(value = "maxAge", required = false) Integer maxAge) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(appUserService.getRegisteredUsers(courseType, eventId, gender, minAge, maxAge));
    }

    @GetMapping("/sorted/{eventId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<RegistrationResponseDTO>> getSortedRegisteredUsers(
            @PathVariable Long eventId,
            @RequestParam String sortBy,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(appUserService.getSortedRegisteredUsers(eventId, sortBy, order, page, size).getContent());
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
            @RequestHeader(value = "Accept-language", required = false) Locale locale,
            @RequestBody ChangePasswordDTO changePasswordDTO,
            Principal connectedAppUser) {
        appUserService.changeConnectedAppUserPassword(changePasswordDTO, connectedAppUser);
        String successMessage = messageSource.getMessage(
                "password.change.success", null, LocaleContextHolder.getLocale());
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(successMessage);
    }

    @GetMapping("/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AppUserDTO> getUserByEmail(
            @PathVariable String email) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(appUserService.getAppUserByUsername(email));
    }

    @DeleteMapping("/me/{email}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> deletedLoggedAppUser(
            @PathVariable String email,
            Principal connectedAppUser) {
        appUserService.deletedConnectedAppUser(email, connectedAppUser);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @DeleteMapping("/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUserByEmail(
            @PathVariable String email) {
        appUserService.deleteAppUserByEmail(email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}