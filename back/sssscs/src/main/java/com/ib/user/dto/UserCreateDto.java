package com.ib.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {
	private String email;
	private String password;
	private String name;
	private String surname;
	private String phoneNumber;
}
