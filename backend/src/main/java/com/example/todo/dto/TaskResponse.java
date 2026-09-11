package com.example.todo.dto;

import java.time.LocalDateTime;

import com.example.todo.entity.TaskStatus;

public class TaskResponse {
    
    private Long id;

    private String title;

    private TaskStatus status;

    private String statusLabel;

    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime completedAt;

    public TaskResponse(Long id, String title, TaskStatus status, String statusLabel, LocalDateTime created_at, LocalDateTime updated_at, LocalDateTime completed_at){
        this.id = id;
        this.title = title;
        this.status = status;
        this.statusLabel = statusLabel;
        this.createdAt = created_at;
        this.updatedAt = updated_at;
        this.completedAt = completed_at;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    

    
}
