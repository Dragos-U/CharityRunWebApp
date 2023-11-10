package com.bearsoft.charityrun.services;

import com.bearsoft.charityrun.models.SecurityAppUser;
import com.bearsoft.charityrun.repositories.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var appUser = appUserRepository.findAppUsersByEmail(email);

        return appUser.map(SecurityAppUser::new)
                .orElseThrow(()-> new UsernameNotFoundException("User email not found."));
    }
}
