package com.bearsoft.charityrun.exceptions.appuser;

public class AppUserAlreadyExistsException extends RuntimeException{

    public AppUserAlreadyExistsException(String message) {
        super(message);
    }
}
