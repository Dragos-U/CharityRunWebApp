package com.bearsoft.charityrun.exceptions.constraints;

public class ConstraintViolationException extends RuntimeException{

    public ConstraintViolationException(String message) {
        super(message);
    }
}
