package com.bearsoft.charityrun.controllers;

import com.bearsoft.charityrun.models.dtos.ChangePasswordDTO;
import com.bearsoft.charityrun.services.AppUserServiceImpl;
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

    private final AppUserServiceImpl appUserServiceImpl;

    @PatchMapping
    public ResponseEntity<String> changePassword(
            @RequestBody ChangePasswordDTO changePasswordDTO,
            Principal connectedAppUser){
        appUserServiceImpl.changePassword(changePasswordDTO, connectedAppUser);
        return ResponseEntity.status(202).body("Your password was changed.");
    }
    @GetMapping
    public String retrieveAllUsers(){
        return "Users";
    }

    @GetMapping("/users/{userID}")
    public String getUserDetails(@PathVariable Long userID){
        return "User details";
    }

    @DeleteMapping("/users/{userID}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public String deleteUser(@PathVariable Long userID){
        return "Delete User";
    }

}

