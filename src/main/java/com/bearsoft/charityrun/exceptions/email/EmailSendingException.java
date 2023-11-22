package com.bearsoft.charityrun.exceptions.email;

public class EmailSendingException extends RuntimeException{

    public EmailSendingException(String message) {
        super(message);
    }
}
