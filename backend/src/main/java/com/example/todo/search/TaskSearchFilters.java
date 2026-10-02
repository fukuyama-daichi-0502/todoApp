package com.example.todo.search;

import java.time.LocalDate;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.example.todo.entity.Task;
import com.example.todo.entity.TaskPriority;
import com.example.todo.entity.TaskStatus;

public final class TaskSearchFilters {
    public static Specification<Task> titleContains(String title){
        String keyword = title.trim().toLowerCase(Locale.ROOT);

        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.like(
                criteriaBuilder.lower(
                    root.get("title")
                ),
                "%" + keyword + "%");
    }

    public static Specification<Task> hasStatus(TaskStatus status){

        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(
                root.get("status"),
                status
            );
    }


    public static Specification<Task> dueToday(LocalDate today){
        
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(
                root.get("dueDate"),
                today
            );
    }

    public static Specification<Task> overdue(LocalDate today){
        
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.and(
                criteriaBuilder.lessThan(
                    root.<LocalDate>get("dueDate"),
                    today
                ),
                criteriaBuilder.not(
                    root.get("status")
                        .in(
                            TaskStatus.DONE,
                            TaskStatus.CANCELLED
                        )
                )
            );
    }

    public static Specification<Task> hasPriority(TaskPriority priority){

        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(
                root.get("priority"),
                priority
            );
    }

    public static Specification<Task> hasCategory(Long categoryId){

        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(
                root.get("category")
                    .get("id"),
                categoryId
            );
    }

    public static Specification<Task> hasNoCategory(){

        return (root, query, criteriaBuilder) ->
        criteriaBuilder.isNull(
            root.get("category")
        );
    }

}

