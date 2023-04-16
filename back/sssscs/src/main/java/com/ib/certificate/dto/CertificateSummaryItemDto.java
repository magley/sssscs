package com.ib.certificate.dto;

import java.time.LocalDateTime;

import com.ib.certificate.Certificate.Type;
import com.ib.pki.SubjectData;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificateSummaryItemDto {
	@NotNull
	private Long id;
	@NotNull
	private LocalDateTime validFrom;
	@NotNull
	private SubjectData subjectData;
	@NotNull
	private Type type;
}
