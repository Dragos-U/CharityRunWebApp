package com.bearsoft.charityrun.services.security.interfaces;

import com.bearsoft.charityrun.models.domain.dtos.AppUserDTO;
import com.bearsoft.charityrun.models.domain.dtos.AuthenticationRequestDTO;
import com.bearsoft.charityrun.models.domain.dtos.AuthenticationResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {

    AuthenticationResponseDTO registerAppUser(AppUserDTO appUserDTO);

    AuthenticationResponseDTO loginAppUser(AuthenticationRequestDTO authenticationRequestDTO);

    void refreshLoggedUserToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

}