package com.example.todo.dto;

import java.time.LocalDate;

import com.example.todo.entity.TaskPriority;
import com.example.todo.entity.TaskStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TaskUpdateRequest {
    @NotBlank(message = "タイトルを入力してください")
    @Size(max = 100, message = "タイトルは100文字以内で入力してください")
    private String title;

    @Size(max = 2000, message = "詳細は2000文字以内で入力してください")
    private String description;

    @NotNull(message = "ステータスは必須です")
    private TaskStatus status;

    private LocalDate dueDate;

    private TaskPriority priority;
    
    private Long categoryId;

    public TaskUpdateRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    
}
