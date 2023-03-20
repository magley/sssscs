package com.ib.common;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Class entityType, Long id) {
        super(String.format("%s with id %d not found", entityType.getName(), id));
    }
}
