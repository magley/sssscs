package com.ib.certificate.dto;

import java.time.LocalDateTime;

import com.ib.certificate.Certificate;

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
	private Long creatorId;
	@NotNull
	private String subjectName;
	@NotNull
	private LocalDateTime validTo;
	private Long parentId;
}
