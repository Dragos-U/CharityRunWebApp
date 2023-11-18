package com.bearsoft.charityrun.services;

import com.bearsoft.charityrun.exceptions.appuser.UserNotFoundException;
import com.bearsoft.charityrun.models.domain.entities.AppUser;
import com.bearsoft.charityrun.models.security.SecurityAppUser;
import com.bearsoft.charityrun.models.domain.dtos.AppUserDTO;
import com.bearsoft.charityrun.models.domain.dtos.ChangePasswordDTO;
import com.bearsoft.charityrun.repositories.AppUserRepository;
import com.bearsoft.charityrun.repositories.RefreshTokenRepository;
import com.bearsoft.charityrun.repositories.TokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService, UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public SecurityAppUser loadUserByUsername(String email) throws UsernameNotFoundException {
        var appUser = appUserRepository.findAppUsersByEmail(email);

        return appUser.map(SecurityAppUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("User email not found."));
    }

    @Override
    @Transactional
    public AppUserDTO getConnectedAppUserData(Principal connectedAppUser) {
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
        List<AppUser> appUsers = appUserRepository.findAllUsers();
        return appUsers
                .stream()
                .map(appUser -> objectMapper.convertValue(appUser, AppUserDTO.class))
                .toList();
    }

    @Override
    @Transactional
    public AppUserDTO updateConnectedAppUserData(AppUserDTO appUserDTO, Principal connectedAppUser) {
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
        appUser = appUserRepository.findAppUsersByEmail(appUserDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User with email: " + appUserDTO.getEmail() + " not found."));

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
        var securityAppUser = (SecurityAppUser) ((UsernamePasswordAuthenticationToken) connectedAppUser).getPrincipal();
        var appUser = securityAppUser.getAppUser();

        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), appUser.getPassword())) {
            throw new IllegalStateException(("Wrong password"));
        }

        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmationPassword())) {
            throw new IllegalStateException("Passwords are not the same");
        }

        appUser.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        appUserRepository.save(appUser);
    }

    @Override
    @Transactional
    public boolean deletedConnectedAppUser(String email, Principal connectedAppUser) {
        var securityAppUser = (SecurityAppUser) ((UsernamePasswordAuthenticationToken) connectedAppUser).getPrincipal();
        var appUser = securityAppUser.getAppUser();
        if(email.equals(appUser.getEmail())) {
            try {
                refreshTokenRepository.deleteByAppUserId(appUser.getId());
                tokenRepository.deleteByAppUserId(appUser.getId());
                appUserRepository.delete(appUser);
                return true;
            } catch (UserNotFoundException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting user", e);
            }
        }
        return false;
    }
}
