package com.bearsoft.charityrun.services.models.interfaces;

import com.bearsoft.charityrun.models.domain.dtos.RegistrationResponseDTO;
import com.bearsoft.charityrun.models.domain.enums.CourseType;
import com.bearsoft.charityrun.models.domain.enums.GenderType;
import com.bearsoft.charityrun.models.security.SecurityAppUser;
import com.bearsoft.charityrun.models.domain.dtos.AppUserDTO;
import com.bearsoft.charityrun.models.domain.dtos.ChangePasswordDTO;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface AppUserService {

    SecurityAppUser loadUserByUsername(String email);
    AppUserDTO getAppUserByUsername(String email);
    void changeConnectedAppUserPassword(ChangePasswordDTO changePasswordDTO, Principal connectedAppUser);
    AppUserDTO getConnectedAppUserData(Principal connectedAppUser);

    Page<RegistrationResponseDTO> getSortedRegisteredUsers(Long eventId, String sortBy, String order, int page, int size);
    public AppUserDTO updateConnectedAppUserData(AppUserDTO appUserDTO, Principal connectedAppUser);
    void deleteAppUserByEmail(String email);
    void deletedConnectedAppUser(String email, Principal connectedAppUser);
    List<AppUserDTO> getAllAppUsers();
    List<RegistrationResponseDTO> getRegisteredUsers(CourseType courseType, Long eventId, GenderType gender, Integer minAge, Integer maxAge);
}
