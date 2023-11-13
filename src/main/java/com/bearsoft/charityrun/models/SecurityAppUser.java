package com.bearsoft.charityrun.models;

import com.bearsoft.charityrun.models.entities.AppUser;
import com.bearsoft.charityrun.models.entities.Role;
import com.bearsoft.charityrun.models.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@AllArgsConstructor
public class SecurityAppUser implements UserDetails {

    private AppUser appUser;

    @Override
    public String getUsername() {
        return appUser.getEmail();
    }

    @Override
    public String getPassword() {
        return appUser.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : appUser.getRoles()) {
            RoleType roleType = role.getRoleType();

            authorities.add(new SimpleGrantedAuthority(roleType.name()));
            roleType.getPermissionTypes().forEach(permissionType ->
                    authorities.add(new SimpleGrantedAuthority(permissionType.name()))
            );
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
