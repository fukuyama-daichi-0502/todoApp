package com.example.todo.calculation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.todo.entity.Task;
import com.example.todo.entity.TaskStatus;

public class TaskProgressCalculatorTest {
    private TaskProgressCalculator taskProgressCalculator;

    @BeforeEach
    void setUp(){
        taskProgressCalculator = new TaskProgressCalculator();
    }

    //CANCELLEDのサブタスクを計算に含めていないか
    @Test
    void calculateProgressExcludesCancelledSubtasks(){
        //準備：テストデータの作成
        Task parentTask = createTask(TaskStatus.IN_PROGRESS);

        List<Task> subtasks = List.of(
            createTask(TaskStatus.DONE),
            createTask(TaskStatus.DONE),
            createTask(TaskStatus.TODO),
            createTask(TaskStatus.CANCELLED)
        );

        //実行：作成したデータの進捗率を計算
        int progress = taskProgressCalculator.calculateProgress(parentTask, subtasks);

        //確認：期待した結果になっているか
        assertEquals(67,progress);
    }

    //CANCELLED以外のサブタスクがDONEの時
    @Test 
    void calculateProgressReturns100WhenAllActiveSubtasksAreDone(){
        //準備：テストデータの作成
        Task parentTask = createTask(TaskStatus.IN_PROGRESS);

        List<Task> subtasks = List.of(
            createTask(TaskStatus.DONE),
            createTask(TaskStatus.DONE),
            createTask(TaskStatus.CANCELLED)
        );

        //実行：作成したデータの進捗率を計算
        int progress = taskProgressCalculator.calculateProgress(parentTask, subtasks);

        //確認：期待した結果になっているか
        assertEquals(100, progress);
    }

    //DONEのサブタスクがない場合    
    @Test 
    void calculateProgressReturns0WhenNosubtasksAreDone(){
        //準備：テストデータの作成
        Task parentTask = createTask(TaskStatus.IN_PROGRESS);

        List<Task> subtasks = List.of(
            createTask(TaskStatus.TODO),
            createTask(TaskStatus.IN_PROGRESS),
            createTask(TaskStatus.ON_HOLD),
            createTask(TaskStatus.CANCELLED)
        );

        //実行：作成したデータの進捗率を計算
        int progress = taskProgressCalculator.calculateProgress(parentTask, subtasks);

        //確認：期待した結果になっているか
        assertEquals(0, progress);
    }

    //サブタスクがない場合
    @Test 
    void calculateProgressUsesParentStatusWhenThereAreNoSubtasks(){
        //準備：テストデータの作成
        Task todoParentTask = createTask(TaskStatus.TODO);
        Task doneParentTask = createTask(TaskStatus.DONE);

        List<Task> subtasks = new ArrayList<>();

        //実行：作成したデータの進捗率を計算
        int todoProgress = taskProgressCalculator.calculateProgress(todoParentTask, subtasks);
        int doneProgress = taskProgressCalculator.calculateProgress(doneParentTask, subtasks);

        //確認：期待した結果になっているか
        assertEquals(0, todoProgress);
        assertEquals(100, doneProgress);
    }

    //サブタスクがすべてCANCELLEDの場合
    @Test 
    void calculateProgressUsesParentStatusWhenAllSubtasksAreCancelled(){
        //準備：テストデータの作成
        Task todoParentTask = createTask(TaskStatus.TODO);
        Task doneParentTask = createTask(TaskStatus.DONE);

        List<Task> subtasks = List.of(
            createTask(TaskStatus.CANCELLED),
            createTask(TaskStatus.CANCELLED)
        );

        //実行：作成したデータの進捗率を計算
        int todoProgress = taskProgressCalculator.calculateProgress(todoParentTask, subtasks);
        int doneProgress = taskProgressCalculator.calculateProgress(doneParentTask, subtasks);

        //確認：期待した結果になっているか
        assertEquals(0, todoProgress);
        assertEquals(100, doneProgress);
    }

    //テスト用データの作成
    private Task createTask(TaskStatus status){
        Task task = new Task();
        task.setStatus(status);

        return task;
    }
}
