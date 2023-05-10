package com.ib.verification.dto;

import com.ib.util.validation.Password;

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
public class VerificationCodeResetDto {
	@NotNull
	private String userEmail;
	@NotNull
	private String code;
	@NotNull
	@Password
	private String newPassword;
}
