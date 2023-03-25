package com.ib.util.exception;

public abstract class EntityException extends RuntimeException {

    public EntityException(String message) {
        super(message);
    }
}
