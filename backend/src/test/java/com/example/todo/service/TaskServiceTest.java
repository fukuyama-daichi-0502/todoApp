package com.example.todo.service;					
					
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;					
import org.springframework.beans.factory.annotation.Autowired;					
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.todo.dto.TaskCreateRequest;
import com.example.todo.dto.TaskResponse;
import com.example.todo.dto.TaskUpdateRequest;
import com.example.todo.entity.TaskStatus;
import com.example.todo.repository.IFTaskRepository;

import static org.assertj.core.api.Assertions.assertThat;					
					
@SpringBootTest
@Transactional					
class TaskServiceTest {					
					
    @Autowired					
    private TaskService taskService;
    
    @Autowired
    private IFTaskRepository taskRepository;
					
    @Test					
    void ステータスをDONEに変更するとcompletedAtが設定される() {					
        // 準備：テスト対象のタスクを1件作る
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("買い物に行く");					
        TaskResponse task = taskService.createTask(request);					
					
        // 実行：ステータスをDONEに変更する
        TaskUpdateRequest updateRequest = new TaskUpdateRequest();
        updateRequest.setTitle(task.getTitle());
        updateRequest.setStatus(TaskStatus.DONE);					
        TaskResponse updated = taskService.updateTask(task.getId(), updateRequest);
        
        //実行2：ステータスをDONEからCANCELLEDに変更
        updateRequest.setStatus(TaskStatus.CANCELLED);
        TaskResponse changeUpdated = taskService.updateTask(task.getId(), updateRequest);				
					
        // 確認：期待した結果になっているか					
        assertThat(updated.getStatus()).isEqualTo(TaskStatus.DONE);					
        assertThat(updated.getCompletedAt()).isNotNull();

        assertThat(changeUpdated.getStatus()).isEqualTo(TaskStatus.CANCELLED);					
        assertThat(changeUpdated.getCompletedAt()).isNull();       
    }
    
    //削除操作を行うと対象タスクがデータベースから削除されること
    @Test 
    void taskDeleteTest(){
        //準備：削除対象のデータを1件作成する
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("データベースの削除");					
        TaskResponse task = taskService.createTask(request);
        Long taskId = task.getId();

        //削除前に対象データが存在していることを確認
        assertTrue(taskRepository.existsById(taskId));

        //実行：対象データを削除する
        taskService.deleteTask(taskId);
        //DELETEを反映
        taskRepository.flush();

        //確認：期待した結果になっているか
        assertFalse(taskRepository.existsById(taskId));
    }

}					