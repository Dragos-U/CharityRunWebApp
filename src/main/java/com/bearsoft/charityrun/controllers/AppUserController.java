package com.bearsoft.charityrun.controllers;

import com.bearsoft.charityrun.models.dtos.ChangePasswordDTO;
import com.bearsoft.charityrun.services.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('ROLE_USER')")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    @PatchMapping
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordDTO changePasswordDTO,
            Principal connectedAppUser){
        appUserService.changePassword(changePasswordDTO, connectedAppUser);
        return ResponseEntity.status(202).body("Your password was changed.");
    }
}
