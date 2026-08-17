package com.example.demo.exception;

public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s with %s = %s already exists.", resourceName, fieldName, fieldValue));
    }
}
