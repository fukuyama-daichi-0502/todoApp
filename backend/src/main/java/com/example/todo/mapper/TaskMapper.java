package com.example.todo.mapper;

import com.example.todo.dto.TaskCreateRequest;
import com.example.todo.dto.TaskResponse;
import com.example.todo.dto.TaskUpdateRequest;
import com.example.todo.entity.Task;
import com.example.todo.entity.TaskPriority;

public class TaskMapper {

    //entityからレスポンス用のDTOに変換
    public static TaskResponse toTaskResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setStatusLabel(task.getStatus().getLabel());
        response.setDueDate(task.getDueDate());
        response.setPriority(task.getPriority());
        response.setPriorityLabel(task.getPriority() == null ? TaskPriority.MEDIUM.getLabel() : task.getPriority().getLabel());
        response.setSortOrder(task.getSortOrder());
        response.setParentTaskId(task.getParentTask() == null ? null: task.getParentTask().getId());
        response.setRecurrenceRule(task.getRecurrenceRule());
        response.setRecurrenceBaseDay(task.getRecurrenceBaseDay());
        response.setNextTaskId(task.getNextTask() == null ? null: task.getNextTask().getId());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        response.setCompletedAt(task.getCompletedAt());
        
        if(task.getCategory() != null){
            response.setCategoryId(task.getCategory().getId());
            response.setCategoryName(task.getCategory().getName());
            response.setCategoryColor(task.getCategory().getColor());
        }

        return response;
    
    }

    //DTOからentityに変換
    public static Task toEntity(TaskCreateRequest request){
        Task task = new Task();

        task.setTitle(request.getTitle());
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority());

        return task;
    }

    //updateリクエストDTOをentityに変換
    public static void updateEntity(TaskUpdateRequest request, Task task){
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setDueDate(request.getDueDate());
        task.setPriority(request.getPriority());
    }

}

