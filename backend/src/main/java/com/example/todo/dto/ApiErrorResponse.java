package com.example.todo.dto;

public class ApiErrorResponse {
    private int status;

    private String message;

    public ApiErrorResponse(String message){
        this.message = message;
    }

    public ApiErrorResponse(int status, String message){
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
