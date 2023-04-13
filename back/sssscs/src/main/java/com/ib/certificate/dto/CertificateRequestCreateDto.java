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
	private Certificate.Type type;
	private Long creatorId;
	private String subjectName;
	private LocalDateTime validTo;
	private Long parentId;
}
