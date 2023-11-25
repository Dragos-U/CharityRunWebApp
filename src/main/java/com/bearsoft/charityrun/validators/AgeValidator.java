package com.bearsoft.charityrun.validators;

import com.bearsoft.charityrun.validators.annotations.AgeValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AgeValidator implements ConstraintValidator<AgeValidation, Integer> {

    private int minAge;
    private int maxAge;

    @Override
    public void initialize(AgeValidation constraintAnnotation) {
        this.minAge = constraintAnnotation.minAge();
        this.maxAge = constraintAnnotation.maxAge();
    }

    @Override
    public boolean isValid(Integer age, ConstraintValidatorContext constraintValidatorContext) {
        if(age == null){
            return true;
        }
        return age >= minAge && age <= maxAge;
    }
}
