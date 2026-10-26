package com.example.todo.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.todo.dto.SubtaskCreateRequest;
import com.example.todo.dto.TaskCreateRequest;
import com.example.todo.dto.TaskReorderRequest;
import com.example.todo.dto.TaskResponse;
import com.example.todo.dto.TaskSearchCondition;
import com.example.todo.dto.TaskStatusUpdateRequest;
import com.example.todo.dto.TaskUpdateRequest;
import com.example.todo.dto.TaskDescriptionUpdateRequest;
import com.example.todo.service.TaskService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:5173")
public class TaskController {
    private final TaskService taskService;
    
    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(@ModelAttribute TaskSearchCondition condition){
        List<TaskResponse> tasks = taskService.searchTasks(condition);

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{parentId}/subtasks")
    public ResponseEntity<List<TaskResponse>> getSubtasks(@PathVariable Long parentId){
        List<TaskResponse> subtasks = taskService.getSubtasks(parentId);

        return ResponseEntity.ok(subtasks);
    }
    
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreateRequest request) {
        TaskResponse response = taskService.createTask(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //サブタスクの作成
    @PostMapping("/{parentId}/subtasks")
    public ResponseEntity<TaskResponse> createSubtask(@PathVariable Long parentId, @Valid @RequestBody SubtaskCreateRequest request){
        TaskResponse response = taskService.createSubtask(parentId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateRequest request){
        TaskResponse response = taskService.updateTask(id,request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable Long id, @Valid @RequestBody TaskStatusUpdateRequest request){
        TaskResponse response = taskService.updateTaskStatus(id, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id){
        TaskResponse response = taskService.getTask(id);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);

        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/reorder")
    public ResponseEntity<Void> reorderTask(@Valid @RequestBody TaskReorderRequest request){
        taskService.reorderTask(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/description")
    public ResponseEntity<TaskResponse> updateTaskDescription(@PathVariable Long id, @Valid @RequestBody TaskDescriptionUpdateRequest request){
        TaskResponse response = taskService.updateTaskDescription(id, request);

        return ResponseEntity.ok(response);
    }

    

}
