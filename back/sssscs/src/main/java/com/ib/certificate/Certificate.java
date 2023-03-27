package com.ib.certificate;

import java.time.LocalDateTime;

import com.ib.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Certificate {
	public enum Type {
		ROOT,
		INTERMEDIATE,
		END
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//@Column(nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private User issuer;
	
	//@Column(nullable = true)
	@ManyToOne(fetch = FetchType.LAZY)
	private Certificate parent;
	
	@Column(nullable = false)
	private LocalDateTime validFrom;
	
	@Column(nullable = false)
	private LocalDateTime validTo;
	
	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private Type type;
}
