package com.bearsoft.charityrun.controllers;

import com.bearsoft.charityrun.models.dtos.AppUserDTO;
import com.bearsoft.charityrun.models.dtos.ChangePasswordDTO;
import com.bearsoft.charityrun.services.AppUserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('ROLE_USER')")
@RequiredArgsConstructor
public class AppUserController {

    private final AppUserServiceImpl appUserServiceImpl;

    @GetMapping("/me")
    public ResponseEntity<AppUserDTO> retrieveUserData(){

        return null;
    }

    @PutMapping("/me")
    public ResponseEntity<AppUserDTO> updateUserData(){

        return null;
    }

    @PatchMapping("/me")
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordDTO changePasswordDTO,
            Principal connectedAppUser){
        appUserServiceImpl.changePassword(changePasswordDTO, connectedAppUser);
        return ResponseEntity.status(202).body("Your password was succesfully changed.");
    }

    @DeleteMapping("/me")
    public ResponseEntity<String> deleteUser(){

        return null;
    }



}
