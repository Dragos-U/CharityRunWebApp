package com.bearsoft.charityrun.exceptions.appuser;

public class PasswordDoesNotMatchException extends RuntimeException{

    public PasswordDoesNotMatchException(String message) {
        super(message);
    }
}
