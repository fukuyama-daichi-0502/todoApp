package com.example.todo.service;

import com.example.todo.dto.ReorderPosition;
import com.example.todo.dto.SubtaskCreateRequest;
import com.example.todo.dto.TaskCreateRequest;
import com.example.todo.dto.TaskDescriptionUpdateRequest;
import com.example.todo.dto.TaskReorderRequest;
import com.example.todo.dto.TaskResponse;
import com.example.todo.dto.TaskSearchCondition;
import com.example.todo.dto.TaskStatusUpdateRequest;
import com.example.todo.dto.TaskUpdateRequest;
import com.example.todo.entity.Category;
import com.example.todo.entity.RecurrenceRule;
import com.example.todo.entity.Task;
import com.example.todo.entity.TaskStatus;
import com.example.todo.exception.InvalidRequestException;
import com.example.todo.exception.ResourceNotFoundException;
import com.example.todo.mapper.TaskMapper;
import com.example.todo.repository.IFCategoryRepository;
import com.example.todo.repository.IFTaskRepository;
import com.example.todo.search.TaskSearchFilters;
import com.example.todo.calculation.RecurrenceDateCalculator;
import com.example.todo.calculation.TaskProgressCalculator;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;


@Service
public class TaskService {
    
    private final IFTaskRepository taskRepository;
    private final IFCategoryRepository categoryRepository;
    private final RecurrenceDateCalculator recurrenceDateCalculator = new RecurrenceDateCalculator();
    private final TaskProgressCalculator taskProgressCalculator = new TaskProgressCalculator();

    public TaskService(IFTaskRepository IFTaskRepository, IFCategoryRepository categoryRepository){
        this.taskRepository = IFTaskRepository;
        this.categoryRepository = categoryRepository;
    }

    //タスク一覧取得　フィルタリング・検索機能
    public List<TaskResponse> searchTasks(TaskSearchCondition condition){
        Specification<Task> specification = 
            (root, query, criteriaBuilder) ->
                criteriaBuilder.conjunction();

        if(condition.getTitle() !=null && !condition.getTitle().isBlank()){
            specification = specification.and(
                TaskSearchFilters.textContains(condition.getTitle())    
            );
        }

        if(condition.getStatus() !=null){
            specification = specification.and(TaskSearchFilters.hasStatus(condition.getStatus()));
        }

        if(condition.getDueDate() != null){
            LocalDate today = LocalDate.now();

            switch(condition.getDueDate()){
                case TODAY -> 
                    specification = specification.and(TaskSearchFilters.dueToday(today)); 
                
                case OVERDUE ->
                    specification = specification.and(TaskSearchFilters.overdue(today)); 
            }
        }

        if(condition.getPriority() != null){
            specification = specification.and(TaskSearchFilters.hasPriority(condition.getPriority()));
        }

        if(condition.getCategory() != null && !condition.getCategory().isBlank()){
            if("NONE".equalsIgnoreCase(condition.getCategory())){
                specification = specification.and(TaskSearchFilters.hasNoCategory());
            }else{
                Long categoryId = Long.valueOf(condition.getCategory());
                specification = specification.and(TaskSearchFilters.hasCategory(categoryId));
            }
        }

        List<Task> matchedTasks = taskRepository.findAll(specification, Sort.by(Sort.Direction.ASC,"sortOrder"));

        //画面へ表示するタスク
        Set<Long>visibleTaskIds = new HashSet<>();

        //全タスク取得
        List<Task>allTasks = taskRepository.findAllByOrderBySortOrderAsc();

        for(Task matchedTask : matchedTasks){
            //親タスクが条件に一致した場合
            if(matchedTask.getParentTask() == null){
                visibleTaskIds.add(matchedTask.getId());

                //一致した親のサブタスクをすべて表示対象にする
                for(Task task : allTasks){
                    if(task.getParentTask() != null && task.getParentTask().getId().equals(matchedTask.getId())){
                        visibleTaskIds.add(task.getId());
                    }
                }
            }else{
                //サブタスクが検索条件に一致した場合
                visibleTaskIds.add(matchedTask.getId());

                //条件に一致したサブタスクの親タスクも表示対象にする
                visibleTaskIds.add(matchedTask.getParentTask().getId());
            }
        }

        //全タスクから表示対象だけをsortOrder順のまま取り出す
        List<Task> resultTasks = new ArrayList<>();

        for(Task task : allTasks){
            if(visibleTaskIds.contains(task.getId())){
                resultTasks.add(task);
            }
        }

        return resultTasks.stream().map(this::toTaskResponseWithProgress).toList();
    }

