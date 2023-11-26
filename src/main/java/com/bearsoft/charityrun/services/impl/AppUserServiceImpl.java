package com.bearsoft.charityrun.services.impl;

import com.bearsoft.charityrun.exceptions.appuser.EmailMatchingException;
import com.bearsoft.charityrun.exceptions.appuser.InvalidUserAuthenticationException;
import com.bearsoft.charityrun.exceptions.appuser.PasswordDoesNotMatchException;
import com.bearsoft.charityrun.exceptions.appuser.AppUserNotFoundException;
import com.bearsoft.charityrun.models.domain.entities.AppUser;
import com.bearsoft.charityrun.models.security.SecurityAppUser;
import com.bearsoft.charityrun.models.domain.dtos.AppUserDTO;
import com.bearsoft.charityrun.models.domain.dtos.ChangePasswordDTO;
import com.bearsoft.charityrun.models.validation.OnUpdate;
import com.bearsoft.charityrun.repositories.AppUserRepository;
import com.bearsoft.charityrun.repositories.RefreshTokenRepository;
import com.bearsoft.charityrun.repositories.TokenRepository;
import com.bearsoft.charityrun.services.AppUserService;
import com.bearsoft.charityrun.validators.ObjectsValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService, UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenRepository tokenRepository;
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
    public String deletedConnectedAppUser(String email, Principal connectedAppUser) {
        checkConnectedUserAuthentication(connectedAppUser);

        var securityAppUser = (SecurityAppUser) ((UsernamePasswordAuthenticationToken) connectedAppUser).getPrincipal();
        var appUser = securityAppUser.getAppUser();
        if (!email.equals(appUser.getEmail())) {
            throw new EmailMatchingException("Email does not match the logged-in user");
        }
        return deleteAppUserCommon(appUser, email);
    }

    @Override
    @Transactional
    public String deleteAppUserByEmail(String email){
        AppUser appUser = appUserRepository.findAppUsersByEmail(email)
                .orElseThrow(() -> new AppUserNotFoundException(String.format("User with email: %s not found", email)));

        return deleteAppUserCommon(appUser, email);
    }

    private void checkConnectedUserAuthentication(Principal connectedAppUser) {
        if (!(connectedAppUser instanceof UsernamePasswordAuthenticationToken)) {
            throw new InvalidUserAuthenticationException("Invalid user authentication");
        }
    }
    private String deleteAppUserCommon(AppUser appUser, String email) {
        try {
            refreshTokenRepository.deleteByAppUserId(appUser.getId());
            tokenRepository.deleteByAppUserId(appUser.getId());
            appUserRepository.delete(appUser);
            return String.format("User %s was successfully deleted", email);
        } catch (AppUserNotFoundException appUserNotFoundException) {
            log.error("User not found. {}", appUserNotFoundException.getMessage());
            throw new AppUserNotFoundException("User not found exception");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error during user deletion", e);
        }
    }
}
