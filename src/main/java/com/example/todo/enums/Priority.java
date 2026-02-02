package com.example.todo.enums;


public enum Priority {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    CRITICAL(4);


    private final int urgencyScore;


    Priority(int score) { this.urgencyScore = score; }
    public int getUrgencyScore() { return urgencyScore; }
}