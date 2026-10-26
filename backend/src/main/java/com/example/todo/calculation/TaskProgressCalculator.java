package com.example.todo.calculation;

import java.util.List;

import com.example.todo.entity.Task;
import com.example.todo.entity.TaskStatus;

public class TaskProgressCalculator {
    public int calculateProgress(Task parentTask, List<Task> subtasks){
        int activeSubtaskCount = 0;
        int completedSubtaskCount = 0;

        for(Task subtask : subtasks){
            if(subtask.getStatus() == TaskStatus.CANCELLED){
                continue;
            }

            activeSubtaskCount++;

            if(subtask.getStatus() == TaskStatus.DONE){
                completedSubtaskCount++;
            }
        }

        //サブタスクが0件またはすべてCANCELEDの場合
        if(activeSubtaskCount == 0){
            return parentTask.getStatus() == TaskStatus.DONE ? 100 : 0;
        }

        return (int)Math.round((double)completedSubtaskCount / activeSubtaskCount * 100);
    }
}
