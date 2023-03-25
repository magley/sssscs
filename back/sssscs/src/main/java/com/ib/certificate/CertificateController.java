package com.ib.certificate;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ib.certificate.dto.CertificateRequestCreateDto;
import com.ib.user.IUserService;
import com.ib.util.DTO;

import jakarta.websocket.server.PathParam;

@RestController
@RequestMapping("/api/cert")
public class CertificateController {
	@Autowired
	private ICertificateService certificateService;
	
	@Autowired
	private IUserService userService;
	
	@PostMapping("/request")
	public ResponseEntity<String> makeRequest(/*@DTO(CertificateRequestCreateDto.class)*/ @RequestBody CertificateRequestCreateDto certificate) {
		// TODO: Automatic mapping.
		
		CertificateRequest req = new CertificateRequest();
		req.setIssuer(userService.findById(certificate.getIssuerId()));
		req.setType(certificate.getType());
		req.setValidTo(certificate.getValidTo());
		if (certificate.getParentId() != 0) {
			Certificate parent = certificateService.findById(certificate.getParentId());
			
			// TODO: 404 if parent is null.
			
			req.setParent(parent);
		}
		
		req = certificateService.makeRequest(req);
		return ResponseEntity.ok(req.toString());
	}
	
	@PutMapping("/request/accept/{id}")
	public ResponseEntity<String> acceptRequest(@PathVariable Long id) {
		CertificateRequest req = certificateService.findReqById(id);
		if (req == null) {
			return new ResponseEntity<String>("", HttpStatus.NOT_FOUND);
		}
		
		Certificate cert = certificateService.save(req);
		return ResponseEntity.ok(cert.toString());
	}
}
