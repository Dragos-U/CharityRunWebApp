package com.bearsoft.charityrun.services;

import com.bearsoft.charityrun.exceptions.course.CourseNotFoundException;
import com.bearsoft.charityrun.models.domain.dtos.CourseRegistrationDTO;
import com.bearsoft.charityrun.models.domain.entities.CourseRegistration;
import com.bearsoft.charityrun.models.security.SecurityAppUser;
import com.bearsoft.charityrun.repositories.AppUserRepository;
import com.bearsoft.charityrun.repositories.CourseRegistrationRepository;
import com.bearsoft.charityrun.repositories.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseRegistrationServiceImpl implements CourseRegistrationService {

    private final AppUserRepository appUserRepository;
    private final CourseRepository courseRepository;
    private final CourseRegistrationRepository courseRegistrationRepository;

    @Override
    @Transactional
    public CourseRegistrationDTO getLoggedAppUserRegistration(Principal connectedAppUser) {
        var securityAppUser = (SecurityAppUser) ((UsernamePasswordAuthenticationToken) connectedAppUser).getPrincipal();
        var appUser = securityAppUser.getAppUser();

        return CourseRegistrationDTO.builder()
                .courseType(appUser.getCourseRegistration().getCourse().getCourseType())
                .tshirtSize(appUser.getCourseRegistration().getTshirtSize())
                .build();
    }

    @Override
    @Transactional
    public CourseRegistrationDTO registerLoggedAppUserToCourse(CourseRegistrationDTO courseRegistrationDTO, Principal connectedAppUser) {
        var securityAppUser = (SecurityAppUser) ((UsernamePasswordAuthenticationToken) connectedAppUser).getPrincipal();
        var appUser = securityAppUser.getAppUser();

        if(appUser.getCourseRegistration() == null) {
            var tshirtSize = courseRegistrationDTO.getTshirtSize();
            var courseType = courseRegistrationDTO.getCourseType();
            var course = courseRepository.getCourseByCourseType(courseType)
                    .orElseThrow(() -> new CourseNotFoundException("Course type " + courseType + " not found."));

            CourseRegistration courseRegistration = CourseRegistration.builder()
                    .course(course)
                    .tshirtSize(tshirtSize)
                    .bib(123)
                    .appUser(appUser)
                    .build();
            appUser.setCourseRegistration(courseRegistration);

            appUserRepository.save(appUser);
            courseRegistrationRepository.save(courseRegistration);
            return courseRegistrationDTO;
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public void unregisterLoggedAppUserFromCourse(Principal connectedAppUser) {
        var securityAppUser = (SecurityAppUser) ((UsernamePasswordAuthenticationToken) connectedAppUser).getPrincipal();
        var appUser = securityAppUser.getAppUser();

        Long courseRegistrationId = appUser.getCourseRegistration().getId();
        appUser.setCourseRegistration(null);

        appUserRepository.save(appUser);
        courseRegistrationRepository.deleteById(courseRegistrationId);
    }
}
