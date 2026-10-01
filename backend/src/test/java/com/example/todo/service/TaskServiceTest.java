package com.example.todo.service;					
					
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;					
import org.springframework.beans.factory.annotation.Autowired;					
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.todo.dto.ReorderPosition;
import com.example.todo.dto.TaskCreateRequest;
import com.example.todo.dto.TaskDescriptionUpdateRequest;
import com.example.todo.dto.TaskReorderRequest;
import com.example.todo.dto.TaskResponse;
import com.example.todo.dto.TaskUpdateRequest;
import com.example.todo.entity.Task;
import com.example.todo.entity.TaskPriority;
import com.example.todo.entity.TaskStatus;
import com.example.todo.repository.IFTaskRepository;

import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;					
					
@SpringBootTest
@Transactional					
class TaskServiceTest {					
					
    @Autowired					
    private TaskService taskService;
    
    @Autowired
    private IFTaskRepository taskRepository;

    @Autowired
    private EntityManager entityManager;
					
    @Test					
    void setCompletedAtTest() {					
        // 準備：テスト対象のタスクを1件作る
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("買い物に行く");					
        TaskResponse task = taskService.createTask(request);					
					
        // 実行：ステータスをDONEに変更する
        TaskUpdateRequest updateRequest = new TaskUpdateRequest();
        updateRequest.setTitle(task.getTitle());
        updateRequest.setStatus(TaskStatus.DONE);
        updateRequest.setPriority(TaskPriority.MEDIUM);					
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

    //下にあるタスクを上方向へ並び替えできること
    @Test
    void sortOrderBeforeTest(){
        //準備：テストデータの作成
        int startSortOrder = taskRepository.findTopByOrderBySortOrderDesc().map(Task::getSortOrder).orElse(0) + 1;
        Task taskA = createTestTask("JUnit Test A", startSortOrder);
        Task taskB = createTestTask("JUnit Test B", startSortOrder + 1);
        Task taskC = createTestTask("JUnit Test C", startSortOrder + 2);
        Task taskD = createTestTask("JUnit Test D", startSortOrder + 3);

        TaskReorderRequest request = new TaskReorderRequest();
        request.setTaskId(taskD.getId());
        request.setTargetTaskId(taskB.getId());
        request.setPosition(ReorderPosition.BEFORE);

        //実行：テストデータを並べ替える
        taskService.reorderTask(request);
        entityManager.flush();
        entityManager.clear();

        //結果：期待した結果になっているか
        List<Task> reorderdTasks = 
            taskRepository
                .findAllByOrderBySortOrderAsc()
                .stream()
                .filter(
                    task -> task.getId().equals(taskA.getId())
                    || task.getId().equals(taskB.getId())
                    || task.getId().equals(taskC.getId())
                    || task.getId().equals(taskD.getId())
                )
                .toList();
        
                assertEquals(4, reorderdTasks.size());
                assertEquals(taskA.getId(), reorderdTasks.get(0).getId());
                assertEquals(taskD.getId(), reorderdTasks.get(1).getId());
                assertEquals(taskB.getId(), reorderdTasks.get(2).getId());
                assertEquals(taskC.getId(), reorderdTasks.get(3).getId());
    }

    //下にあるタスクを最上段へ並び替えできること
    @Test
    void sortOrderFirstPositionTest(){
        //準備：テストデータの作成
        int startSortOrder = taskRepository.findTopByOrderBySortOrderDesc().map(Task::getSortOrder).orElse(0) + 1;
        Task taskA = createTestTask("JUnit Test A", startSortOrder);
        Task taskB = createTestTask("JUnit Test B", startSortOrder + 1);
        Task taskC = createTestTask("JUnit Test C", startSortOrder + 2);
        Task taskD = createTestTask("JUnit Test D", startSortOrder + 3);

        TaskReorderRequest request = new TaskReorderRequest();
        request.setTaskId(taskD.getId());
        request.setTargetTaskId(taskA.getId());
        request.setPosition(ReorderPosition.BEFORE);

        //実行：テストデータを並べ替える
        taskService.reorderTask(request);
        entityManager.flush();
        entityManager.clear();

        //結果：期待した結果になっているか
        List<Task> reorderdTasks = 
            taskRepository
                .findAllByOrderBySortOrderAsc()
                .stream()
                .filter(
                    task -> task.getId().equals(taskA.getId())
                    || task.getId().equals(taskB.getId())
                    || task.getId().equals(taskC.getId())
                    || task.getId().equals(taskD.getId())
                )
                .toList();
        
                assertEquals(4, reorderdTasks.size());
                assertEquals(taskD.getId(), reorderdTasks.get(0).getId());
                assertEquals(taskA.getId(), reorderdTasks.get(1).getId());
                assertEquals(taskB.getId(), reorderdTasks.get(2).getId());
                assertEquals(taskC.getId(), reorderdTasks.get(3).getId());
    }

    //上にあるタスクを下方向へ並び替えできること
    @Test
    void sortOrderAfterTest(){
        //準備：テストデータの作成
        int startSortOrder = taskRepository.findTopByOrderBySortOrderDesc().map(Task::getSortOrder).orElse(0) + 1;
        Task taskA = createTestTask("JUnit Test A", startSortOrder);
        Task taskB = createTestTask("JUnit Test B", startSortOrder + 1);
        Task taskC = createTestTask("JUnit Test C", startSortOrder + 2);
        Task taskD = createTestTask("JUnit Test D", startSortOrder + 3);

        TaskReorderRequest request = new TaskReorderRequest();
        request.setTaskId(taskA.getId());
        request.setTargetTaskId(taskC.getId());
        request.setPosition(ReorderPosition.AFTER);

        //実行：テストデータを並べ替える
        taskService.reorderTask(request);
        entityManager.flush();
        entityManager.clear();

        //結果：期待した結果になっているか
        List<Task> reorderdTasks = 
            taskRepository
                .findAllByOrderBySortOrderAsc()
                .stream()
                .filter(
                    task -> task.getId().equals(taskA.getId())
                    || task.getId().equals(taskB.getId())
                    || task.getId().equals(taskC.getId())
                    || task.getId().equals(taskD.getId())
                )
                .toList();
        
                assertEquals(4, reorderdTasks.size());
                assertEquals(taskB.getId(), reorderdTasks.get(0).getId());
                assertEquals(taskC.getId(), reorderdTasks.get(1).getId());
                assertEquals(taskA.getId(), reorderdTasks.get(2).getId());
                assertEquals(taskD.getId(), reorderdTasks.get(3).getId());
    }

    //上にあるタスクを最下段へ並び替えできること
    @Test
    void sortOrderLastPositionTest(){
        //準備：テストデータの作成
        int startSortOrder = taskRepository.findTopByOrderBySortOrderDesc().map(Task::getSortOrder).orElse(0) + 1;
        Task taskA = createTestTask("JUnit Test A", startSortOrder);
        Task taskB = createTestTask("JUnit Test B", startSortOrder + 1);
        Task taskC = createTestTask("JUnit Test C", startSortOrder + 2);
        Task taskD = createTestTask("JUnit Test D", startSortOrder + 3);

        TaskReorderRequest request = new TaskReorderRequest();
        request.setTaskId(taskA.getId());
        request.setTargetTaskId(taskD.getId());
        request.setPosition(ReorderPosition.AFTER);

        //実行：テストデータを並べ替える
        taskService.reorderTask(request);
        entityManager.flush();
        entityManager.clear();

        //結果：期待した結果になっているか
        List<Task> reorderdTasks = 
            taskRepository
                .findAllByOrderBySortOrderAsc()
                .stream()
                .filter(
                    task -> task.getId().equals(taskA.getId())
                    || task.getId().equals(taskB.getId())
                    || task.getId().equals(taskC.getId())
                    || task.getId().equals(taskD.getId())
                )
                .toList();
        
                assertEquals(4, reorderdTasks.size());
                assertEquals(taskB.getId(), reorderdTasks.get(0).getId());
                assertEquals(taskC.getId(), reorderdTasks.get(1).getId());
                assertEquals(taskD.getId(), reorderdTasks.get(2).getId());
                assertEquals(taskA.getId(), reorderdTasks.get(3).getId());
    }

    //タスク詳細を保存できること
    @Test
    void saveDescription(){
        //準備：テストデータの作成
        int sortOrder = taskRepository.findTopByOrderBySortOrderDesc().map(Task::getSortOrder).orElse(0) + 1;
        Task testTask = createTestTask("Junit Description Test", sortOrder);
        String description = "1行目：JUnitテスト😀\n" + "2行目：改行テスト";

        TaskDescriptionUpdateRequest request = new TaskDescriptionUpdateRequest();

        request.setDescription(description);

        //実行：テストデータの登録
        taskService.updateTaskDescription(testTask.getId(), request);
        entityManager.flush();
        entityManager.clear();

        //確認：期待した結果になっているか
        Task updatedTask = taskRepository.findById(testTask.getId()).orElseThrow();
        assertEquals(description, updatedTask.getDescription());
    }

    //テストデータ作成用メソッド
    private Task createTestTask(String title, int sortOrder){
            Task task = new Task();
            task.setTitle(title);
            task.setStatus(TaskStatus.TODO);
            task.setPriority(TaskPriority.MEDIUM);
            task.setSortOrder(sortOrder);

            return taskRepository.saveAndFlush(task);
    }

}					