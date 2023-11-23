package com.bearsoft.charityrun.services;

import com.bearsoft.charityrun.models.security.SecurityAppUser;
import com.bearsoft.charityrun.models.domain.dtos.AppUserDTO;
import com.bearsoft.charityrun.models.domain.dtos.ChangePasswordDTO;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface AppUserService {

    SecurityAppUser loadUserByUsername(String email);
//    AppUserDTO getAppUserByUsername(String email);
    void changeConnectedAppUserPassword(ChangePasswordDTO changePasswordDTO, Principal connectedAppUser);
    AppUserDTO getConnectedAppUserData(Principal connectedAppUser);
    public AppUserDTO updateConnectedAppUserData(AppUserDTO appUserDTO, Principal connectedAppUser);
    ResponseEntity<Object> deletedConnectedAppUser(String email, Principal connectedAppUser);
    List<AppUserDTO> getAllAppUsers();
}
