package com.example.todo.dto;

import jakarta.validation.constraints.Size;

public class TaskDescriptionUpdateRequest {
    
    @Size(max = 2000, message = "詳細は2000文字以内で入力してください")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
