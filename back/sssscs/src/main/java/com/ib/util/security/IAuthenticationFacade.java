package com.ib.util.security;

import org.springframework.security.core.Authentication;
import com.ib.user.User;

public interface IAuthenticationFacade {
    Authentication getAuthentication();
    User getUser();
}
