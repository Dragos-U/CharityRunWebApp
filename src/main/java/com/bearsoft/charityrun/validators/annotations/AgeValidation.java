package com.bearsoft.charityrun.validators.annotations;

import com.bearsoft.charityrun.validators.AgeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AgeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AgeValidation {

    String message() default "Invalid age";
    Class<?>[] groups() default  {};
    Class<? extends Payload>[] payload() default {};

    int minAge() default 0;
    int maxAge() default 120;
}
