package com.example.todo.entity;

public enum TaskPriority {
    LOW("低"),
    MEDIUM("中"),
    HIGH("高");

    private final String label;

    TaskPriority(String label){
        this.label = label;
    }

    public String getLabel(){
        return label;
    }

    
}
