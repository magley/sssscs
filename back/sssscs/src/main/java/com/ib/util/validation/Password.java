package com.ib.util.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { PasswordValidator.class })
public @interface Password {

	String message() default "must contain only characters a-z, A-Z, 0-9, @, #, $, ^, +, =; "
			+ "and must be between 8 and 15 characters; and contain at least one digit and capital letter";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
