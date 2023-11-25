package com.bearsoft.charityrun.services.impl;

import com.bearsoft.charityrun.exceptions.appuser.InvalidUserAuthenticationException;
import com.bearsoft.charityrun.exceptions.course.CourseNotFoundException;
import com.bearsoft.charityrun.exceptions.courseregistration.CourseRegistrationAlreadyExistsException;
import com.bearsoft.charityrun.exceptions.courseregistration.CourseRegistrationNotFoundException;
import com.bearsoft.charityrun.models.domain.dtos.CourseRegistrationDTO;
import com.bearsoft.charityrun.models.domain.entities.AppUser;
import com.bearsoft.charityrun.models.domain.entities.Course;
import com.bearsoft.charityrun.models.domain.entities.CourseRegistration;
import com.bearsoft.charityrun.models.domain.entities.Role;
import com.bearsoft.charityrun.models.domain.enums.CourseType;
import com.bearsoft.charityrun.models.domain.enums.RoleType;
import com.bearsoft.charityrun.models.security.SecurityAppUser;
import com.bearsoft.charityrun.repositories.AppUserRepository;
import com.bearsoft.charityrun.repositories.CourseRegistrationRepository;
import com.bearsoft.charityrun.repositories.CourseRepository;
import com.bearsoft.charityrun.repositories.RoleRepository;
import com.bearsoft.charityrun.services.BibNumberGeneratorService;
import com.bearsoft.charityrun.services.CourseRegistrationService;
import com.bearsoft.charityrun.services.EmailService;
import com.bearsoft.charityrun.validators.ObjectsValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseRegistrationServiceImpl implements CourseRegistrationService {

    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final AppUserRepository appUserRepository;
    private final CourseRepository courseRepository;
    private final BibNumberGeneratorService bibNumberGeneratorService;
    private final CourseRegistrationRepository courseRegistrationRepository;
    private final ObjectsValidator<CourseRegistrationDTO> courseRegistrationDTOObjectsValidator;

    @Override
    @Transactional
    public CourseRegistrationDTO getLoggedAppUserRegistration(Principal connectedAppUser) {
        SecurityAppUser securityAppUser = extractUserFromPrincipal(connectedAppUser);
        var appUser = securityAppUser.getAppUser();
        validateCourseRegistrationExistence(appUser);

        return CourseRegistrationDTO.builder()
                .courseType(appUser.getCourseRegistration().getCourse().getCourseType())
                .tShirtSize(appUser.getCourseRegistration().getTShirtSize())
                .genre(appUser.getCourseRegistration().getGenre())
                .age(appUser.getCourseRegistration().getAge())
                .build();
    }

    @Transactional
    public CourseRegistrationDTO registerLoggedAppUserToCourse(CourseRegistrationDTO courseRegistrationDTO, Principal connectedAppUser) {
        courseRegistrationDTOObjectsValidator.validate(courseRegistrationDTO);

        SecurityAppUser securityAppUser = extractUserFromPrincipal(connectedAppUser);
        var appUser = securityAppUser.getAppUser();
        validateCourseRegistrationNotExistence(appUser);

        var courseType = courseRegistrationDTO.getCourseType();
        CourseRegistration courseRegistration = CourseRegistration.builder()
                .course(getCourseByType(courseType))
                .tShirtSize(courseRegistrationDTO.getTShirtSize())
                .genre(courseRegistrationDTO.getGenre())
                .bib(generateBib(courseType))
                .age(courseRegistrationDTO.getAge())
                .appUser(appUser)
                .build();
        appUser.setCourseRegistration(courseRegistration);
        addRoleToUser(appUser);
        courseRegistrationRepository.save(courseRegistration);

        emailService.sendCourseRegistrationEmail(courseRegistrationDTO, appUser, "Course Registration Confirmation.");

        return courseRegistrationDTO;
    }

    @Override
    @Transactional
    public void unregisterLoggedAppUserFromCourse(Principal connectedAppUser) {
        SecurityAppUser securityAppUser = extractUserFromPrincipal(connectedAppUser);
        var appUser = securityAppUser.getAppUser();

        validateCourseRegistrationExistence(appUser);
        Long courseRegistrationId = appUser.getCourseRegistration().getId();
        appUser.setCourseRegistration(null);
        removeRoleFromUser(appUser);

        courseRegistrationRepository.deleteById(courseRegistrationId);
    }

    private SecurityAppUser extractUserFromPrincipal(Principal connectedAppUser) {
        checkConnectedUserAuthentication(connectedAppUser);
        return (SecurityAppUser) ((UsernamePasswordAuthenticationToken) connectedAppUser).getPrincipal();
    }

    private void checkConnectedUserAuthentication(Principal connectedAppUser) {
        if (!(connectedAppUser instanceof UsernamePasswordAuthenticationToken)) {
            throw new InvalidUserAuthenticationException("Invalid user authentication");
        }
    }

    private void validateCourseRegistrationNotExistence(AppUser appUser) {
        if (appUser.getCourseRegistration() != null) {
            log.error("Course registration already found for user: {}", appUser.getEmail());
            throw new CourseRegistrationAlreadyExistsException(
                    String.format("Course registration already exists for user : %s", appUser.getEmail()));
        }
    }

    private void validateCourseRegistrationExistence(AppUser appUser) {
        if (appUser.getCourseRegistration() == null) {
            log.error("Course registration not found for user: {}", appUser.getEmail());
            throw new CourseRegistrationNotFoundException(
                    String.format("Course registration not found for user : %s", appUser.getEmail()));
        }
    }

    private Course getCourseByType(CourseType courseType) {
        return courseRepository.getCourseByCourseType(courseType)
                .orElseThrow(() -> new CourseNotFoundException(String.format("Course type %s not found.", courseType)));
    }

    private int generateBib(CourseType courseType) {
        Set<Integer> existingBibs = courseRegistrationRepository.findAllBibNumbers();
        int bibNumber = bibNumberGeneratorService.generateBibNumber(courseType);
        while (existingBibs.contains(bibNumber)) {
            bibNumber = bibNumberGeneratorService.generateBibNumber(courseType);
        }
        return bibNumber;
    }

    private void addRoleToUser(AppUser appUser) {
        Role roleToAdd = Role.builder()
                .roleType(RoleType.ROLE_PARTICIPANT)
                .build();
        appUser.getRoles().add(roleToAdd);
        appUserRepository.save(appUser);
    }

    private void removeRoleFromUser(AppUser appUser) {
        Role existingRole = appUser.getRoles().stream()
                .filter(role -> role.getRoleType() == RoleType.ROLE_PARTICIPANT)
                .findFirst()
                .orElse(null);

        if (existingRole != null) {
            appUser.getRoles().remove(existingRole);
            appUserRepository.save(appUser);
            roleRepository.delete(existingRole);
        }
    }
}
