package com.example.todo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.todo.dto.CategoryCreateRequest;
import com.example.todo.dto.CategoryResponse;
import com.example.todo.entity.Category;
import com.example.todo.entity.Task;
import com.example.todo.mapper.CategoryMapper;
import com.example.todo.repository.IFCategoryRepository;
import com.example.todo.repository.IFTaskRepository;
import com.example.todo.exception.DuplicateResourceException;
import com.example.todo.exception.ResourceNotFoundException;



@Service 
public class CategoryService {
    private final IFCategoryRepository categoryRepository;
    private final IFTaskRepository taskRepository;

    public CategoryService(IFCategoryRepository categoryRepository, IFTaskRepository taskRepository){
        this.categoryRepository = categoryRepository;
        this.taskRepository = taskRepository;
    }

    //カテゴリ一覧取得
    @Transactional(readOnly = true)
    public List<CategoryResponse>getCategories(){
        return categoryRepository.findAll().stream().map(CategoryMapper::toResponse).toList();
    }

    //カテゴリ追加
    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest request){
        Category category = CategoryMapper.toEntity(request);
        
        //カテゴリ名重複チェック
        if(categoryRepository.existsByName(request.getName())){
            throw new DuplicateResourceException("カテゴリ名「" + request.getName() + "」はすでに登録されています");
        }

        Category savedCategory = categoryRepository.save(category);

        return CategoryMapper.toResponse(savedCategory);
        
    }

    //カテゴリ削除
    @Transactional 
    public void deleteCategory(Long id){
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("カテゴリが見つかりません"));
        
        List<Task> tasks = taskRepository.findByCategory_Id(id);

        for (Task task : tasks){
            task.setCategory(null);
        }

        taskRepository.saveAllAndFlush(tasks);

        categoryRepository.delete(category);
    }

}
