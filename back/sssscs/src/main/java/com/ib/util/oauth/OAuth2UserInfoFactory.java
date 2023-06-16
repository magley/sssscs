package com.ib.util.oauth;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase("github")) {
            return new GithubOAuth2UserInfo(attributes);
        } else {
        	// TODO: custom exception
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Login with " + registrationId + " is not supported");
        }
    }
}
