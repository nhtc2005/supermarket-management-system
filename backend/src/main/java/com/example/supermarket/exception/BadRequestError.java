package com.example.supermarket.exception;

import org.springframework.http.HttpStatus;

public class BadRequestError extends BaseException{
    public BadRequestError(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
