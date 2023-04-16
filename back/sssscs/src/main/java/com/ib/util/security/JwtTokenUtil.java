// Copied from professor Vidakovic's material with some minor changes

package com.ib.util.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil {
	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 10000000;
	public static final long JWT_LIFE = 1080000000;
	@Value("verysecret")
	private String secret;

	// retrieve username from jwt token
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	// retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	// for retrieveing any information from token we will need the secret key
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	// generate token for user
	public String generateToken(String username, Long id, String role) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("id", id);
		claims.put("role", role);
		return doGenerateToken(claims, username);
	}

	// while creating the token -
	// 1. Define claims of the token, like Issuer, Expiration, Subject, and the ID
	// 2. Sign the JWT using the HS512 algorithm and secret key.
	// 3. According to JWS Compact
	// Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	// compaction of the JWT to a URL-safe string
	private String doGenerateToken(Map<String, Object> claims, String subject) {
//		Jwts.builder()
//			.setClaims(claims)
//			.setSubject(subject)
//			.setIssuedAt(new Date(System.currentTimeMillis()))
//			.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
//			.signWith(SignatureAlgorithm.HS512, secret).compact();
        return Jwts.builder()
                .setIssuer("sssscs")
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + JWT_LIFE))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
	}
}
