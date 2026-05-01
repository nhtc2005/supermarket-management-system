package com.example.supermarket.exception;

import org.springframework.http.HttpStatus;

public class ConflictError extends BaseException{
    public ConflictError(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
