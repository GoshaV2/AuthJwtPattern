package com.t1.authjwtpattern.exception;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends BaseException {

    public AuthorizationException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
