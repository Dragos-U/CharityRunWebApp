package com.bearsoft.charityrun.exceptions.event;

public class EventAlreadyExistsException extends RuntimeException{

    public EventAlreadyExistsException(String message){
        super(message);
    }
}
