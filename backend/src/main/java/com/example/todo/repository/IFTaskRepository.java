package com.example.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.todo.entity.Task;

public interface  IFTaskRepository extends JpaRepository<Task, Long>{
    
}
