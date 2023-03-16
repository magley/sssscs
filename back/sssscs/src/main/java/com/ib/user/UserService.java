package com.ib.user;

import com.ib.user.dto.UserCreateDto;

public interface UserService {
	public User register(UserCreateDto dto);
}
