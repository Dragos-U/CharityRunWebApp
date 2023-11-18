package com.bearsoft.charityrun.services;

import com.bearsoft.charityrun.models.domain.dtos.CourseRegistrationDTO;

import java.security.Principal;

public interface CourseRegistrationService {

    CourseRegistrationDTO registerLoggedAppUserToCourse(CourseRegistrationDTO courseRegistrationDTO, Principal connectedAppUser);

    void unregisterLoggedAppUserFromCourse(Principal connectedAppUser);

    CourseRegistrationDTO getLoggedAppUserRegistration(Principal connectedAppUser);

}
