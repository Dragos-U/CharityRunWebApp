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
import com.bearsoft.charityrun.exceptions.ratelimiter.RateLimiterException;
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
    private static final HttpStatus UNAUTHORIZED = HttpStatus.UNAUTHORIZED;

    private static ZonedDateTime getCurrentTimeStamp() {
        return ZonedDateTime.now(ZoneId.of("Z"));
    }

    private static ResponseEntity<Object> getResponseEntity(String httpMessageNotReadableException, HttpStatus badRequest) {
        ApiException apiException = ApiException.builder()
                .message(httpMessageNotReadableException)
                .httpStatus(badRequest)
                .timeStamp(getCurrentTimeStamp())
                .build();
        return new ResponseEntity<>(apiException, badRequest);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException httpMessageNotReadableException) {
        log.error("HttpMessageNotReadableException occurred: {}", httpMessageNotReadableException.getMessage(), httpMessageNotReadableException);
        return getResponseEntity(httpMessageNotReadableException.getMessage(), BAD_REQUEST);
    }


    @ExceptionHandler(RateLimiterException.class)
    public ResponseEntity<Object> handleRateLimiterException(RateLimiterException limiterException) {
        log.error("HttpMessageNotReadableException occurred: {}", limiterException.getMessage(), limiterException);
        return getResponseEntity(limiterException.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(ObjectNotValidException.class)
    public ResponseEntity<Object> handleObjectNotValidException(ObjectNotValidException objectNotValidException) {
        log.error("ObjectNotValidException occurred: {}", objectNotValidException.getMessage(), objectNotValidException);
        Set<String> errorMessages = objectNotValidException.getErrorMessages();
        String combinedErrorMessage = String.join(", ", errorMessages);
        return getResponseEntity(combinedErrorMessage, BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handlerConstraintViolationException(ConstraintViolationException constraintViolationException) {
        log.error("ConstraintViolationException occurred: {}", constraintViolationException.getMessage(), constraintViolationException);
        return getResponseEntity(constraintViolationException.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler(PasswordDoesNotMatchException.class)
    public ResponseEntity<Object> handlerPasswordDoesNotMatchException(PasswordDoesNotMatchException passwordDoesNotMatchException) {
        log.error("PasswordDoesNotMatchException occurred: {}", passwordDoesNotMatchException.getMessage(), passwordDoesNotMatchException);
        return getResponseEntity(passwordDoesNotMatchException.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler(AppUserNotFoundException.class)
    public ResponseEntity<Object> handlerUserNotFoundException(AppUserNotFoundException appUserNotFoundException) {
        log.error("UserNotFoundException occurred: {}", appUserNotFoundException.getMessage(), appUserNotFoundException);
        return getResponseEntity(appUserNotFoundException.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler(InvalidUserAuthenticationException.class)
    public ResponseEntity<Object> handlerInvalidUserAuthenticationException(InvalidUserAuthenticationException invalidUserAuthenticationException) {
        log.error("InvalidUserAuthenticationException occurred: {}", invalidUserAuthenticationException.getMessage(), invalidUserAuthenticationException);
        return getResponseEntity(invalidUserAuthenticationException.getMessage(), UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidSortFieldException.class)
    public ResponseEntity<Object> handlerInvalidSortFieldException(InvalidSortFieldException invalidSortFieldException) {
        log.error("InvalidSortFieldException occurred: {}", invalidSortFieldException.getMessage());
        return getResponseEntity(invalidSortFieldException.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<Object> handlerTokenNotFoundException(TokenNotFoundException tokenNotFoundException) {
        log.error("TokenNotFoundException occurred: {}", tokenNotFoundException.getMessage());
        return getResponseEntity(tokenNotFoundException.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler(EmailMatchingException.class)
    public ResponseEntity<Object> handlerEmailMatchingException(EmailMatchingException emailMatchingException) {
        log.error("EmailMatchingException occurred: {}", emailMatchingException.getMessage(), emailMatchingException);
        return getResponseEntity(emailMatchingException.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler(AppUserAlreadyExistsException.class)
    public ResponseEntity<Object> handlerAppUserAlreadyExistsException(AppUserAlreadyExistsException appUserAlreadyExistsException) {
        log.error("AppUserAlreadyExistsException occurred: {}", appUserAlreadyExistsException.getMessage(), appUserAlreadyExistsException);
        return getResponseEntity(appUserAlreadyExistsException.getMessage(), CONFLICT);
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<Object> handlerEmailSendingException(EmailSendingException emailSendingException) {
        log.error("EmailSendingException occurred: {}", emailSendingException.getMessage());
        return getResponseEntity(emailSendingException.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler(CourseAlreadyExistException.class)
    public ResponseEntity<Object> handlerCourseAlreadyExistException(CourseAlreadyExistException courseAlreadyExistException) {
        log.error("CourseAlreadyExistException occurred: {}", courseAlreadyExistException.getMessage());
        return getResponseEntity(courseAlreadyExistException.getMessage(), CONFLICT);
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<Object> handlerCourseNotFoundException(CourseNotFoundException courseNotFoundException) {
        return getResponseEntity(courseNotFoundException.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler(CourseRegistrationAlreadyExistsException.class)
    public ResponseEntity<Object> handlerCourseRegistrationAlreadyExistsException(CourseRegistrationAlreadyExistsException courseRegistrationAlreadyExistsException) {
        return getResponseEntity(courseRegistrationAlreadyExistsException.getMessage(), CONFLICT);
    }

    @ExceptionHandler(CourseRegistrationNotFoundException.class)
    public ResponseEntity<Object> handlerCourseRegistrationNotFoundException(CourseRegistrationNotFoundException courseRegistrationNotFoundException) {
        return getResponseEntity(courseRegistrationNotFoundException.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler(EventAlreadyExistsException.class)
    public ResponseEntity<Object> handlerEventAlreadyExistsException(EventAlreadyExistsException eventAlreadyExistsException) {
        log.error("EventAlreadyExistsException occurred: {}", eventAlreadyExistsException.getMessage());
        return getResponseEntity(eventAlreadyExistsException.getMessage(), CONFLICT);
    }

    @ExceptionHandler(EventUpdateException.class)
    public ResponseEntity<Object> handlerEventUpdateException(EventUpdateException eventUpdateException) {
        log.error("EventUpdateException occurred: {}", eventUpdateException.getMessage());
        return getResponseEntity(eventUpdateException.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<Object> handlerEventNotFoundException(EventNotFoundException eventNotFoundException) {
        log.error("EventNotFoundException occurred: {}", eventNotFoundException.getMessage());
        return getResponseEntity(eventNotFoundException.getMessage(), BAD_REQUEST);
    }
}