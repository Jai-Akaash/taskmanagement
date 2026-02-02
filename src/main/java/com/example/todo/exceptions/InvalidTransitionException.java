package com.example.todo.exceptions;

public class InvalidTransitionException extends RuntimeException {
    public InvalidTransitionException(String message) { super(message); }
}