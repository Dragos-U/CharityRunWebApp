package com.bearsoft.charityrun.exceptions.handler;

import com.bearsoft.charityrun.exceptions.course.CourseNotFoundException;
import com.bearsoft.charityrun.exceptions.courseregistration.CourseRegistrationAlreadyExistsException;
import com.bearsoft.charityrun.exceptions.courseregistration.CourseRegistrationNotFoundException;
import com.bearsoft.charityrun.exceptions.event.EventAlreadyExistsException;
import com.bearsoft.charityrun.exceptions.event.EventNotFoundException;
import com.bearsoft.charityrun.exceptions.event.EventUpdateException;
import com.bearsoft.charityrun.models.exception.ApiException;
import com.bearsoft.charityrun.exceptions.appuser.PasswordDoesNotMatchException;
import com.bearsoft.charityrun.exceptions.appuser.UserNotFoundException;
import com.bearsoft.charityrun.exceptions.course.CourseAlreadyExistException;
import com.bearsoft.charityrun.exceptions.objectvalidator.ObjectNotValidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ObjectNotValidException.class)
    public ResponseEntity<Object> handleObjectNotValidException(ObjectNotValidException objectNotValidException){
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        log.error("ObjectNotValidException occurred: {}", objectNotValidException.getMessage(), objectNotValidException);

        Set<String> errorMessages = objectNotValidException.getErrorMessages();
        String combinedErrorMessage = String.join(", ", errorMessages);
        ApiException apiException = ApiException.builder()
                .message(combinedErrorMessage)
                .httpStatus(badRequest)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, badRequest);
    }

    @ExceptionHandler(PasswordDoesNotMatchException.class)
    public ResponseEntity<Object> handlerPasswordDoesNotMatchException(PasswordDoesNotMatchException passwordDoesNotMatchException){
        HttpStatus forbidden = HttpStatus.FORBIDDEN;
        log.error("PasswordDoesNotMatchException occurred: {}", passwordDoesNotMatchException.getMessage(), passwordDoesNotMatchException);
        ApiException apiException = new ApiException(
                passwordDoesNotMatchException.getMessage(),
                forbidden,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(apiException, forbidden);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handlerUserNotFoundException(UserNotFoundException userNotFoundException){
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        log.error("UserNotFoundException occurred: {}", userNotFoundException.getMessage(),userNotFoundException);
        ApiException apiException = ApiException.builder()
                .message(userNotFoundException.getMessage())
                .httpStatus(badRequest)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, badRequest);
    }

    @ExceptionHandler(CourseAlreadyExistException.class)
    public ResponseEntity<Object> handlerCourseAlreadyExistException(CourseAlreadyExistException courseAlreadyExistException){
        HttpStatus conflict = HttpStatus.CONFLICT;
        log.error("CourseAlreadyExistException occured: {}", courseAlreadyExistException.getMessage());
        ApiException apiException = ApiException.builder()
                .message(courseAlreadyExistException.getMessage())
                .httpStatus(conflict)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, conflict);
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<Object> handlerCourseNotFoundException(CourseNotFoundException courseNotFoundException){
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        log.error("CourseNotFoundException occurred: {}", courseNotFoundException.getMessage());
        ApiException apiException = ApiException.builder()
                .message(courseNotFoundException.getMessage())
                .httpStatus(badRequest)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, badRequest);
    }

    @ExceptionHandler(CourseRegistrationAlreadyExistsException.class)
    public ResponseEntity<Object> handlerCourseRegistrationAlreadyExistsException(CourseRegistrationAlreadyExistsException courseRegistrationAlreadyExistsException){
        HttpStatus conflict = HttpStatus.CONFLICT;
        log.error("CourseRegistrationAlreadyExistsException occurred: {}", courseRegistrationAlreadyExistsException.getMessage());
        ApiException apiException = ApiException.builder()
                .message(courseRegistrationAlreadyExistsException.getMessage())
                .httpStatus(conflict)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, conflict);
    }

    @ExceptionHandler(CourseRegistrationNotFoundException.class)
    public ResponseEntity<Object> handlerCourseRegistrationNotFoundException(CourseRegistrationNotFoundException courseRegistrationNotFoundException){
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        log.error("CourseRegistrationNotFound occurred: {}", courseRegistrationNotFoundException.getMessage());
        ApiException apiException = ApiException.builder()
                .message(courseRegistrationNotFoundException.getMessage())
                .httpStatus(badRequest)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, badRequest);
    }

    @ExceptionHandler(EventAlreadyExistsException.class)
    public ResponseEntity<Object> handlerEventAlreadyExistsException(EventAlreadyExistsException eventAlreadyExistsException){
        HttpStatus conflict = HttpStatus.CONFLICT;
        log.error("EventAlreadyExistsException occurred: {}", eventAlreadyExistsException.getMessage());

        ApiException apiException = ApiException.builder()
                .message(eventAlreadyExistsException.getMessage())
                .httpStatus(conflict)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, conflict);
    }

    @ExceptionHandler(EventUpdateException.class)
    public ResponseEntity<Object> handlerEventUpdateException(EventUpdateException eventUpdateException){
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        log.error("EventUpdateException occurred: {}", eventUpdateException.getMessage());
        ApiException apiException = ApiException.builder()
                .message(eventUpdateException.getMessage())
                .httpStatus(badRequest)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, badRequest);
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<Object> handlerEventNotFoundException(EventNotFoundException eventNotFoundException){
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        log.error("EventNotFoundException occurred: {}", eventNotFoundException.getMessage());
        ApiException apiException = ApiException.builder()
                .message(eventNotFoundException.getMessage())
                .httpStatus(badRequest)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, badRequest);
    }
}