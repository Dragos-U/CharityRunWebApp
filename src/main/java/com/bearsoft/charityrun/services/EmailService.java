package com.bearsoft.charityrun.services;

import com.bearsoft.charityrun.models.domain.dtos.AppUserDTO;
import com.bearsoft.charityrun.models.domain.dtos.CourseRegistrationDTO;
import com.bearsoft.charityrun.models.domain.entities.AppUser;

public interface EmailService {

    void sendAppUserRegistrationEmail(AppUserDTO appUserDTO, String subject);
    void sendCourseRegistrationEmail(CourseRegistrationDTO courseRegistrationDTO, AppUser appUser, String subject);
    void sendCourseReminderEmail(AppUser appUser, String subject, long daysUntilCourse);

}
