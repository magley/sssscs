package com.ib.certificate.dto;

import java.time.LocalDateTime;

import com.ib.certificate.Certificate.Type;
import com.ib.certificate.request.CertificateRequest.Status;
import com.ib.pki.SubjectData;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateRequestDto {
	@NotNull
	private Long id;
	@NotNull
	private SubjectData subjectData;
	@NotNull
	private LocalDateTime validTo;
	private Long parentId;
	@NotNull
	private Type type;
	@NotNull
	private Status status;
	private String rejectionReason;
}
