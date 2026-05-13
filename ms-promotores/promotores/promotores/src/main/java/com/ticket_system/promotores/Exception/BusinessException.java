package com.ticket_system.promotores.Exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}