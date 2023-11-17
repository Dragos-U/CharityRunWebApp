package com.bearsoft.charityrun.services;

import com.bearsoft.charityrun.models.SecurityAppUser;
import com.bearsoft.charityrun.models.dtos.AppUserDTO;
import com.bearsoft.charityrun.models.dtos.ChangePasswordDTO;

import java.security.Principal;

public interface AppUserService {

    SecurityAppUser loadUserByUsername(String email);
    void changeConnectedAppUserPassword(ChangePasswordDTO changePasswordDTO, Principal connectedAppUser);
    AppUserDTO getConnectedAppUserData(Principal connectedAppUser);
    public AppUserDTO updateConnectedAppUserData(AppUserDTO appUserDTO, Principal connectedAppUser);
    boolean deletedConnectedAppUser(String email, Principal connectedAppUser);
}
