package com.t1.authjwtpattern.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BaseException extends ResponseStatusException {

    public BaseException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }

    public BaseException(HttpStatus httpStatus, int code) {
        super(httpStatus);
    }

    public BaseException(HttpStatus httpStatus, String message, Throwable cause) {
        super(httpStatus, message, cause);
    }

    public BaseException(HttpStatus httpStatus) {
        super(httpStatus);
    }
}
