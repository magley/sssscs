package com.ib.certificate;

import java.time.LocalDateTime;

import com.ib.certificate.request.CertificateRequest;
import com.ib.pki.SubjectData;
import com.ib.user.User;

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
public class Certificate {
	public enum Type {
		ROOT, INTERMEDIATE, END
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// @Column(nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private User owner;

	// @Column(nullable = true)
	@ManyToOne(fetch = FetchType.LAZY)
	private Certificate parent;

	@Column(nullable = false)
	private LocalDateTime validFrom;

	@Column(nullable = false)
	private LocalDateTime validTo;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private Type type;

	@Column(nullable = false)
	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "name", column = @Column(name = "subject_data_name")),
			@AttributeOverride(name = "surname", column = @Column(name = "subject_data_surname")),
			@AttributeOverride(name = "email", column = @Column(name = "subject_data_email")),
			@AttributeOverride(name = "organization", column = @Column(name = "subject_data_organization")),
			@AttributeOverride(name = "commonName", column = @Column(name = "subject_data_common_name")) })
	private SubjectData subjectData;

	public Certificate(CertificateRequest req) {
		setParent(req.getParent());
		setOwner(req.getCreator());
		setType(req.getType());
		setValidFrom(LocalDateTime.now());
		setValidTo(req.getValidTo());
		setSubjectData(req.getSubjectData());
	}

	public String getSerialNumber() {
		return id.toString();
	}
}
