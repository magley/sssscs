package com.ib.util.exception;

import java.io.Serial;

public abstract class EntityException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 6089383742016495500L;

	public EntityException(String message) {
        super(message);
    }
}
