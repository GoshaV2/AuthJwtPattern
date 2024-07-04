package com.t1.authjwtpattern.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class NotFoundElementException extends BaseException {

    public NotFoundElementException(UUID id, Class<?> type) {
        this(String.format("Entity(%s) with id=%s not be found", type.getName(), id));
    }

    public NotFoundElementException(String message) {
        super(HttpStatus.NOT_FOUND, String.format(message));
    }

    public NotFoundElementException(String format, Object... args) {
        this(String.format(format, args));
    }

    public NotFoundElementException(UUID id, String type) {
        this(String.format("Entity(%s) with id=%s not be found", type, id));
    }

}