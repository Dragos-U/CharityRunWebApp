package com.bearsoft.charityrun.models.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bearsoft.charityrun.models.enums.PermissionType.*;

@Getter
@RequiredArgsConstructor
public enum RoleType {
    ROLE_ADMIN ( Set.of(
            ADMIN_READ,
            ADMIN_UPDATE,
            ADMIN_CREATE,
            ADMIN_DELETE
    )),
    ROLE_USER (Set.of(
            USER_READ,
            USER_CREATE,
            USER_UPDATE,
            USER_DELETE
    )),
    ROLE_PARTICIPANT (Set.of(
            PARTICIPANT_READ
    ));

    private final Set<PermissionType> permissionTypes;
}
