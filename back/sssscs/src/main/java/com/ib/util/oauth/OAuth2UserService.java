package com.ib.util.oauth;

import java.util.HashMap;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    private static final String EMAILS_URL = "https://api.github.com/user/emails";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        try {
            return addEmailsToPrincipal(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User addEmailsToPrincipal(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        RequestEntity<Void> request = RequestEntity.get(EMAILS_URL)
        		.header("Authorization", "Bearer " + oAuth2UserRequest.getAccessToken().getTokenValue())
                .accept(MediaType.APPLICATION_JSON).build();
        RestTemplate restTemplate = new RestTemplate();
        System.err.println("Sending GET request to " + EMAILS_URL);
        List<GithubEmail> emails = restTemplate.exchange(request,
        		new ParameterizedTypeReference<List<GithubEmail>>() { })
        		.getBody();
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        GithubOAuth2User ret = new GithubOAuth2User();
        ret.setAuthorities(oAuth2User.getAuthorities());;
        ret.setName(oAuth2User.getName());
        ret.setAttributes(attributes);
        ret.getAttributes().putAll(oAuth2User.getAttributes());
        for (GithubEmail email : emails) {
			if (email.getPrimary() && !(email.getVerified() == true)) {
				// TODO: custom exception
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email not verified in OAuth2 provider");
			}
			if (email.getPrimary()) {
				ret.getAttributes().put("email", email.getEmail());
				break;
			}
		}
        if(!StringUtils.hasLength(ret.getAttribute("email"))) {
            // TODO: custom exception
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email not found from OAuth2 provider");
        }
        return ret;
    }
}
