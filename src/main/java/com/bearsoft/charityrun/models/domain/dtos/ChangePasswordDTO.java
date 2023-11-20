package com.bearsoft.charityrun.models.domain.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChangePasswordDTO {

    @NotEmpty(message = "The CURRENT PASSWORD must not be empty.")
    private String currentPassword;

    @NotEmpty(message = "The NEW PASSWORD must not be empty.")
    private String newPassword;

    @NotEmpty(message = "The CONFIRMATION PASSWORD must not be empty.")
    private String confirmationPassword;
}
