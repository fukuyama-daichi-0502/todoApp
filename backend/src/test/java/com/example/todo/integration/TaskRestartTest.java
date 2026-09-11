package com.example.todo.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.example.todo.TodoApplication;
import com.example.todo.entity.Task;
import com.example.todo.entity.TaskStatus;
import com.example.todo.repository.IFTaskRepository;

public class TaskRestartTest {
    
    //アプリケーションを再起動しても保存されているタスクが失われないこと
    @Test 
    void applicationRestartTest(){
        //準備　1回目の起動、テストデータ1件を作成
        Long taskId = null;

        String testTitle = "JUnit再起動テスト_testData";

        ConfigurableApplicationContext context1 = null;
        ConfigurableApplicationContext context2 = null;
        
        try{
            //1回目の起動
            context1 = new SpringApplicationBuilder(TodoApplication.class).web(WebApplicationType.NONE).run();
            IFTaskRepository repository1 = context1.getBean(IFTaskRepository.class);

            //テスト用データを作成
            Task task = new Task();
            task.setTitle(testTitle);
            task.setStatus(TaskStatus.TODO);
            Task savedTask = repository1.saveAndFlush(task);

            taskId = savedTask.getId();

            assertTrue(repository1.existsById(taskId));

            //実行：アプリケーションの停止、データが保存されているか確認
            context1.close();
            context1 = null;

            //2回目の起動
            context2 = new SpringApplicationBuilder(TodoApplication.class).web(WebApplicationType.NONE).run();
            IFTaskRepository repository2 = context2.getBean(IFTaskRepository.class);

            //保存したデータを取得
            Task reloadedTask = repository2.findById(taskId).orElse(null);

            //確認：期待した結果になっているか
            assertTrue(reloadedTask != null);
            assertEquals(testTitle,reloadedTask.getTitle());
        }finally{
            if(context1 != null){
                context1.close();
            }

            if(context2 != null){
                context2.close();
            }
        }

    }
}
