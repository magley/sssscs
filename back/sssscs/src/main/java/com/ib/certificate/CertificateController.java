package com.ib.certificate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ib.certificate.dto.CertificateRequestCreateDto;
import com.ib.user.IUserService;
import com.ib.util.DTO;

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
			// TODO: Link to parent.
			req.setParent(null);
		}
		
		req = certificateService.makeRequest(req);
		return ResponseEntity.ok(req.toString());
	}
}
