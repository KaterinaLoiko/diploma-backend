package com.netology.diploma.loikokate.diplomabackend.exception.handler;

import com.netology.diploma.loikokate.diplomabackend.dto.exception.ErrorResponse;
import com.netology.diploma.loikokate.diplomabackend.exception.UserNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class UserNotFoundExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {UserNotFoundException.class})
    protected ResponseEntity<Object> handleException(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, ErrorResponse.builder().message(ex.getMessage()).build(),
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}
