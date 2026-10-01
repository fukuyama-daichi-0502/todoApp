package com.example.todo.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.todo.entity.Category;
import com.example.todo.entity.Task;
import com.example.todo.entity.TaskPriority;
import com.example.todo.entity.TaskStatus;
import com.example.todo.repository.IFCategoryRepository;
import com.example.todo.repository.IFTaskRepository;

import jakarta.persistence.EntityManager;

@SpringBootTest 
@Transactional 
public class CategoryServiceTest {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private IFCategoryRepository categoryRepository;

    @Autowired
    private IFTaskRepository taskRepository;

    @Autowired
    private EntityManager entityManager;

    private Category testCategory;

    //テスト用のカテゴリを作成
    @BeforeEach
    void setUp(){
        testCategory = new Category();
        testCategory.setName("JUnitテストカテゴリ");
        testCategory.setColor("#000000");
        testCategory = categoryRepository.saveAndFlush(testCategory);
    }

    @Test
    void categoryDeleteTest(){
        // 準備：テスト対象のタスクを1件作る
        Task task = new Task();
        
        task.setTitle("JUnitテストTask");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.MEDIUM);
        int nextSortOrder = taskRepository.findTopByOrderBySortOrderDesc().map(Task::getSortOrder).orElse(0) + 1;
        task.setSortOrder(nextSortOrder);
        task.setCategory(testCategory);

        task = taskRepository.saveAndFlush(task);

        Long taskId = task.getId();
        Long categoryId = testCategory.getId();

        // 実行：対象データを削除する
        categoryService.deleteCategory(categoryId);
        entityManager.flush();
        //メモリ上で管理しているEntityをクリア
        entityManager.clear();

        // 確認：期待した結果になっているか
        //カテゴリが削除されているかの確認
        assertFalse(categoryRepository.existsById(categoryId));
        Task updatedTask = taskRepository.findById(taskId).orElseThrow();
        //DB上のカテゴリがnullになっているかの確認
        assertNull(updatedTask.getCategory());
    }
    


}
