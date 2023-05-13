package com.ib.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRotationDto {
	private String userEmail;
	private String oldPassword;
	private String newPassword;
}
