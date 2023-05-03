package com.ib.certificate.request;

import java.time.LocalDateTime;

import com.ib.certificate.Certificate;
import com.ib.certificate.Certificate.Type;
import com.ib.pki.SubjectData;
import com.ib.user.User;
import com.ib.user.User.Role;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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
public class CertificateRequest {
	public enum Status {
		PENDING, ACCEPTED, REJECTED;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	private User creator;

	@Column(nullable = false)
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "name", column = @Column(name = "subject_data_name")),
			@AttributeOverride(name = "surname", column = @Column(name = "subject_data_surname")),
			@AttributeOverride(name = "email", column = @Column(name = "subject_data_email")),
			@AttributeOverride(name = "organization", column = @Column(name = "subject_data_organization")),
			@AttributeOverride(name = "commonName", column = @Column(name = "subject_data_common_name")) })
	private SubjectData subjectData;

	@Column(nullable = false)
	private LocalDateTime validTo;

	@ManyToOne(fetch = FetchType.LAZY)
	private Certificate parent;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private Type type;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private Status status;

	@Column(nullable = true)
	private String rejectionReason;

	/**
	 * @return Whether validTo is expired. Should never happen, otherwise the
	 *         request is invalid.
	 */
	public boolean isExpired() {
		return getValidTo().isBefore(LocalDateTime.now());
	}

	/**
	 * @return True if the creator can create this type certificate.
	 */
	public boolean isCreatorAuthorized() {
		// Regular cannot create root.
		return !(getCreator().getRole() == Role.REGULAR && (getParent() == null || getType() == Type.ROOT));
	}

	/**
	 * @return False if no parent while the type is anything except ROOT.
	 */
	public boolean isTypeValid() {
		return type == Type.ROOT || parent != null;
	}

	/**
	 * @return True if this certificate will expire after its parent (if any). This
	 *         is invalid state and such certificate request should not be created.
	 */
	public boolean expiresAfterParent() {
		if (parent == null) {
			return false;
		}
		return getValidTo().isAfter(parent.getValidTo());
	}

	public boolean isChildOfRevokedParent() {
		return parent != null && parent.getStatus() == Certificate.Status.REVOKED;
	}

	public boolean parentIsEndCertificate() {
		return parent != null && parent.getType() == Type.END;
	}
}
