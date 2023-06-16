package com.ib.user.dto;

import com.ib.util.validation.Password;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRotationDto {
	private String userEmail;
	private String oldPassword;
	@NotNull
	@Password
	@Size(min=8, message="Must contain at least 8 characters")
	private String newPassword;
}
