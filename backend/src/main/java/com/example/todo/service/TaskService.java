package com.example.todo.service;

import com.example.todo.dto.ReorderPosition;
import com.example.todo.dto.TaskCreateRequest;
import com.example.todo.dto.TaskDescriptionUpdateRequest;
import com.example.todo.dto.TaskReorderRequest;
import com.example.todo.dto.TaskResponse;
import com.example.todo.dto.TaskStatusUpdateRequest;
import com.example.todo.dto.TaskUpdateRequest;
import com.example.todo.entity.Category;
import com.example.todo.entity.Task;
import com.example.todo.entity.TaskPriority;
import com.example.todo.entity.TaskStatus;
import com.example.todo.exception.ResourceNotFoundException;
import com.example.todo.mapper.TaskMapper;
import com.example.todo.repository.IFCategoryRepository;
import com.example.todo.repository.IFTaskRepository;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;


@Service
public class TaskService {
    
    private final IFTaskRepository taskRepository;
    private final IFCategoryRepository categoryRepository;

    public TaskService(IFTaskRepository IFTaskRepository, IFCategoryRepository categoryRepository){
        this.taskRepository = IFTaskRepository;
        this.categoryRepository = categoryRepository;
    }

    //タスク一覧取得
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasks(){
        List<Task> tasks = taskRepository.findAllByOrderBySortOrderAsc();
        List<TaskResponse> responses = new ArrayList<>();

        for(Task task:tasks){
            TaskResponse response = TaskMapper.toTaskResponse(task);
            
            responses.add(response);
        }
    
        return responses;
    }

    //タスク1件の取得
    public TaskResponse getTask(Long id) {

    Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("タスクが見つかりません"));

    return TaskMapper.toTaskResponse(task);
    }

    //新規タスク作成
    @Transactional
    public TaskResponse createTask(TaskCreateRequest request){

        //DB上のsortOrderの不整合チェック
        normalizeSortOrder();

        Task task = TaskMapper.toEntity(request);

        task.setStatus(TaskStatus.TODO);
        //優先度が設定されているかチェック
        if (task.getPriority() == null) {
            task.setPriority(TaskPriority.MEDIUM);
        }

        if(request.getCategoryId() != null){
            Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("カテゴリが見つかりません"));
            task.setCategory(category);
        }

        //現在使用している表示順の最大値+1を設定
        int nextSortOrder = taskRepository.findTopByOrderBySortOrderDesc().map(Task::getSortOrder).orElse(0) + 1;
        task.setSortOrder(nextSortOrder);

        Task savedTask = taskRepository.save(task);

        return TaskMapper.toTaskResponse(savedTask);
    }

    //既存タスクのアップデート
    @Transactional
    public TaskResponse updateTask(Long id, TaskUpdateRequest request){
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("タスクが見つかりません"));

        TaskStatus beforeStatus = task.getStatus();
        TaskStatus afterStatus = request.getStatus();

        //すでに完了しているタスクでないかのチェック
        if(beforeStatus != TaskStatus.DONE && afterStatus == TaskStatus.DONE){
            task.setCompletedAt(LocalDateTime.now());
        }else if(beforeStatus == TaskStatus.DONE && afterStatus != TaskStatus.DONE){
            task.setCompletedAt(null);
        }

        TaskMapper.updateEntity(request,task);

        //カテゴリを更新
        if(request.getCategoryId() != null){
            Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("カテゴリが見つかりません"));
            task.setCategory(category);
        }else{
            task.setCategory(null);
        }

        Task savedTask = taskRepository.save(task);

        return TaskMapper.toTaskResponse(savedTask);
    }

    //詳細テキストの更新
    @Transactional
    public TaskResponse updateTaskDescription(Long id, TaskDescriptionUpdateRequest request){
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("タスクが見つかりません"));

        task.setDescription(request.getDescription());

        Task savedTask = taskRepository.save(task);

        return TaskMapper.toTaskResponse(savedTask);
    }

    //ステータス更新
    public TaskResponse updateTaskStatus(Long id, TaskStatusUpdateRequest request){
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("タスクが見つかりません"));
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

        //DB上のsortOrderの不整合チェック
        normalizeSortOrder();

        Task task = taskRepository.findById(id).orElseThrow(() ->new ResourceNotFoundException("タスクが見つかりません"));

        Integer deletedSortOrder = task.getSortOrder();
        taskRepository.delete(task);
        taskRepository.flush();

        List<Task> followingTasks = taskRepository.findBySortOrderGreaterThanOrderBySortOrderAsc(deletedSortOrder);
        
        for(Task followingTask : followingTasks){
            followingTask.setSortOrder(followingTask.getSortOrder() - 1);
            taskRepository.saveAndFlush(followingTask);
        }
    }

    //sortOrder並び替え
    @Transactional
    public void reorderTask(TaskReorderRequest request){
        normalizeSortOrder();

        if(request.getTaskId().equals(request.getTargetTaskId())){
            return;
        }

        Task movingTask = taskRepository.findById(request.getTaskId()).orElseThrow(() -> new ResourceNotFoundException("移動するタスクが見つかりません"));
        Task targetTask = taskRepository.findById(request.getTargetTaskId()).orElseThrow(() -> new ResourceNotFoundException("移動先のタスクが見つかりません"));

        Integer currentSortOrder = movingTask.getSortOrder();
        Integer targetSortOrder = targetTask.getSortOrder();
        Integer newSortOrder;

        if(currentSortOrder < targetSortOrder){
            if(request.getPosition() == ReorderPosition.BEFORE){
                newSortOrder = targetSortOrder - 1;
            }else{
                newSortOrder = targetSortOrder;
            }
        }else{
            if(request.getPosition() == ReorderPosition.BEFORE){
                newSortOrder = targetSortOrder;
            }else{
                newSortOrder = targetSortOrder + 1;
            }
        }

        if(currentSortOrder.equals(newSortOrder)){
            return;
        }

        movingTask.setSortOrder(0);
        taskRepository.saveAndFlush(movingTask);

        if(newSortOrder < currentSortOrder){
            List<Task> affectedTasks = taskRepository.findBySortOrderBetweenOrderBySortOrderDesc(newSortOrder, currentSortOrder - 1);

            for(Task task : affectedTasks){
                task.setSortOrder(task.getSortOrder() + 1);
                taskRepository.saveAndFlush(task);
            }
        }else{
            List<Task> affectedTasks = taskRepository.findBySortOrderBetweenOrderBySortOrderAsc(currentSortOrder + 1, newSortOrder);

            for(Task task : affectedTasks){
                task.setSortOrder(task.getSortOrder() - 1);
                taskRepository.saveAndFlush(task);
            }
        }

        movingTask.setSortOrder(newSortOrder);
        taskRepository.saveAndFlush(movingTask);
    }

    //連番チェック・修復
    private void normalizeSortOrder(){
        List<Task> tasks = taskRepository.findAllByOrderBySortOrderAsc();

        for(int i = 0; i < tasks.size(); i++){
            Task task = tasks.get(i);

            Integer expectedSortOrder = i + 1;

            if(!task.getSortOrder().equals(expectedSortOrder)){
                task.setSortOrder(expectedSortOrder);
                taskRepository.saveAndFlush(task);
            }
        }

    }
        


}
