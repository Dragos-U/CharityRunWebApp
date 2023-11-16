package com.bearsoft.charityrun.services;

import com.bearsoft.charityrun.models.SecurityAppUser;
import com.bearsoft.charityrun.models.dtos.ChangePasswordDTO;

import java.security.Principal;

public interface AppUserService {

    SecurityAppUser loadUserByUsername(String email);
    void changePassword(ChangePasswordDTO changePasswordDTO, Principal connectedAppUser);
}
