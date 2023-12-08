package com.bearsoft.charityrun.models.domain.dtos;

import com.bearsoft.charityrun.models.domain.enums.ExperienceType;
import com.bearsoft.charityrun.validators.annotations.EnumValidation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpenAiRequestDTO {

    @EnumValidation
    private ExperienceType runningExperienceType;

    @NotNull(message = "The runsPerWeek (frequency running) must not be null.")
    @Min(0)
    @Max(14)
    private int runsPerWeek;
}
