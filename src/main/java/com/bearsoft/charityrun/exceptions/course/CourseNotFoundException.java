package com.bearsoft.charityrun.exceptions.course;

public class CourseNotFoundException extends RuntimeException{

    public CourseNotFoundException(String message){
        super(message);
    }
}
