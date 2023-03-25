package com.ib.certificate.dto;

import java.time.LocalDateTime;

import com.ib.certificate.Certificate.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificateSummaryItemDto {
	private Long id;
	private LocalDateTime validFrom;
	private String subjectName;
	private Type type;
}
