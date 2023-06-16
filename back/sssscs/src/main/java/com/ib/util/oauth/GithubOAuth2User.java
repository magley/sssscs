package com.ib.util.oauth;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GithubOAuth2User implements OAuth2User {
	
	private Map<String,Object> attributes;
	private Collection<? extends GrantedAuthority> authorities;
	private String name;
}
