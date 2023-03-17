package com.ib.user;

import com.ib.user.dto.UserCreateDto;

public interface IUserService {
	public User register(UserCreateDto dto);
}
