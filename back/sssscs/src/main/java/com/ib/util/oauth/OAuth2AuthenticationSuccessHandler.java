package com.ib.util.oauth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.ib.user.IUserService;
import com.ib.user.User;
import com.ib.util.exception.EntityNotFoundException;
import com.ib.util.security.JwtTokenUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// partial attribution: https://github.com/callicoder/spring-boot-react-oauth2-social-login-demo
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    // TODO: unhardcode, extract outside
    private static final String REDIRECT_URL = "http://localhost:4200/login";
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private IUserService userService;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);
        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        User user;
        try {
            user = processOAuth2User(oAuth2User);
        } catch (AuthenticationException ex) {
        	// TODO: remove this code
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
		String token = jwtTokenUtil.generateToken(user.getEmail(), user.getId(), user.getRole().toString());
        return UriComponentsBuilder.fromUriString(REDIRECT_URL)
                .queryParam("redirectToken", token)
                .build().toUriString();
    }

    private User processOAuth2User(OAuth2User oAuth2User) {
    	// TODO: don't assume github, extract from request url or something
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo("github", oAuth2User.getAttributes());
        if(!StringUtils.hasLength(oAuth2UserInfo.getEmail())) {
            // TODO: custom exception
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email not found from OAuth2 provider");
        }
        // TODO: to expand with more OAuth providers, we might need to keep which provider user used when logged in
        try {
            return userService.findByEmail(oAuth2UserInfo.getEmail());
        } catch (EntityNotFoundException ex) {
            return this.registerNewUser(oAuth2UserInfo);
        }
    }

    private User registerNewUser(OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setPassword("");
        user.setName(oAuth2UserInfo.getName());
        user.setSurname("");  // this might be bad, but github doesn't have it
        user.setPhoneNumber("");  // FIXME: will this cause problem with annoying you to verify?
        return userService.register(user);
    }
}
