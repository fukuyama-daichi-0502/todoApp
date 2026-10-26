package com.example.todo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.todo.dto.ApiErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //HTTPエラー
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException exception){
        ApiErrorResponse response = new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    //Validationエラー
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception){
        String message = exception.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        
        ApiErrorResponse response = new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(),message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    //複数項目の組み合わせによるValidationエラー
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidRequestException(InvalidRequestException exception){
        ApiErrorResponse errorResponse = new ApiErrorResponse(exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    //カテゴリ名重複エラー
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateResourceException (DuplicateResourceException exception){
        ApiErrorResponse response = new ApiErrorResponse(HttpStatus.CONFLICT.value(), exception.getMessage());
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

}
