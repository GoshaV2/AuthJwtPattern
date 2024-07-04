package com.t1.authjwtpattern.exception.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.t1.authjwtpattern.exception.BaseException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {
        List<String> messages = new ArrayList<>();

        int numberOfErrorMessage = 1;
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            if (e.getBindingResult().getFieldErrors().size() > 1) {
                messages.add(String.format(
                        "Wrong request param #%s - %s",
                        numberOfErrorMessage,
                        error.getDefaultMessage())
                );
            } else {
                messages.add(error.getDefaultMessage());
            }
            numberOfErrorMessage++;
        }
        String exceptionMessage = String.join("<br />", messages);

        ExceptionDetail exceptionDetail = ExceptionDetail.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST)
                .message(exceptionMessage)
                .details(request.getDescription(true))
                .build();
        log.warn(e.getMessage());
        return handleExceptionInternal(e, exceptionDetail, headers, exceptionDetail.getStatus(), request);
    }

    @ExceptionHandler(value
            = {BaseException.class})
    protected ResponseEntity<Object> handleAmountPatternException(
            BaseException exception, WebRequest request) {
        log.error(exception.getMessage(), exception);
        return handleExceptionInternal(exception,
                ExceptionDetail.builder()
                        .httpCode(exception.getStatusCode().value())
                        .details(request.getDescription(true))
                        .message(exception.getMessage())
                        .timestamp(LocalDateTime.now())
                        .status(exception.getStatusCode())
                        .build(),
                new HttpHeaders(), exception.getStatusCode(), request);
    }

    @ExceptionHandler(value
            = {AuthenticationException.class, JwtException.class})
    protected ResponseEntity<Object> handleAuthenticationException(
            RuntimeException exception, WebRequest request) {
        if (!(exception instanceof ExpiredJwtException)) {
            log.error(exception.getMessage(), exception);
        }
        return handleExceptionInternal(exception,
                ExceptionDetail.builder()
                        .httpCode(HttpStatus.UNAUTHORIZED.value())
                        .details(request.getDescription(true))
                        .message(exception.getMessage())
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.UNAUTHORIZED)
                        .build(),
                new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(value
            = {RuntimeException.class})
    protected ResponseEntity<Object> handleRuntimeException(
            RuntimeException exception, WebRequest request) {
        log.error(exception.getMessage(), exception);
        return handleExceptionInternal(exception,
                ExceptionDetail.builder()
                        .httpCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .details(request.getDescription(true))
                        .message(exception.getMessage())
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build(),
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExceptionDetail {

        @JsonProperty(value = "timestamp")
        private LocalDateTime timestamp;

        @JsonProperty(value = "status")
        private HttpStatusCode status;

        @JsonProperty(value = "message")
        private String message;

        @JsonProperty(value = "details")
        private String details;

        @JsonProperty(value = "httpCode")
        private int httpCode;
    }
}

