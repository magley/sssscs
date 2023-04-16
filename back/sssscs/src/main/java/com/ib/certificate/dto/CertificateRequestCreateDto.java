package com.ib.certificate.dto;

import java.time.LocalDateTime;

import com.ib.certificate.Certificate;
import com.ib.pki.SubjectData;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateRequestCreateDto {
	@NotNull
	private Certificate.Type type;
	@NotNull
	private Long creatorId;
	@NotNull
	private SubjectData subjectData;
	@NotNull
	private LocalDateTime validTo;
	private Long parentId;
}
