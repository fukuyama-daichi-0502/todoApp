package com.example.todo.mapper;

import com.example.todo.dto.TaskCreateRequest;
import com.example.todo.dto.TaskResponse;
import com.example.todo.dto.TaskUpdateRequest;
import com.example.todo.entity.Task;

public class TaskMapper {

    //entityからレスポンス用のDTOに変換
    public static TaskResponse toTaskResponse(Task task) {
        return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getStatus(),
            task.getStatus().getLabel(),
            task.getCreatedAt(),
            task.getUpdatedAt(),
            task.getCompletedAt()
        );
    
    }

    //DTOからentityに変換
    public static Task toEntity(TaskCreateRequest request){
        Task task = new Task();

        task.setTitle(request.getTitle());

        return task;
    }

    //updateリクエストDTOをentityに変換
    public static void updateEntity(TaskUpdateRequest request, Task task){
        task.setTitle(request.getTitle());
        task.setStatus(request.getStatus());
    }

}

