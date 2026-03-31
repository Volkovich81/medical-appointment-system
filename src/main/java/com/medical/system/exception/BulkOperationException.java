package com.medical.system.exception;

public class BulkOperationException extends RuntimeException {
    public BulkOperationException(String message) {
        super(message);
    }
}