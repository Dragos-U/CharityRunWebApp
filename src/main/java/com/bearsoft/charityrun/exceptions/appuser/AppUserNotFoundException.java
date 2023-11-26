package com.bearsoft.charityrun.exceptions.appuser;

public class AppUserNotFoundException extends RuntimeException{

    public AppUserNotFoundException(String message){
        super(message);
    }
}
