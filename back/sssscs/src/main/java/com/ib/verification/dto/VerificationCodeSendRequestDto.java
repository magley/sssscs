package com.ib.verification.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VerificationCodeSendRequestDto {
	public enum Method {
		EMAIL, SMS
	}

	@NotNull
	private String userEmail;
	@NotNull
	private Method method;
	@NotNull
	private Boolean dontActuallySend;
}