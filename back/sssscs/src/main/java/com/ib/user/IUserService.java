package com.ib.user;

public interface IUserService {
	User register(User user);
	User findById(Long issuer);
}
