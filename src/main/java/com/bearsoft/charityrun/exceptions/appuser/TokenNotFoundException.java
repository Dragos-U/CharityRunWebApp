package com.bearsoft.charityrun.exceptions.appuser;

public class TokenNotFoundException extends RuntimeException{

    public TokenNotFoundException(String message) {
        super(message);
    }
}
