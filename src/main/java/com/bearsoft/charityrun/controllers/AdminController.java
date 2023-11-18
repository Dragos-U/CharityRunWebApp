package com.bearsoft.charityrun.controllers;

import com.bearsoft.charityrun.models.domain.dtos.ChangePasswordDTO;
import com.bearsoft.charityrun.services.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AppUserService appUserService;

    @PatchMapping
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordDTO changePasswordDTO,
            Principal connectedAppUser){
        appUserService.changeConnectedAppUserPassword(changePasswordDTO, connectedAppUser);
        return ResponseEntity.status(202).body("Your password was changed.");
    }

}

