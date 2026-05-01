package com.example.supermarket.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedError extends BaseException{
    public UnauthorizedError(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
