package com.example.supermarket.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseException extends RuntimeException{
    final HttpStatus httpStatus;

    public BaseException(String message, HttpStatus httpStatus) {
        super(message == null ? httpStatus.getReasonPhrase() : message);
        this.httpStatus = httpStatus;
    }
}
