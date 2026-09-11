package com.example.todo.entity;

public enum TaskStatus {
    
    TODO("未着手"),
    IN_PROGRESS("進行中"),
    ON_HOLD("保留"),
    DONE("完了"),
    CANCELLED("中止");

    private final String label;

    public String getLabel() {
        return label;
    }

     TaskStatus(String label) {
        this.label = label;
    }

}
