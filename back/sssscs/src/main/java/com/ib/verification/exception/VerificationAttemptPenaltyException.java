package com.ib.verification.exception;

public class VerificationAttemptPenaltyException extends RuntimeException {
	private static final long serialVersionUID = -1997252693702496776L;

	public VerificationAttemptPenaltyException() {
		super("Too many failed attempts at verification. Locking your account...");
	}
}
