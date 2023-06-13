package com.ib.certificate.dto;

import com.ib.certificate.Certificate;
import com.ib.pki.SubjectData;

import jakarta.validation.Valid;
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
	@Valid
	private SubjectData subjectData;
	private Long parentId;
}
