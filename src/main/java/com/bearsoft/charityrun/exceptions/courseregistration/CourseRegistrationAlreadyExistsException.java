package com.bearsoft.charityrun.exceptions.courseregistration;

public class CourseRegistrationAlreadyExistsException extends RuntimeException{

    public CourseRegistrationAlreadyExistsException(String message) {
        super(message);
    }
}
