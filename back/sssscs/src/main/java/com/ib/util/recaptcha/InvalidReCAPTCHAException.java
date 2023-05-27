package com.ib.util.recaptcha;

public class InvalidReCAPTCHAException extends RuntimeException {
    public InvalidReCAPTCHAException(String message) {
        super(message);
    }
}
