package com.bearsoft.charityrun.models.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Getter
@Builder
public class ApiException {

    private final String message;
    private final HttpStatus httpStatus;
    private final ZonedDateTime timeStamp;
}
