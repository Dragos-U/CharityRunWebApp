package com.bearsoft.charityrun.models.domain.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChangePasswordDTO {

    private String currentPassword;
    private String newPassword;
    private String confirmationPassword;
}
