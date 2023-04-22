package com.ib.verification;

import java.time.LocalDateTime;

import com.ib.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class VerificationCode {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = true)
	private String code = null;
	@Column(nullable = false)
	private LocalDateTime expiraiton;
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;
	@Column(nullable = false)
	private Boolean valid = true;
}