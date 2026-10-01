package com.example.todo.dto;

public class CategoryResponse {
    private Long id;
    private String name;
    private String categoryColor;

    public CategoryResponse(){}

    public CategoryResponse(Long id, String name, String categoryColor){
        this.id = id;
        this.name = name;
        this.categoryColor = categoryColor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(String color) {
        this.categoryColor = color;
    }

    

    
}