    //タスク1件の取得
    public TaskResponse getTask(Long id) {

    Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("タスクが見つかりません"));

    return toTaskResponseWithProgress(task);
    }

    //サブタスクの取得
    @Transactional(readOnly = true)
    public List<TaskResponse> getSubtasks(Long parentId){
        Task parentTask = taskRepository.findById(parentId).orElseThrow(() -> new ResourceNotFoundException("親タスクが見つかりません"));
        
        if(parentTask.getParentTask() != null){
            throw new InvalidRequestException("サブタスクを親として指定することはできません");
        }

        return taskRepository
            .findByParentTask_IdOrderBySortOrderAsc(parentId)
            .stream()
            .map(TaskMapper :: toTaskResponse)
            .toList();
    }

    //新規タスク作成
    @Transactional
    public TaskResponse createTask(TaskCreateRequest request){

        //DB上のsortOrderの不整合チェック
        normalizeSortOrder();

        Task task = TaskMapper.toEntity(request);

        task.setStatus(TaskStatus.TODO);

        if(request.getCategoryId() != null){
            Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("カテゴリが見つかりません"));
            task.setCategory(category);
        }

        //現在使用している表示順の最大値+1を設定
        int nextSortOrder = taskRepository.findTopByOrderBySortOrderDesc().map(Task::getSortOrder).orElse(0) + 1;
        task.setSortOrder(nextSortOrder);

        //繰り返し設定の整合性確認
        RecurrenceRule recurrenceRule = normalizeRecurrenceRule(request.getRecurrenceRule());
        validateRecurrence(request.getDueDate(), recurrenceRule, request.getRecurrenceBaseDay());
        task.setRecurrenceRule(recurrenceRule);
        task.setRecurrenceBaseDay(normalizeRecurrenceBaseDay(recurrenceRule, request.getRecurrenceBaseDay()));

        Task savedTask = taskRepository.save(task);

        return toTaskResponseWithProgress(savedTask);
    }

    //サブタスク作成
    @Transactional
    public TaskResponse createSubtask(Long parentId, SubtaskCreateRequest request){
        Task parentTask = taskRepository
            .findById(parentId)
            .orElseThrow(
                () -> new ResourceNotFoundException("親タスクが見つかりません")
            );
        
        if(parentTask.getParentTask() != null){
            throw new InvalidRequestException("サブタスクにサブタスクを追加することはできません");
        }

        //親タスクに紐づいているタスクの中で大きいソートオーダーを取得
        Integer groupEndSortOrder = 
            taskRepository
                .findTopByParentTask_IdOrderBySortOrderDesc(parentId)
                .map(Task::getSortOrder)
                .orElse(parentTask.getSortOrder());
        
        List<Task> followingTasks = taskRepository.findBySortOrderGreaterThanOrderBySortOrderDesc(groupEndSortOrder);

        //親タスクに紐づいているタスクの中で大きいソートオーダー以上のソートオーダーに+1する
        for(Task followingTask : followingTasks){
            followingTask.setSortOrder(followingTask.getSortOrder() + 1);
            taskRepository.saveAndFlush(followingTask);
        }

        Task subtask = new Task();

        subtask.setTitle(request.getTitle());
        subtask.setStatus(TaskStatus.TODO);
        subtask.setDueDate(request.getDueDate());
        subtask.setPriority(request.getPriority());
        subtask.setDescription(request.getDescription());
        subtask.setParentTask(parentTask);
        subtask.setSortOrder(groupEndSortOrder + 1);
        subtask.setRecurrenceRule(RecurrenceRule.NONE);
        subtask.setRecurrenceBaseDay(null);
        subtask.setNextTask(null);

        if(request.getCategoryId() != null){
            Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("カテゴリが見つかりません"));
            subtask.setCategory(category);
        }

        Task savedSubtask = taskRepository.save(subtask);

        return TaskMapper.toTaskResponse(savedSubtask);
    }

    //既存タスクのアップデート
    @Transactional
    public TaskResponse updateTask(Long id, TaskUpdateRequest request){
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("タスクが見つかりません"));

        TaskStatus beforeStatus = task.getStatus();
        TaskStatus afterStatus = request.getStatus();

        boolean changedToDone = beforeStatus != TaskStatus.DONE && afterStatus == TaskStatus.DONE;

        //すでに完了しているタスクでないかのチェック
        if(changedToDone){
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

        //繰り返し設定の整合性確認
        RecurrenceRule recurrenceRule = normalizeRecurrenceRule(request.getRecurrenceRule());
        validateRecurrence(request.getDueDate(), recurrenceRule, request.getRecurrenceBaseDay());
        task.setRecurrenceRule(recurrenceRule);
        task.setRecurrenceBaseDay(normalizeRecurrenceBaseDay(recurrenceRule, request.getRecurrenceBaseDay()));

        //親タスクがDONEになったときサブタスクもDONEにする
        if(task.getParentTask() == null && changedToDone){
            completeSubtasks(task);
            generateNextTaskIfNeeded(task);
        }

        Task savedTask = taskRepository.save(task);

        return toTaskResponseWithProgress(savedTask);
    }

    //詳細テキストの更新
    @Transactional
    public TaskResponse updateTaskDescription(Long id, TaskDescriptionUpdateRequest request){
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("タスクが見つかりません"));

        task.setDescription(request.getDescription());

        Task savedTask = taskRepository.save(task);

        return toTaskResponseWithProgress(savedTask);
    }

    //ステータス更新
    @Transactional
    public TaskResponse updateTaskStatus(Long id, TaskStatusUpdateRequest request){
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("タスクが見つかりません"));
        TaskStatus beforeStatus = task.getStatus();
        TaskStatus afterStatus = request.getStatus();

        if(beforeStatus != TaskStatus.DONE && afterStatus == TaskStatus.DONE){
            task.setCompletedAt(LocalDateTime.now());
        }else if(beforeStatus == TaskStatus.DONE && afterStatus != TaskStatus.DONE){
            task.setCompletedAt(null);
        }

        
        //親タスクに紐づくサブタスクの処理
        boolean changedToDone = beforeStatus != TaskStatus.DONE && afterStatus == TaskStatus.DONE;

        if(task.getParentTask() == null && changedToDone){
            completeSubtasks(task);
            generateNextTaskIfNeeded(task);
        }

        task.setStatus(afterStatus);
        Task savedTask = taskRepository.save(task);
        return toTaskResponseWithProgress(savedTask);

    }

    //タスク削除
    public void deleteTask(Long id) {

        //DB上のsortOrderの不整合チェック
        normalizeSortOrder();

        Task task = taskRepository.findById(id).orElseThrow(() ->new ResourceNotFoundException("タスクが見つかりません"));

        //削除対象が親タスクの時
        if(task.getParentTask() == null){
            List<Task>subtasks = taskRepository.findByParentTask_IdOrderBySortOrderAsc(task.getId());

            //親タスクに紐づくサブタスクの削除
            taskRepository.deleteAll(subtasks);
            taskRepository.flush();
        }

        // Integer deletedSortOrder = task.getSortOrder();
        taskRepository.delete(task);
        taskRepository.flush();

        //サブタスクが一緒に削除されたときにソートオーダーを詰める
        normalizeSortOrder();
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

        boolean movingTaskIsParent = movingTask.getParentTask() == null;
        boolean targetTaskIsParent = targetTask.getParentTask() == null;

        //既存の全タスクをsortOrder順に取得
        List<Task> allTasks = taskRepository.findAllByOrderBySortOrderAsc();

        //親タスクだけ取り出す
        List<Task> parentTasks = new ArrayList<>();

        for(Task task : allTasks){
            if(task.getParentTask() == null){
                parentTasks.add(task);
            }
        }

        //サブタスクを並べる時に使用
        Long reorderedSubtaskParentId = null;
        List<Task> reorderedSubtasks = null;

        //親同士の時
        if(movingTaskIsParent && targetTaskIsParent){
            parentTasks.removeIf(task -> task.getId().equals(movingTask.getId()));
            int targetIndex = -1;

            for(int i = 0; i < parentTasks.size(); i++){
                Task parentTask = parentTasks.get(i);

                if(parentTask.getId().equals(targetTask.getId())){
                    targetIndex = i;

                    break;
                }
            }

            if (targetIndex == -1){
                throw new InvalidRequestException("並べ替え先の親タスクが見つかりません");
            }

            if(request.getPosition() == ReorderPosition.BEFORE){
                parentTasks.add(targetIndex, movingTask);
            }else if(request.getPosition() == ReorderPosition.AFTER){
                parentTasks.add(targetIndex + 1, movingTask);
            }else{
                throw new InvalidRequestException("並べ替え位置が不正です");
            }
        }else if(!movingTaskIsParent && !targetTaskIsParent){
            Long movingParentId = movingTask.getParentTask().getId();
            Long targetParentId = targetTask.getParentTask().getId();

            //同じ親のサブタスクか
            if(!movingParentId.equals(targetParentId)){
                throw new InvalidRequestException("サブタスクを別の親タスクへ移動することはできません");
            }

            reorderedSubtaskParentId = movingParentId;
            reorderedSubtasks = taskRepository.findByParentTask_IdOrderBySortOrderAsc(movingParentId);

            //移動するサブタスクを一覧から外す
            reorderedSubtasks.removeIf(task -> task.getId().equals(movingTask.getId()));

            int targetIndex = -1;

            for(int i = 0; i < reorderedSubtasks.size(); i++){
                Task subtask = reorderedSubtasks.get(i);
                if(subtask.getId().equals(targetTask.getId())){
                    targetIndex = i;
                    break;
                }
            } 

            if(targetIndex == -1){
                throw new InvalidRequestException("並べ替え先のサブタスクが見つかりません");
            }

            if(request.getPosition() == ReorderPosition.BEFORE){
                reorderedSubtasks.add(targetIndex, movingTask);
            }else if(request.getPosition() == ReorderPosition.AFTER){
                reorderedSubtasks.add(targetIndex + 1, movingTask);
            }else{
                throw new InvalidRequestException("並べ替え位置が不正です");
            }

        }

        //最終的な並び順の作成
        List<Task> reorderedTasks = new ArrayList<>();

        for(Task parentTask : parentTasks){
            reorderedTasks.add(parentTask);
            //今回サブタスクを並べ替えた親の場合
            if(reorderedSubtaskParentId != null && parentTask.getId().equals(reorderedSubtaskParentId)){
                reorderedTasks.addAll(reorderedSubtasks);
            }else{
                //それ以外の親の場合
                List<Task> subtasks = taskRepository.findByParentTask_IdOrderBySortOrderAsc(parentTask.getId());
                reorderedTasks.addAll(subtasks);
            }
        }

        //sortOrderのユニーク制約対策
        int maxSortOrder = 0;

        for(Task task : allTasks){
            if(task.getSortOrder() > maxSortOrder){
                maxSortOrder = task.getSortOrder();
            }
        }

        int temporarySortOrder = maxSortOrder + 1;

        for(Task task : reorderedTasks){
            task.setSortOrder(temporarySortOrder);

            taskRepository.saveAndFlush(task);
            temporarySortOrder++;
        }
        taskRepository.saveAllAndFlush(reorderedTasks);

        normalizeSortOrder();
        taskRepository.flush();
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

    //繰り返し設定判定
    private RecurrenceRule normalizeRecurrenceRule(RecurrenceRule recurrenceRule){
        return recurrenceRule == null ? RecurrenceRule.NONE : recurrenceRule;
    }

    //繰り返し設定の整合性チェック
    private void validateRecurrence(LocalDate dueDate, RecurrenceRule recurrenceRule, Long recurrenceBaseDay){
        if(recurrenceRule == RecurrenceRule.NONE){
            return;
        }

        if(dueDate == null){
            throw new InvalidRequestException("繰り返し設定には期限が必要です");
        }

        if(recurrenceRule == RecurrenceRule.MONTHLY && recurrenceBaseDay == null){
            throw new InvalidRequestException("毎月の繰り返しには基準日が必要です");
        }
    }

    //基準日チェック
    private Long normalizeRecurrenceBaseDay(RecurrenceRule recurrenceRule, Long recurrenceBaseDay){
        if(recurrenceRule == RecurrenceRule.MONTHLY){
            return recurrenceBaseDay;
        }

        return null;
    }

    //サブタスクのステータスを完了にする
    private void completeSubtasks(Task parentTask){
        List<Task> subtasks = taskRepository.findByParentTask_IdOrderBySortOrderAsc(parentTask.getId());

        LocalDateTime completedAt = LocalDateTime.now();

        for(Task subtask : subtasks){

            if(subtask.getStatus() == TaskStatus.DONE){
                continue;
            }

            subtask.setStatus(TaskStatus.DONE);
            subtask.setCompletedAt(completedAt);
        }
    }

    //繰り返しタスク生成
    private void generateNextTaskIfNeeded(Task task){
        //対象タスクがサブタスクの場合繰り返しタスクは生成しない
        if(task.getParentTask() != null){
            return;
        }

        RecurrenceRule recurrenceRule = normalizeRecurrenceRule(task.getRecurrenceRule());
        
        //繰り返し設定なしの場合
        if(recurrenceRule == RecurrenceRule.NONE){
            return;
        }

        //繰り返しタスクを生成しているタスクの場合重複生成しない
        if(task.getNextTask() != null){
            return;
        }

        LocalDate nextDueDate = recurrenceDateCalculator.calculateNextDueDate(
            task.getDueDate(),
            recurrenceRule,
            task.getRecurrenceBaseDay()
        );

        //DB上のsortOrderの不整合チェック
        normalizeSortOrder();

        //現在使用している表示順の最大値+1を設定
        int nextSortOrder = taskRepository.findTopByOrderBySortOrderDesc().map(Task::getSortOrder).orElse(0) + 1;

        Task nextTask = new Task();

        nextTask.setTitle(task.getTitle());
        nextTask.setStatus(TaskStatus.TODO);
        nextTask.setDueDate(nextDueDate);
        nextTask.setPriority(task.getPriority());
        nextTask.setDescription(task.getDescription());
        nextTask.setCategory(task.getCategory());
        nextTask.setParentTask(null);
        nextTask.setSortOrder(nextSortOrder);

        //繰り返し設定を次回タスクに引き継ぐ
        nextTask.setRecurrenceRule(recurrenceRule);
        nextTask.setRecurrenceBaseDay(task.getRecurrenceBaseDay());

        nextTask.setNextTask(null);

        Task savedNextTask = taskRepository.save(nextTask);

        //元の親タスクのサブタスクを次回タスクに複製
        copySubtasks(task, savedNextTask, nextSortOrder);

        //元タスクに自動生成したタスクを紐づける
        task.setNextTask(savedNextTask);

    }

    //繰り返しサブタスクの作成
    private void copySubtasks(Task originalParentTask, Task nextParentTask, int parentSortOrder){
        List<Task> originalSubtasks = taskRepository.findByParentTask_IdOrderBySortOrderAsc(originalParentTask.getId());

        int nextSortOrder = parentSortOrder + 1;

        for(Task originalSubtask : originalSubtasks){
            Task nextSubtask = new Task();

            nextSubtask.setTitle(originalSubtask.getTitle());
            nextSubtask.setStatus(TaskStatus.TODO);
            nextSubtask.setDueDate(recurrenceDateCalculator.calculateNextSubtaskDueDate(originalSubtask.getDueDate(), originalParentTask.getDueDate(), nextParentTask.getDueDate()));
            nextSubtask.setPriority(originalSubtask.getPriority());
            nextSubtask.setDescription(originalSubtask.getDescription());
            nextSubtask.setCategory(originalSubtask.getCategory());
            nextSubtask.setParentTask(nextParentTask);
            nextSubtask.setSortOrder(nextSortOrder);
            nextSubtask.setRecurrenceRule(RecurrenceRule.NONE);
            nextSubtask.setRecurrenceBaseDay(null);
            nextSubtask.setNextTask(null);

            taskRepository.save(nextSubtask);

            nextSortOrder++;
        }
    }

    //タスク進捗の計算
    private TaskResponse toTaskResponseWithProgress(Task task){
        TaskResponse response = TaskMapper.toTaskResponse(task);

        //サブタスクには進捗率を設定しない
        if(task.getParentTask() != null){
            return response;
        }

        List<Task> subtasks = taskRepository.findByParentTask_IdOrderBySortOrderAsc(task.getId());

        int progress = taskProgressCalculator.calculateProgress(task, subtasks);

        response.setProgress(progress);

        return response;
    }

}
