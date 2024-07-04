package com.t1.authjwtpattern.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExist extends BaseException {
    public UserAlreadyExist(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
