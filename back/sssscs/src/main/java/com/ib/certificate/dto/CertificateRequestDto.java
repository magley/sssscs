package com.ib.certificate.dto;

import java.time.LocalDateTime;

import com.ib.certificate.Certificate.Type;
import com.ib.certificate.CertificateRequest.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateRequestDto {
	private Long id;
	private String subjectName;
	private LocalDateTime validTo;
	private Long parentId;
	private Type type;
	private Status status;
	private String rejectionReason;
}
