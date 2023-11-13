package com.bearsoft.charityrun.services;

import com.bearsoft.charityrun.models.SecurityAppUser;
import com.bearsoft.charityrun.models.dtos.ChangePasswordDTO;
import com.bearsoft.charityrun.repositories.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SecurityAppUser loadUserByUsername(String email) throws UsernameNotFoundException {
        var appUser = appUserRepository.findAppUsersByEmail(email);

        return appUser.map(SecurityAppUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("User email not found."));
    }

    public void changePassword(ChangePasswordDTO changePasswordDTO, Principal connectedAppUser) {
        var securityAppUser = (SecurityAppUser) (((UsernamePasswordAuthenticationToken) connectedAppUser).getPrincipal());
        var appUser = securityAppUser.getAppUser();

        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), appUser.getPassword())){
            throw new IllegalStateException(("Wrong password"));
        }

        if(!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmationPassword())){
            throw new IllegalStateException("Passwords are not the same");
        }

        appUser.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        appUserRepository.save(appUser);
    }
}
