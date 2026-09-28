package com.junior.todomanager.exceptions;

public class TaskAlreadyFinishedException extends RuntimeException {
    public TaskAlreadyFinishedException(String message) {
        super(message);
    }
}
