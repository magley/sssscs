package com.ib.user.dto;

import com.ib.util.validation.Password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {
	@NotNull
	@Email
	@Size(min=2, max = 100, message="Must contain between 2 and 100 characters")
	private String email;
	@NotNull
	@Password
	@Size(min=8, message="Must contain at least 8 characters")
	private String password;
	@NotNull
	@Size(min=2, max=30, message="Must contain between 2 and 30 characters")
	@Pattern(regexp = "^[A-Za-z][A-Za-z ]*[A-Za-z]$", message="Must be letters or spaces (except at the start and end)")
	private String name;
	@NotNull
	@Size(min=2, max=30, message="Must contain between 2 and 30 characters")
	@Pattern(regexp = "^[A-Za-z][A-Za-z ]*[A-Za-z]$", message="Must be letters or spaces (except at the start and end)")
	private String surname;
	@NotNull
	@Size(max = 18, message="Must contain at most 18 characters")
	@Pattern(regexp = "^[0-9]*$", message="Must be numbers")
	private String phoneNumber;
	@NotNull
	private String token;
}
