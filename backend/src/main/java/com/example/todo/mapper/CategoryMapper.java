package com.example.todo.mapper;

import com.example.todo.dto.CategoryCreateRequest;
import com.example.todo.dto.CategoryResponse;
import com.example.todo.entity.Category;

public class CategoryMapper {

    //DTOからEntityに変換
     public static Category toEntity(CategoryCreateRequest request) {

        Category category = new Category();

        category.setName(request.getName());
        category.setColor(request.getcolorCode());

        return category;
    }

    //entityからレスポンス用のDTOに変換
    public static CategoryResponse toResponse(Category category) {

        CategoryResponse response = new CategoryResponse();

        response.setId(category.getId());
        response.setName(category.getName());
        response.setCategoryColor(category.getColor());

        return response;
    }
}
