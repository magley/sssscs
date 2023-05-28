package com.ib.user.dto;

import com.ib.util.validation.Password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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
	@Size(max = 100)
	private String email;
	@NotNull
	@Password
	private String password;
	@NotNull
	@Size(max = 100)
	private String name;
	@NotNull
	@Size(max = 100)
	private String surname;
	@NotNull
	@Size(max = 18)
	private String phoneNumber;
	@NotNull
	private String token;
}
