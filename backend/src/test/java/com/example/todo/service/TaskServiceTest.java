package com.example.todo.service;					
					
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;					
import org.springframework.beans.factory.annotation.Autowired;					
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.todo.dto.DueDateFilter;
import com.example.todo.dto.ReorderPosition;
import com.example.todo.dto.TaskCreateRequest;
import com.example.todo.dto.TaskDescriptionUpdateRequest;
import com.example.todo.dto.TaskReorderRequest;
import com.example.todo.dto.TaskResponse;
import com.example.todo.dto.TaskSearchCondition;
import com.example.todo.dto.TaskUpdateRequest;
import com.example.todo.entity.Category;
import com.example.todo.entity.RecurrenceRule;
import com.example.todo.entity.Task;
import com.example.todo.entity.TaskPriority;
import com.example.todo.entity.TaskStatus;
import com.example.todo.repository.IFCategoryRepository;
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
    private IFCategoryRepository categoryRepository;

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

    //フィルタリング・検索機能が正しく動作すること
    @Test
    void shouldSearchTasksByConditions(){
        LocalDate today = LocalDate.now();

        int sortOrder = taskRepository.findTopByOrderBySortOrderDesc().map(Task::getSortOrder).orElse(0) + 1;

        //準備：テスト対象のタスクを作成
        Category testCategoryA = new Category();
        testCategoryA.setName("JUnitテストカテゴリA");
        testCategoryA.setColor("#000000");
        Category savedTestCategoryA = categoryRepository.saveAndFlush(testCategoryA);

        Category testCategoryB = new Category();
        testCategoryB.setName("JUnitテストカテゴリB");
        testCategoryB.setColor("#fa4343");
        //カテゴリBは比較時に使用しないためsavedTestCategoryBとして変数を作成しない
        categoryRepository.saveAndFlush(testCategoryB);

        Task targetTask = createTestTask("JUnit Search Java Target", sortOrder + 1);
        targetTask.setStatus(TaskStatus.TODO);
        targetTask.setPriority(TaskPriority.HIGH);
        targetTask.setDueDate(today);
        targetTask.setCategory(testCategoryA);

        taskRepository.saveAndFlush(targetTask);

        Task differentStatusTask = createTestTask("JUnit Search Java Different Status", sortOrder + 2);
        differentStatusTask.setStatus(TaskStatus.IN_PROGRESS);
        differentStatusTask.setPriority(TaskPriority.HIGH);
        differentStatusTask.setDueDate(today);
        differentStatusTask.setCategory(testCategoryA);

        taskRepository.saveAndFlush(differentStatusTask);

        Task overdueTask = createTestTask("JUnit Search Overdue", sortOrder + 3);
        overdueTask.setStatus(TaskStatus.TODO);
        overdueTask.setPriority(TaskPriority.LOW);
        overdueTask.setDueDate(today.minusDays(1));
        overdueTask.setCategory(testCategoryB);
        overdueTask.setDescription("JUnit Description keyword");

        taskRepository.saveAndFlush(overdueTask);

        Task completedOverdueTask = createTestTask("JUnit Search Completed Overdue", sortOrder + 4);
        completedOverdueTask.setStatus(TaskStatus.DONE);
        completedOverdueTask.setPriority(TaskPriority.LOW);
        completedOverdueTask.setDueDate(today.minusDays(1));
        completedOverdueTask.setCategory(testCategoryB);

        taskRepository.saveAndFlush(completedOverdueTask);

        Task noCategoryTask = createTestTask("JUnit Search No Category", sortOrder + 5);
        noCategoryTask.setStatus(TaskStatus.TODO);
        noCategoryTask.setPriority(TaskPriority.MEDIUM);
        noCategoryTask.setDueDate(today.plusDays(1));
        noCategoryTask.setCategory(null);

        taskRepository.saveAndFlush(noCategoryTask);

        Task noPriorityTask = createTestTask("JUnit No Priority", sortOrder + 6);
        
        noPriorityTask.setStatus(TaskStatus.TODO);
        noPriorityTask.setPriority(null);
        noPriorityTask.setDueDate(today);
        noPriorityTask.setCategory(testCategoryA);
        
        taskRepository.saveAndFlush(noPriorityTask);

        //確認：期待した結果になっているか
        assertAll(
            "Task search conditions",
            //タイトル部分一致
            () -> {
                TaskSearchCondition condition = new TaskSearchCondition();

                condition.setTitle("Java Target");

                List<TaskResponse> result = taskService.searchTasks(condition);

                assertTrue(result.stream().anyMatch(task -> task.getId().equals(targetTask.getId())));
                assertFalse(result.stream().anyMatch(task -> task.getId().equals(overdueTask.getId())));
            },
            //詳細に一致する場合
            () -> {
                TaskSearchCondition condition = new TaskSearchCondition();

                condition.setTitle("Description Keyword");

                List<TaskResponse> result = taskService.searchTasks(condition);

                assertTrue(result.stream().anyMatch(task -> task.getId().equals(overdueTask.getId())));
                assertFalse(result.stream().anyMatch(task -> task.getId().equals(targetTask.getId())));
            },
            //ステータス
            () -> {
                TaskSearchCondition condition = new TaskSearchCondition();
                condition.setStatus(TaskStatus.TODO);

                List<TaskResponse> result = taskService.searchTasks(condition);
                assertTrue(result.stream().anyMatch(task -> task.getId().equals(targetTask.getId())));
                assertFalse(result.stream().anyMatch(task -> task.getId().equals(differentStatusTask.getId())));
            },
            //TODAY
            () -> {
                TaskSearchCondition condition = new TaskSearchCondition();
                condition.setDueDate(DueDateFilter.TODAY);

                List<TaskResponse> result = taskService.searchTasks(condition);
                assertTrue(result.stream().anyMatch(task -> task.getId().equals(targetTask.getId())));
                assertFalse(result.stream().anyMatch(task -> task.getId().equals(overdueTask.getId())));
            },
            //OVERDUE
            () -> {
                TaskSearchCondition condition = new TaskSearchCondition();
                condition.setDueDate(DueDateFilter.OVERDUE);

                List<TaskResponse> result = taskService.searchTasks(condition);
                assertTrue(result.stream().anyMatch(task -> task.getId().equals(overdueTask.getId())));
                assertFalse(result.stream().anyMatch(task -> task.getId().equals(completedOverdueTask.getId())));
            },
            //優先度
            () -> {
                TaskSearchCondition condition = new TaskSearchCondition();
                condition.setPriority(TaskPriority.HIGH);

                List<TaskResponse> result = taskService.searchTasks(condition);
                assertTrue(result.stream().anyMatch(task -> task.getId().equals(targetTask.getId())));
                assertFalse(result.stream().anyMatch(task -> task.getId().equals(overdueTask.getId())));
            },
            //優先度なし
            () -> {
                TaskSearchCondition condition = new TaskSearchCondition();
                condition.setPriority(TaskPriority.MEDIUM);

                List<TaskResponse> result = taskService.searchTasks(condition);
                assertTrue(result.stream().anyMatch(task -> task.getId().equals(noPriorityTask.getId())));
                assertFalse(result.stream().anyMatch(task -> task.getId().equals(overdueTask.getId())));
            },
            //カテゴリID
            () -> {
                TaskSearchCondition condition = new TaskSearchCondition();
                condition.setCategory(String.valueOf(savedTestCategoryA.getId()));

                List<TaskResponse> result = taskService.searchTasks(condition);
                assertTrue(result.stream().anyMatch(task -> task.getId().equals(targetTask.getId())));
                assertFalse(result.stream().anyMatch(task -> task.getId().equals(overdueTask.getId())));
            },
            //カテゴリなし
            () -> {
                TaskSearchCondition condition = new TaskSearchCondition();
                condition.setCategory("NONE");

                List<TaskResponse> result = taskService.searchTasks(condition);
                assertTrue(result.stream().anyMatch(task -> task.getId().equals(noCategoryTask.getId())));
                assertFalse(result.stream().anyMatch(task -> task.getId().equals(targetTask.getId())));
            },
            //複数条件
            () -> {
                TaskSearchCondition condition = new TaskSearchCondition();
                condition.setTitle("JUnit Search Java");
                condition.setStatus(TaskStatus.TODO);
                condition.setDueDate(DueDateFilter.TODAY);
                condition.setPriority(TaskPriority.HIGH);
                condition.setCategory(String.valueOf(savedTestCategoryA.getId()));

                List<TaskResponse> result = taskService.searchTasks(condition);
                assertTrue(result.stream().anyMatch(task -> task.getId().equals(targetTask.getId())));
                assertFalse(result.stream().anyMatch(task -> task.getId().equals(differentStatusTask.getId())));
            }
        );

    }

    //親タスク削除時にサブタスクも削除される
    @Test 
    void deleteTaskDeletesSubtasksWhenParentTaskIsDeleted(){
        //準備：テストデータの作成
        int startSortOrder = taskRepository.findTopByOrderBySortOrderDesc().map(task -> task.getSortOrder() + 1).orElse(1);
        Task parentTask = createTestTask("親タスク", startSortOrder);
        Task subtask1 = createTestSubtask("サブタスク1", startSortOrder + 1, parentTask);
        Task subtask2 = createTestSubtask("サブタスク2", startSortOrder + 2, parentTask);

        Long parentTaskId = parentTask.getId();
        Long subtask1Id = subtask1.getId();
        Long subtask2Id = subtask2.getId();

        //実行前確認：DBにそれぞれのタスクが保存されていることの確認
        assertTrue(taskRepository.existsById(parentTaskId));
        assertTrue(taskRepository.existsById(subtask1Id));
        assertTrue(taskRepository.existsById(subtask2Id));

        //実行：親タスクの削除
        taskService.deleteTask(parentTaskId);
        taskRepository.flush();

        //確認：期待した結果になっているか
        assertFalse(taskRepository.existsById(parentTaskId));
        assertFalse(taskRepository.existsById(subtask1Id));
        assertFalse(taskRepository.existsById(subtask2Id));
    }

    //繰り返しタスクの元タスクの削除時にすでに生成済みの次回タスクが残る
    @Test 
    void deleteTaskDoesNotDeleteGeneratedNextTask(){
        //準備：テストデータの作成
        int startSortOrder = taskRepository.findTopByOrderBySortOrderDesc()
            .map(task -> task.getSortOrder() + 1)
            .orElse(1);
        
        Task originalTask = createTestTask("繰り返し元タスク", startSortOrder);
        Task nextTask = createTestTask("生成済み次回タスク", startSortOrder + 1);

        originalTask.setRecurrenceRule(RecurrenceRule.DAILY);
        nextTask.setRecurrenceRule(RecurrenceRule.DAILY);

        originalTask.setNextTask(nextTask);

        originalTask = taskRepository.saveAndFlush(originalTask);

        Long originalTaskId = originalTask.getId();
        Long nextTaskId = nextTask.getId();

        assertTrue(taskRepository.existsById(originalTaskId));
        assertTrue(taskRepository.existsById(nextTaskId));

        //実行：元タスクの削除
        taskService.deleteTask(originalTaskId);
        taskRepository.flush();

        //確認：期待した結果になっているか
        assertFalse(taskRepository.existsById(originalTaskId));
        assertTrue(taskRepository.existsById(nextTaskId));
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

    //テストデータ作成用メソッド（サブタスク）
    private Task createTestSubtask(String title, int sortOrder, Task parentTask){
        Task task = new Task();
        task.setTitle(title);
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.MEDIUM);
        task.setSortOrder(sortOrder);
        task.setParentTask(parentTask);

        return taskRepository.saveAndFlush(task);
    }

}					