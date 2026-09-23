package com.junior.todomanager.exceptions;

public class DateInvalidException extends RuntimeException {
    public DateInvalidException(String message) {
        super(message);
    }
}
