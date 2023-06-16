// Copied from professor Vidakovic's material with some changes.

package com.ib.util.security;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.impl.crypto.MacProvider;

@Component
public class JwtTokenUtil {
	private static final long JWT_LIFE = 5 * 60 * 1000;
    private static final SecretKey secret = MacProvider.generateKey(SignatureAlgorithm.HS256);
    private static final byte[] secretBytes = secret.getEncoded();
    private static final String base64SecretBytes = Base64.getEncoder().encodeToString(secretBytes);

	public String generateToken(String username, Long id, String role) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("id", id);
		claims.put("role", role);
		return doGenerateToken(claims, username);
	}

	private String doGenerateToken(Map<String, Object> claims, String subject) {
		return Jwts.builder()
				.setIssuer("sssscs")
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() + JWT_LIFE))
				.signWith(SignatureAlgorithm.HS512, base64SecretBytes)
				.compact();
	}

	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}
	
	public Long getIdFromToken(String token) {
		final Claims claims = getAllClaimsFromToken(token);
		return (long)(int)claims.get("id");
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(base64SecretBytes).parseClaimsJws(token).getBody();
	}
	
	public boolean validateToken(String authToken) throws ExpiredJwtException {
		try {	
			Jwts.parser().setSigningKey(base64SecretBytes).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
			throw new BadCredentialsException("BAD_CREDENTIALS", ex);
		}
	}
	
	public String getJWTFromHeader(String header) {
		if (header == null || !header.contains("Bearer")) {
			return null;
		}
		try {
			String jwt = header.substring(header.indexOf("Bearer ") + 7);
			if (jwt.isEmpty()) {
				return null;
			}
			return jwt;
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
}
