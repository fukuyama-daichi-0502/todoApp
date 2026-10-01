package com.example.todo.dto;

import jakarta.validation.constraints.NotNull;

public class TaskReorderRequest {
    @NotNull
    private Long taskId;

    @NotNull
    private Long targetTaskId;

    @NotNull
    private ReorderPosition position;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getTargetTaskId() {
        return targetTaskId;
    }

    public void setTargetTaskId(Long targetTaskId) {
        this.targetTaskId = targetTaskId;
    }

    public ReorderPosition getPosition() {
        return position;
    }

    public void setPosition(ReorderPosition position) {
        this.position = position;
    }

    
}
