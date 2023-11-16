package com.bearsoft.charityrun.exceptions.course;

public class CourseAlreadyExistException extends RuntimeException{

    public CourseAlreadyExistException(String message){
        super(message);
    }
}
