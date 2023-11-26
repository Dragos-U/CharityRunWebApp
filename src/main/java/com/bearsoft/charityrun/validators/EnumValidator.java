package com.bearsoft.charityrun.validators;

import com.bearsoft.charityrun.models.domain.enums.CourseType;
import com.bearsoft.charityrun.models.domain.enums.GenderType;
import com.bearsoft.charityrun.models.domain.enums.TShirtSize;
import com.bearsoft.charityrun.validators.annotations.EnumValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class EnumValidator implements ConstraintValidator<EnumValidation, Enum<?>> {

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        boolean isValid = true;
        if (value == null || !isValidEnumValue(value)) {
            isValid = false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid enum value").addConstraintViolation();
        }
        return isValid;
    }

    private boolean isValidEnumValue(Enum<?> value) {
        if (value instanceof CourseType) {
            return Arrays.asList(CourseType.values()).contains(value);
        } else if (value instanceof TShirtSize) {
            return Arrays.asList(TShirtSize.values()).contains(value);
        } else if (value instanceof GenderType) {
            return Arrays.asList(GenderType.values()).contains(value);
        }
        return false;
    }
}
