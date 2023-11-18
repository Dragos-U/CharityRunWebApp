package com.bearsoft.charityrun.exceptions.appuser;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(String message){
        super(message);
    }
}
