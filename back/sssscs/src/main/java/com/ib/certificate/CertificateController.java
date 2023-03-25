package com.ib.certificate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ib.certificate.CertificateRequest.Status;
import com.ib.certificate.dto.CertificateRequestCreateDto;
import com.ib.certificate.dto.CertificateRequestDto;
import com.ib.user.IUserService;
import com.ib.user.User;

@RestController
@RequestMapping("/api/cert")
public class CertificateController {
	@Autowired
	private ICertificateService certificateService;
	
	@Autowired
	private ICertificateRequestService certificateRequestService;
	
	@Autowired
	private IUserService userService;
	
	@PostMapping("/request")
	public ResponseEntity<String> makeRequest(/*@DTO(CertificateRequestCreateDto.class)*/ @RequestBody CertificateRequestCreateDto certificate) {
		// TODO: Automatic mapping.
		
		CertificateRequest req = new CertificateRequest();
		req.setIssuer(userService.findById(certificate.getIssuerId()));
		req.setType(certificate.getType());
		req.setValidTo(certificate.getValidTo());
		req.setStatus(Status.PENDING);
		if (certificate.getParentId() != 0) {
			Certificate parent = certificateService.findById(certificate.getParentId());
			
			// TODO: 404 if parent is null.
			
			req.setParent(parent);
		}
		
		req = certificateRequestService.makeRequest(req);
		
		if (certificateRequestService.canAutoAccept(req)) {
			certificateService.accept(req);
		}
		
		return ResponseEntity.ok(req.toString());
	}
	
	@PutMapping("/request/accept/{id}")
	public ResponseEntity<String> acceptRequest(@PathVariable Long id) {
		CertificateRequest req = certificateRequestService.findByIdAndStatusEquals(id, Status.PENDING);
		if (req == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		
		Certificate cert = certificateService.accept(req);
		return ResponseEntity.ok(cert.toString());
	}
	
	@GetMapping("/request/{id}")
	public ResponseEntity<List<CertificateRequestDto>> getMyRequests(@PathVariable Long id) {
		User issuer = userService.findById(id);
		if (issuer == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		
		List<CertificateRequest> requests = certificateRequestService.findByIssuer(issuer);
		List<CertificateRequestDto> result = requests.stream()
				.map(r -> new CertificateRequestDto(
						r.getId(), 
						r.getIssuer().getId(), 
						r.getValidTo(), 
						r.getParent() != null ? r.getParent().getId() : 0, 
						r.getType(), 
						r.getStatus())
		).toList();
		
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/request/incoming/{id}")
	public ResponseEntity<List<CertificateRequestDto>> getPendingRequestsIssuedTo(@PathVariable Long id) {
		User issuee = userService.findById(id);
		if (issuee == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
		
		List<CertificateRequest> requests = certificateRequestService.findByIssuee(issuee);
		List<CertificateRequestDto> result = requests.stream()
				.map(r -> new CertificateRequestDto(
						r.getId(), 
						r.getIssuer().getId(), 
						r.getValidTo(), 
						r.getParent() != null ? r.getParent().getId() : 0, 
						r.getType(), 
						r.getStatus())
		).toList();
		
		return ResponseEntity.ok(result);
	}
	
	
}
