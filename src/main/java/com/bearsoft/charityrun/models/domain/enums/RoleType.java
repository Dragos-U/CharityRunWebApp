package com.bearsoft.charityrun.models.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {
    ROLE_ADMIN,
    ROLE_USER,
    ROLE_PARTICIPANT
}
