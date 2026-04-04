package com.care.medi.exception;

public class ResourceNotFoundException extends  RuntimeException {
    public ResourceNotFoundException(String message,Exception ex){
        super(message,ex);
    }
    public ResourceNotFoundException(String message){
        super(message);
    }
}
