package com.bearsoft.charityrun.services.impl;

import com.bearsoft.charityrun.exceptions.appuser.UserNotFoundException;
import com.bearsoft.charityrun.exceptions.course.CourseNotFoundException;
import com.bearsoft.charityrun.exceptions.courseregistration.CourseRegistrationAlreadyExistsException;
import com.bearsoft.charityrun.exceptions.courseregistration.CourseRegistrationNotFoundException;
import com.bearsoft.charityrun.models.domain.dtos.CourseRegistrationDTO;
import com.bearsoft.charityrun.models.domain.entities.CourseRegistration;
import com.bearsoft.charityrun.models.security.SecurityAppUser;
import com.bearsoft.charityrun.repositories.AppUserRepository;
import com.bearsoft.charityrun.repositories.CourseRegistrationRepository;
import com.bearsoft.charityrun.repositories.CourseRepository;
import com.bearsoft.charityrun.services.CourseRegistrationService;
import com.bearsoft.charityrun.validator.ObjectsValidator;
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
    private final ObjectsValidator<CourseRegistrationDTO> courseRegistrationDTOObjectsValidator;

    @Override
    @Transactional
    public CourseRegistrationDTO getLoggedAppUserRegistration(Principal connectedAppUser) {
        var securityAppUser = (SecurityAppUser) ((UsernamePasswordAuthenticationToken) connectedAppUser).getPrincipal();
        var appUser = securityAppUser.getAppUser();

        try{
            if(appUser.getCourseRegistration() == null)
            {
                throw new CourseRegistrationNotFoundException(
                        String.format("Course registration for user : %s , not found",appUser.getEmail()));
            }

            return CourseRegistrationDTO.builder()
                    .courseType(appUser.getCourseRegistration().getCourse().getCourseType())
                    .tShirtSize(appUser.getCourseRegistration().getTShirtSize())
                    .build();
        } catch(CourseRegistrationNotFoundException courseRegistrationNotFoundException){
            log.error("Course registration for user : %s , not found",appUser.getEmail());
            throw new CourseRegistrationNotFoundException(
                    String.format("Course registration for user : %s , not found",appUser.getEmail()));
        }
    }

    @Override
    @Transactional
    public CourseRegistrationDTO registerLoggedAppUserToCourse(CourseRegistrationDTO courseRegistrationDTO, Principal connectedAppUser) {
        courseRegistrationDTOObjectsValidator.validate(courseRegistrationDTO);

        var securityAppUser = (SecurityAppUser) ((UsernamePasswordAuthenticationToken) connectedAppUser).getPrincipal();
        var appUser = securityAppUser.getAppUser();

        try{
            if(appUser.getCourseRegistration() != null)
            {
                throw new CourseRegistrationAlreadyExistsException(
                        String.format("Course registration already exists for user : %s",appUser.getEmail()));
            }
            var tshirtSize = courseRegistrationDTO.getTShirtSize();
            var courseType = courseRegistrationDTO.getCourseType();
            var course = courseRepository.getCourseByCourseType(courseType)
                    .orElseThrow(() -> new CourseNotFoundException(String.format("Course type %s not found.", courseType)));

            CourseRegistration courseRegistration = CourseRegistration.builder()
                    .course(course)
                    .tShirtSize(tshirtSize)
                    .bib(123)
                    .appUser(appUser)
                    .build();
            appUser.setCourseRegistration(courseRegistration);

            appUserRepository.save(appUser);
            courseRegistrationRepository.save(courseRegistration);
            return courseRegistrationDTO;
        } catch (CourseRegistrationAlreadyExistsException e){
            throw new CourseRegistrationAlreadyExistsException(
                    String.format("Course registration already exists for user : %s",appUser.getEmail()));
        }
    }

    @Override
    @Transactional
    public void unregisterLoggedAppUserFromCourse(Principal connectedAppUser) {
        var securityAppUser = (SecurityAppUser) ((UsernamePasswordAuthenticationToken) connectedAppUser).getPrincipal();
        var appUser = securityAppUser.getAppUser();

        try{
            if(appUser.getCourseRegistration() == null)
            {
                throw new CourseRegistrationNotFoundException(
                        String.format("Course registration for user : %s , not found",appUser.getEmail()));
            }
            Long courseRegistrationId = appUser.getCourseRegistration().getId();
            appUser.setCourseRegistration(null);

            appUserRepository.save(appUser);
            courseRegistrationRepository.deleteById(courseRegistrationId);
        } catch(CourseRegistrationNotFoundException courseRegistrationNotFoundException){
            throw new CourseRegistrationNotFoundException(
                    String.format("Course registration for user : %s , not found",appUser.getEmail() ));
        }

    }
}
