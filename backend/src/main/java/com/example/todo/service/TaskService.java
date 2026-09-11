package com.example.todo.service;

import com.example.todo.dto.TaskCreateRequest;
import com.example.todo.dto.TaskResponse;
import com.example.todo.dto.TaskStatusUpdateRequest;
import com.example.todo.dto.TaskUpdateRequest;
import com.example.todo.entity.Task;
import com.example.todo.entity.TaskStatus;
import com.example.todo.exception.TaskNotFoundException;
import com.example.todo.mapper.TaskMapper;
import com.example.todo.repository.IFTaskRepository;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class TaskService {
    private final IFTaskRepository taskRepository;

    public TaskService(IFTaskRepository IFTaskRepository){
        this.taskRepository = IFTaskRepository;
    }

    //タスク一覧取得
    public List<TaskResponse> getTasks(){
        List<Task> tasks = taskRepository.findAll(Sort.by(Sort.Order.asc("createdAt")));
        List<TaskResponse> responses = new ArrayList<>();

        for(Task task:tasks){
            TaskResponse response = TaskMapper.toTaskResponse(task);
            
            responses.add(response);
        }
    
        return responses;
    }

    //タスク1件の取得
    public TaskResponse getTask(Long id) {

    Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("タスクが見つかりません"));

    return TaskMapper.toTaskResponse(task);
    }

    //新規タスク作成
    public TaskResponse createTask(TaskCreateRequest request){
        Task task = TaskMapper.toEntity(request);

        task.setStatus(TaskStatus.TODO);
        Task savedTask = taskRepository.save(task);

        return TaskMapper.toTaskResponse(savedTask);
    }

    //既存タスクのアップデート
    @Transactional
    public TaskResponse updateTask(Long id, TaskUpdateRequest request){
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("タスクが見つかりません"));

        TaskStatus beforeStatus = task.getStatus();
        TaskStatus afterStatus = request.getStatus();

        //すでに完了しているタスクでないかのチェック
        if(beforeStatus != TaskStatus.DONE && afterStatus == TaskStatus.DONE){
            task.setCompletedAt(LocalDateTime.now());
        }else if(beforeStatus == TaskStatus.DONE && afterStatus != TaskStatus.DONE){
            task.setCompletedAt(null);
        }

        TaskMapper.updateEntity(request,task);
        Task savedTask = taskRepository.save(task);

        return TaskMapper.toTaskResponse(savedTask);
    }

    //ステータス更新
    public TaskResponse updateTaskStatus(Long id, TaskStatusUpdateRequest request){
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("タスクが見つかりません"));
        TaskStatus beforeStatus = task.getStatus();
        TaskStatus afterStatus = request.getStatus();

        if(beforeStatus != TaskStatus.DONE && afterStatus == TaskStatus.DONE){
            task.setCompletedAt(LocalDateTime.now());
        }else if(beforeStatus == TaskStatus.DONE && afterStatus != TaskStatus.DONE){
            task.setCompletedAt(null);
        }

        task.setStatus(afterStatus);
        Task savedTask = taskRepository.save(task);
        return TaskMapper.toTaskResponse(savedTask);

    }

    //タスク削除
    public void deleteTask(Long id) {

    Task task = taskRepository.findById(id).orElseThrow(() ->new TaskNotFoundException("タスクが見つかりません"));

    taskRepository.delete(task);
    }

        


}
