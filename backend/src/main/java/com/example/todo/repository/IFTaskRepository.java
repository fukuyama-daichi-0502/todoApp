package com.example.todo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.todo.entity.Task;

public interface  IFTaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task>{
    Optional<Task> findTopByOrderBySortOrderDesc();

    Optional<Task> findTopByParentTask_IdOrderBySortOrderDesc(Long parentTaskId);

    List<Task> findAllByOrderBySortOrderAsc();

    List<Task> findBySortOrderGreaterThanOrderBySortOrderAsc(Integer sortOrder);

    List<Task> findBySortOrderBetweenOrderBySortOrderDesc(Integer startSortOrder, Integer endSortOrder);

    List<Task> findBySortOrderBetweenOrderBySortOrderAsc(Integer startSortOrder, Integer endSortOrder);

    List<Task> findByCategory_Id(Long categoryId);

    List<Task> findBySortOrderGreaterThanOrderBySortOrderDesc(Integer sortOrder);

    List<Task> findByParentTask_IdOrderBySortOrderAsc(Long parentTaskId);

}
