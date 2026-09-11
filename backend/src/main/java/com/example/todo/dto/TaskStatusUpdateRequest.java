package com.example.todo.dto;

import com.example.todo.entity.TaskStatus;

import jakarta.validation.constraints.NotNull;

public class TaskStatusUpdateRequest {
    
    @NotNull(message = "ステータスは必須です")
    private TaskStatus status;

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

}
