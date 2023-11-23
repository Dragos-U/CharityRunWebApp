package com.bearsoft.charityrun.exceptions.appuser;

public class InvalidUserAuthenticationException extends RuntimeException{

    public InvalidUserAuthenticationException(String message) {
        super(message);
    }
}
