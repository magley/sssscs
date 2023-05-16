// Copied from professor Vidaković's material with some changes.

package com.ib.util.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ib.user.IUserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
	@Autowired
	private IUserService userService;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		try {	
			this.doJwtAuthenticationFilter(request);
			chain.doFilter(request, response);
		} catch (ExpiredJwtException ex) {
			System.err.println(ex);
		}
	}

	private void doJwtAuthenticationFilter(HttpServletRequest request) {
		System.err.println(request.getRequestURL().toString() + " " + request.getMethod());
		if (!request.getRequestURL().toString().contains("/api/")) {
			return;
		}

		try {
			String jwtToken = this.getTokenFromRequest(request);
			var authenticationToken = this.getAuthFromToken(jwtToken);
			if (authenticationToken == null) {
				return;
			}
			
			jwtTokenUtil.validateToken(jwtToken); // Throws expired token exception
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);	
		} catch (ExpiredJwtException e) {
			throw e;
		} catch (Exception e) {
			return;
		}
	}

	private String getTokenFromRequest(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		return getTokenFromHeader(header);
	}

	public String getTokenFromHeader(String header) {
		if (header == null || !header.contains("Bearer")) {
			return null;
		}
		return header.substring(header.indexOf("Bearer ") + 7);
	}

	public UsernamePasswordAuthenticationToken getAuthFromToken(String jwt) {
		UserDetails userDetails;
		try {
			userDetails = this.getUserDetailsFromJwtToken(jwt);
		} catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
			throw e;
		}
		var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());
		return authenticationToken;
	}

	private UserDetails getUserDetailsFromJwtToken(String jwtToken) throws JwtException, IllegalArgumentException, UsernameNotFoundException {
		String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
		return this.userService.loadUserByUsername(username);
	}
}