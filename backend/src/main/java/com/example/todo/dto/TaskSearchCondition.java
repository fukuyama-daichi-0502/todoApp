package com.example.todo.dto;

import com.example.todo.entity.TaskPriority;
import com.example.todo.entity.TaskStatus;

public class TaskSearchCondition {
    private String title;

    private TaskStatus status;

    private DueDateFilter dueDate;

    private TaskPriority priority;

    private String category;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public DueDateFilter getDueDate() {
        return dueDate;
    }

    public void setDueDate(DueDateFilter dueDate) {
        this.dueDate = dueDate;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    
}
