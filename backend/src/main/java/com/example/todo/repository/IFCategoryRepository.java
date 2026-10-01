package com.example.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.todo.entity.Category;

public interface IFCategoryRepository extends JpaRepository<Category, Long>{
    boolean existsByName(String name);
}
