package com.bearsoft.charityrun.validator;

import com.bearsoft.charityrun.exceptions.objectvalidator.ObjectNotValidException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ObjectsValidator<T> {

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    public void validate(T objectToValidate, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validator.validate(objectToValidate, groups);
        if(!violations.isEmpty()) {
            var errorMessages = violations
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toSet());

            log.warn("Validation failed for object: {}", objectToValidate);
            log.warn("Validation errors: {}", errorMessages);

            throw new ObjectNotValidException(errorMessages);
        }
    }
}
