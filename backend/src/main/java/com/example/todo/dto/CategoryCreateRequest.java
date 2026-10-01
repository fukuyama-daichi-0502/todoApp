package com.example.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CategoryCreateRequest {
    @NotBlank(message = "カテゴリ名は必須です")
    @Size(max = 50, message = "カテゴリ名は20字以内で入力してください")
    private String name;

    @NotBlank(message = "カラーコードは必須です")
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "カラーコードは#RRGGBB形式で入力してください")
    private String colorCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getcolorCode() {
        return colorCode;
    }

    public void setcolorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    
}
