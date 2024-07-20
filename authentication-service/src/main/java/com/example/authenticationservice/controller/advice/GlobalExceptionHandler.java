package com.example.authenticationservice.controller.advice;

import com.example.authenticationservice.dto.ResponseMessage;
import com.example.authenticationservice.exception.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ResponseMessage> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        return new ResponseEntity<>(new ResponseMessage(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseMessage> handleRuntimeException(RuntimeException e) {
        return new ResponseEntity<>(new ResponseMessage("Failed to create user: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseMessage> handleException(Exception e) {
        return new ResponseEntity<>(new ResponseMessage("An unexpected error occurred: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
