package com.ib.certificate;

import java.time.LocalDateTime;

import com.ib.certificate.Certificate.Type;
import com.ib.user.User;
import com.ib.user.User.Role;

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
@Table(name = "certificate_requests")
public class CertificateRequest {
	public enum Status {
		PENDING, ACCEPTED, REJECTED;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//@Column(nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private User issuer;

	@Column(nullable = false)
	private LocalDateTime validTo;
	
	//@Column(nullable = true)
	@ManyToOne(fetch = FetchType.LAZY)
	private Certificate parent;
	
	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private Type type;
	
	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private Status status;
	
	/**
	 * @return Whether validTo is expired. Should never happen, 
	 * otherwise the request is invalid.
	 */
	public boolean isExpired() {
		return getValidTo().isBefore(LocalDateTime.now());
	}
	
	/**
	 * @return True if the issuer can issue a certificate of this type. 
	 */
	public boolean isIssuerAuthorized() {
		if (getIssuer().getRole() == Role.REGULAR && (getParent() == null || getType() == Type.ROOT)) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return False if no parent while the type is anything except ROOT.
	 */
	public boolean isTypeValid() {
		return type == Type.ROOT || parent != null;
	}
}
