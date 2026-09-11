package com.example.todo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.todo.repository.IFTaskRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IFTaskRepository taskRepository;

    //タイトルが空のまま保存するとエラーになり、保存されない
    @Test
    void titleNullTest() throws Exception{
        //準備：タイトルが空のリクエストを1件作る
        String requestBody = """
                {
                    "title": ""
                }
                """;

        //実行：作成したリクエストをコントローラーに送る　エラーが返ってくるか確認
        long beforeCount = taskRepository.count();
        mockMvc.perform(
            post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
        //400 Bad Request
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.message")
                    .value("タイトルを入力してください")
        );
        long afterCount = taskRepository.count();

        //確認：期待した結果になっているか
        assertEquals(beforeCount, afterCount);
    }
}
