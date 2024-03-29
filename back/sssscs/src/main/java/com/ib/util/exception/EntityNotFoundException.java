package com.ib.util.exception;

import java.io.Serial;

public class EntityNotFoundException extends EntityException {
	@Serial
	private static final long serialVersionUID = 1822712875031490049L;

	public EntityNotFoundException(Class<?> entityType, String key) {
		super(String.format("%s with key %s not found", entityType.getSimpleName(), key));
	}

	public EntityNotFoundException(Class<?> entityType, Long id) {
		super(String.format("%s with id %d not found", entityType.getSimpleName(), id));
	}
}
