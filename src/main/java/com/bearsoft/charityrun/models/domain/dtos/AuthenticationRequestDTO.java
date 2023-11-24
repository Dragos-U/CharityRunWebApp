package com.bearsoft.charityrun.models.domain.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequestDTO {

  @NotEmpty(message = "User's EMAIL must not be empty.")
  private String email;

  @NotEmpty(message = "User's PASSWORD must not be empty.")
  private String password;
}
