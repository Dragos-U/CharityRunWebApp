package com.bearsoft.charityrun.exceptions.handler;

import com.bearsoft.charityrun.exceptions.appuser.*;
import com.bearsoft.charityrun.exceptions.constraints.ConstraintViolationException;
import com.bearsoft.charityrun.exceptions.course.CourseNotFoundException;
import com.bearsoft.charityrun.exceptions.courseregistration.CourseRegistrationAlreadyExistsException;
import com.bearsoft.charityrun.exceptions.courseregistration.CourseRegistrationNotFoundException;
import com.bearsoft.charityrun.exceptions.email.EmailSendingException;
import com.bearsoft.charityrun.exceptions.event.EventAlreadyExistsException;
import com.bearsoft.charityrun.exceptions.event.EventNotFoundException;
import com.bearsoft.charityrun.exceptions.event.EventUpdateException;
import com.bearsoft.charityrun.models.exception.ApiException;
import com.bearsoft.charityrun.exceptions.course.CourseAlreadyExistException;
import com.bearsoft.charityrun.exceptions.constraints.ObjectNotValidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final HttpStatus BAD_REQUEST = HttpStatus.BAD_REQUEST;
    private static final HttpStatus CONFLICT = HttpStatus.CONFLICT;
    private static final HttpStatus FORBIDDEN = HttpStatus.FORBIDDEN;
    private static final HttpStatus UNAUTHORIZED = HttpStatus.UNAUTHORIZED;

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException httpMessageNotReadableException) {
        log.error("HttpMessageNotReadableException occurred: {}", httpMessageNotReadableException.getMessage(), httpMessageNotReadableException);

        ApiException apiException = ApiException.builder()
                .message(httpMessageNotReadableException.getMessage())
                .httpStatus(BAD_REQUEST)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, BAD_REQUEST);
    }

    @ExceptionHandler(ObjectNotValidException.class)
    public ResponseEntity<Object> handleObjectNotValidException(ObjectNotValidException objectNotValidException) {
        log.error("ObjectNotValidException occurred: {}", objectNotValidException.getMessage(), objectNotValidException);

        Set<String> errorMessages = objectNotValidException.getErrorMessages();
        String combinedErrorMessage = String.join(", ", errorMessages);
        ApiException apiException = ApiException.builder()
                .message(combinedErrorMessage)
                .httpStatus(BAD_REQUEST)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handlerConstraintViolationException(ConstraintViolationException constraintViolationException){
        log.error("ConstraintViolationException occurred: {}", constraintViolationException.getMessage(), constraintViolationException);

        ApiException apiException = ApiException.builder()
                .message(constraintViolationException.getMessage())
                .httpStatus(BAD_REQUEST)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, BAD_REQUEST);
    }

    @ExceptionHandler(PasswordDoesNotMatchException.class)
    public ResponseEntity<Object> handlerPasswordDoesNotMatchException(PasswordDoesNotMatchException passwordDoesNotMatchException) {
        log.error("PasswordDoesNotMatchException occurred: {}", passwordDoesNotMatchException.getMessage(), passwordDoesNotMatchException);
        ApiException apiException = ApiException.builder()
                .message(passwordDoesNotMatchException.getMessage())
                .httpStatus(FORBIDDEN)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, FORBIDDEN);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handlerUserNotFoundException(UserNotFoundException userNotFoundException) {
        log.error("UserNotFoundException occurred: {}", userNotFoundException.getMessage(), userNotFoundException);
        ApiException apiException = ApiException.builder()
                .message(userNotFoundException.getMessage())
                .httpStatus(BAD_REQUEST)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, BAD_REQUEST);
    }

    @ExceptionHandler(InvalidUserAuthenticationException.class)
    public ResponseEntity<Object> handlerInvalidUserAuthenticationException(InvalidUserAuthenticationException invalidUserAuthenticationException) {
        log.error("InvalidUserAuthenticationException occurred: {}", invalidUserAuthenticationException.getMessage(), invalidUserAuthenticationException);
        ApiException apiException = ApiException.builder()
                .message(invalidUserAuthenticationException.getMessage())
                .httpStatus(UNAUTHORIZED)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, UNAUTHORIZED);
    }

    @ExceptionHandler(EmailMatchingException.class)
    public ResponseEntity<Object> handlerEmailMatchingException(EmailMatchingException emailMatchingException) {
        log.error("EmailMatchingException occurred: {}", emailMatchingException.getMessage(), emailMatchingException);
        ApiException apiException = ApiException.builder()
                .message(emailMatchingException.getMessage())
                .httpStatus(BAD_REQUEST)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, BAD_REQUEST);
    }

    @ExceptionHandler(AppUserAlreadyExistsException.class)
    public ResponseEntity<Object> handlerAppUserAlreadyExistsException(AppUserAlreadyExistsException appUserAlreadyExistsException) {
        log.error("AppUserAlreadyExistsException occurred: {}", appUserAlreadyExistsException.getMessage(), appUserAlreadyExistsException);
        ApiException apiException = ApiException.builder()
                .message(appUserAlreadyExistsException.getMessage())
                .httpStatus(BAD_REQUEST)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, BAD_REQUEST);
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<Object> handlerEmailSendingException(EmailSendingException emailSendingException) {
        log.error("EmailSendingException occurred: {}", emailSendingException.getMessage());
        ApiException apiException = ApiException.builder()
                .message(emailSendingException.getMessage())
                .httpStatus(BAD_REQUEST)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();

        return new ResponseEntity<>(apiException, BAD_REQUEST);
    }

    @ExceptionHandler(CourseAlreadyExistException.class)
    public ResponseEntity<Object> handlerCourseAlreadyExistException(CourseAlreadyExistException courseAlreadyExistException) {
        log.error("CourseAlreadyExistException occurred: {}", courseAlreadyExistException.getMessage());
        ApiException apiException = ApiException.builder()
                .message(courseAlreadyExistException.getMessage())
                .httpStatus(CONFLICT)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, CONFLICT);
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<Object> handlerCourseNotFoundException(CourseNotFoundException courseNotFoundException) {
        ApiException apiException = ApiException.builder()
                .message(courseNotFoundException.getMessage())
                .httpStatus(BAD_REQUEST)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, BAD_REQUEST);
    }

    @ExceptionHandler(CourseRegistrationAlreadyExistsException.class)
    public ResponseEntity<Object> handlerCourseRegistrationAlreadyExistsException(CourseRegistrationAlreadyExistsException courseRegistrationAlreadyExistsException) {
        ApiException apiException = ApiException.builder()
                .message(courseRegistrationAlreadyExistsException.getMessage())
                .httpStatus(CONFLICT)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, CONFLICT);
    }

    @ExceptionHandler(CourseRegistrationNotFoundException.class)
    public ResponseEntity<Object> handlerCourseRegistrationNotFoundException(CourseRegistrationNotFoundException courseRegistrationNotFoundException) {
        ApiException apiException = ApiException.builder()
                .message(courseRegistrationNotFoundException.getMessage())
                .httpStatus(BAD_REQUEST)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, BAD_REQUEST);
    }

    @ExceptionHandler(EventAlreadyExistsException.class)
    public ResponseEntity<Object> handlerEventAlreadyExistsException(EventAlreadyExistsException eventAlreadyExistsException) {
        log.error("EventAlreadyExistsException occurred: {}", eventAlreadyExistsException.getMessage());

        ApiException apiException = ApiException.builder()
                .message(eventAlreadyExistsException.getMessage())
                .httpStatus(CONFLICT)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, CONFLICT);
    }

    @ExceptionHandler(EventUpdateException.class)
    public ResponseEntity<Object> handlerEventUpdateException(EventUpdateException eventUpdateException) {
        log.error("EventUpdateException occurred: {}", eventUpdateException.getMessage());
        ApiException apiException = ApiException.builder()
                .message(eventUpdateException.getMessage())
                .httpStatus(BAD_REQUEST)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, BAD_REQUEST);
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<Object> handlerEventNotFoundException(EventNotFoundException eventNotFoundException) {
        log.error("EventNotFoundException occurred: {}", eventNotFoundException.getMessage());
        ApiException apiException = ApiException.builder()
                .message(eventNotFoundException.getMessage())
                .httpStatus(BAD_REQUEST)
                .timeStamp(ZonedDateTime.now(ZoneId.of("Z")))
                .build();
        return new ResponseEntity<>(apiException, BAD_REQUEST);
    }
}