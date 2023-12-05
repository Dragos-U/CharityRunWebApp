package com.bearsoft.charityrun.services.models;

import com.bearsoft.charityrun.exceptions.appuser.*;
import com.bearsoft.charityrun.models.domain.dtos.RegistrationResponseDTO;
import com.bearsoft.charityrun.models.domain.entities.AppUser;
import com.bearsoft.charityrun.models.domain.enums.CourseType;
import com.bearsoft.charityrun.models.domain.enums.GenderType;
import com.bearsoft.charityrun.models.security.SecurityAppUser;
import com.bearsoft.charityrun.models.domain.dtos.AppUserDTO;
import com.bearsoft.charityrun.models.domain.dtos.ChangePasswordDTO;
import com.bearsoft.charityrun.models.validation.OnUpdate;
import com.bearsoft.charityrun.repositories.AppUserRepository;
import com.bearsoft.charityrun.services.models.interfaces.AppUserService;
import com.bearsoft.charityrun.validators.ObjectsValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService, UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final ObjectsValidator<ChangePasswordDTO> changePasswordDTOObjectsValidator;
    private final ObjectsValidator<AppUserDTO> appUserDTOObjectsValidator;

    @Override
    @Transactional
    public SecurityAppUser loadUserByUsername(String email) throws UsernameNotFoundException {
        var appUser = appUserRepository.findAppUsersByEmail(email);

        return appUser.map(SecurityAppUser::new)
                .orElseThrow(() -> new AppUserNotFoundException(String.format("Username %s not found.",email)));
    }

    @Override
    @Transactional
    public AppUserDTO getAppUserByUsername(String email){
        AppUser appUser = appUserRepository.findAppUsersByEmail(email)
                .orElseThrow(() -> new AppUserNotFoundException(String.format("User with email: %s not found", email)));

        return AppUserDTO.builder()
                .firstName(appUser.getFirstName())
                .lastName(appUser.getLastName())
                .email(appUser.getEmail())
                .address(appUser.getAddress())
                .courseRegistration(appUser.getCourseRegistration())
                .build();
    }

    @Override
    @Transactional
    public AppUserDTO getConnectedAppUserData(Principal connectedAppUser) {
        checkConnectedUserAuthentication(connectedAppUser);
        var securityAppUser = (SecurityAppUser) ((UsernamePasswordAuthenticationToken) connectedAppUser).getPrincipal();
        var appUser = securityAppUser.getAppUser();
        return AppUserDTO.builder()
                .firstName(appUser.getFirstName())
                .lastName(appUser.getLastName())
                .email(appUser.getEmail())
                .address(appUser.getAddress())
                .courseRegistration(appUser.getCourseRegistration())
                .build();
    }

    @Override
    public List<AppUserDTO> getAllAppUsers() {
        List<AppUser> appUsers = appUserRepository.findAllUsers()
                .orElseThrow(() -> new AppUserNotFoundException("Users not found."));
        return appUsers
                .stream()
                .map(appUser -> objectMapper.convertValue(appUser, AppUserDTO.class))
                .toList();
    }

    @Override
    public List<RegistrationResponseDTO> getRegisteredUsers(CourseType courseType, Long eventId, GenderType gender, Integer minAge, Integer maxAge) {
        List<AppUser> appUsers = appUserRepository.findUsersByCourseTypeEventIdAndGender(courseType, eventId, gender, minAge, maxAge);
        return appUsers
                .stream()
                .map(this::convertAppUserToRegistrationResponseDTO)
                .toList();
    }

    @Override
    public Page<RegistrationResponseDTO> getSortedRegisteredUsers(Long eventId, String sortBy, String order, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AppUser> usersPage = appUserRepository.findAppUsersByEventId(eventId, pageable);

        Comparator<AppUser> comparator = getComparator(sortBy, order);

        List<RegistrationResponseDTO> sortedUsers = usersPage.getContent().stream()
                .sorted(comparator)
                .map(this::convertAppUserToRegistrationResponseDTO)
                .toList();

        return new PageImpl<>(sortedUsers, pageable, usersPage.getTotalElements());
    }

    @Override
    @Transactional
    public AppUserDTO updateConnectedAppUserData(AppUserDTO appUserDTO, Principal connectedAppUser) {
        checkConnectedUserAuthentication(connectedAppUser);
        appUserDTOObjectsValidator.validate(appUserDTO, OnUpdate.class);

        log.info("update user data...");
        var securityAppUser = (SecurityAppUser) ((UsernamePasswordAuthenticationToken) connectedAppUser).getPrincipal();
        var appUser = securityAppUser.getAppUser();

        if (appUserDTO.getFirstName() != null) {
            appUser.setFirstName(appUserDTO.getFirstName());
        }

        if (appUserDTO.getLastName() != null) {
            appUser.setLastName(appUserDTO.getLastName());
        }

        if (appUserDTO.getEmail() != null) {
            appUser.setEmail(appUserDTO.getEmail());
        }

        if (appUserDTO.getAddress() != null) {
            appUser.setAddress(appUserDTO.getAddress());
        }

        appUserRepository.save(appUser);
        log.info("User saved to database.");
        appUser = appUserRepository.findAppUsersByEmail(appUserDTO.getEmail())
                .orElseThrow(() -> new AppUserNotFoundException(String.format("User with email: %s not found", appUserDTO.getEmail())));

        return AppUserDTO.builder()
                .firstName(appUser.getFirstName())
                .lastName(appUser.getLastName())
                .email(appUser.getEmail())
                .address(appUser.getAddress())
                .courseRegistration(appUser.getCourseRegistration())
                .build();
    }

    @Override
    @Transactional
    public void changeConnectedAppUserPassword(ChangePasswordDTO changePasswordDTO, Principal connectedAppUser) {
        checkConnectedUserAuthentication(connectedAppUser);
        changePasswordDTOObjectsValidator.validate(changePasswordDTO);

        var securityAppUser = (SecurityAppUser) ((UsernamePasswordAuthenticationToken) connectedAppUser).getPrincipal();
        var appUser = securityAppUser.getAppUser();

        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), appUser.getPassword())) {
            throw new PasswordDoesNotMatchException("Wrong current password.");
        }

        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmationPassword())) {
            throw new PasswordDoesNotMatchException("New Password does not match Confirmation Password.");
        }

        appUser.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        appUserRepository.save(appUser);
        log.info("New user password successfully saved to database.");
    }

    @Override
    @Transactional
    public void deletedConnectedAppUser(String email, Principal connectedAppUser) {
        checkConnectedUserAuthentication(connectedAppUser);

        var securityAppUser = (SecurityAppUser) ((UsernamePasswordAuthenticationToken) connectedAppUser).getPrincipal();
        var appUser = securityAppUser.getAppUser();
        if (!email.equals(appUser.getEmail())) {
            throw new EmailMatchingException("Email does not match the logged-in user");
        }
        deleteAppUserCommon(appUser, email);
    }

    @Override
    @Transactional
    public void deleteAppUserByEmail(String email){
        AppUser appUser = appUserRepository.findAppUsersByEmail(email)
                .orElseThrow(() -> new AppUserNotFoundException(String.format("User with email: %s not found", email)));

        deleteAppUserCommon(appUser, email);
    }

    private void checkConnectedUserAuthentication(Principal connectedAppUser) {
        if (!(connectedAppUser instanceof UsernamePasswordAuthenticationToken)) {
            throw new InvalidUserAuthenticationException("Invalid user authentication");
        }
    }
    private void deleteAppUserCommon(AppUser appUser, String email) {
        try {
            appUserRepository.delete(appUser);
        } catch (AppUserNotFoundException appUserNotFoundException) {
            log.error("User not found. {}", appUserNotFoundException.getMessage());
            throw new AppUserNotFoundException("User not found exception");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during user deletion", e);
        }
    }

    private Comparator<AppUser> getComparator(String sortBy, String order) {
        Comparator<AppUser> comparator = switch (sortBy) {
            case "firstName" -> Comparator.comparing(AppUser::getFirstName);
            case "lastName" -> Comparator.comparing(AppUser::getLastName);
            case "email" -> Comparator.comparing(AppUser::getEmail);
            case "age" -> Comparator.comparing(user -> user.getCourseRegistration().getAge());
            default -> throw new InvalidSortFieldException("Invalid sort field");
        };
        return "desc".equalsIgnoreCase(order) ? comparator.reversed() : comparator;
    }

    private RegistrationResponseDTO convertAppUserToRegistrationResponseDTO(AppUser appUser) {
        return RegistrationResponseDTO.builder()
                .firstName(appUser.getFirstName())
                .lastName(appUser.getLastName())
                .email(appUser.getEmail())
                .age(appUser.getCourseRegistration().getAge())
                .gender(appUser.getCourseRegistration().getGender())
                .bib(appUser.getCourseRegistration().getBib())
                .build();
    }
}