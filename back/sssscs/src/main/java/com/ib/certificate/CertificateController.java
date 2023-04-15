package com.ib.certificate;

import java.util.List;

import org.modelmapper.ModelMapper;
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

import com.ib.certificate.dto.CertificateRequestCreateDto;
import com.ib.certificate.dto.CertificateRequestDto;
import com.ib.certificate.dto.CertificateSummaryItemDto;
import com.ib.certificate.request.CertificateRequest;
import com.ib.certificate.request.CertificateRequest.Status;
import com.ib.certificate.request.ICertificateRequestService;
import com.ib.user.IUserService;
import com.ib.user.User;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cert")
public class CertificateController {
	@Autowired
	private ICertificateService certificateService;
	@Autowired
	private ICertificateRequestService certificateRequestService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ModelMapper modelMapper;
	
	@PostMapping("/request")
	public ResponseEntity<String> makeRequest(@Valid @RequestBody CertificateRequestCreateDto certificate) {
		CertificateRequest req = new CertificateRequest();
		req.setSubjectData(certificate.getSubjectData());
		req.setType(certificate.getType());
		req.setValidTo(certificate.getValidTo());
		req.setStatus(Status.PENDING);
		req.setCreator(userService.findById(certificate.getCreatorId()));
		if (certificate.getParentId() != null) {
			Certificate parent = certificateService.findById(certificate.getParentId());
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
		CertificateRequest req = certificateRequestService.findByIdAndStatus(id, Status.PENDING);
		
		if (req.getParent() != null) {
			// TODO: Get user from JWT.
			User user = req.getParent().getOwner();
			
			List<CertificateRequest> requestsUserCanSign = certificateRequestService.findRequestsByUserResponsibleForThem(user);
			if (!requestsUserCanSign.contains(req)) {
				// TODO: Uncomment once we retrieve real user from JWT.
				// throw new EntityNotFoundException(CertificateRequest.class, req.getId());
			}
		}
	
		Certificate cert = certificateService.accept(req);
		return ResponseEntity.ok(cert.toString());
	}
	
	@PutMapping("/request/reject/{id}")
	public ResponseEntity<?> rejectRequest(@PathVariable Long id, @RequestBody String reason) {
		CertificateRequest req = certificateRequestService.findByIdAndStatus(id, Status.PENDING);
		
		if (req.getParent() != null) {
			// TODO: Get user from JWT.
			User user = req.getParent().getOwner();
			
			List<CertificateRequest> requestsUserCanSign = certificateRequestService.findRequestsByUserResponsibleForThem(user);
			if (!requestsUserCanSign.contains(req)) {
				// TODO: Uncomment once we retrieve real user from JWT.
				// throw new EntityNotFoundException(CertificateRequest.class, req.getId());
			}
		}
		
		certificateRequestService.reject(req, reason);
		return new ResponseEntity<Void>((Void)null, HttpStatus.NO_CONTENT);
	}
	
	@GetMapping("/request/created/{id}")
	public ResponseEntity<List<CertificateRequestDto>> getRequestsCreatedBy(@PathVariable Long id) {
		User creator = userService.findById(id);
		
		List<CertificateRequest> requests = certificateRequestService.findByCreator(creator);
		List<CertificateRequestDto> result = requests.stream().map(r -> modelMapper.map(r, CertificateRequestDto.class)).toList();
		
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/request/incoming/{id}")
	public ResponseEntity<List<CertificateRequestDto>> getRequestsIssuedTo(@PathVariable Long id) {
		// TODO: Get user from JWT instead of using @PathVariable.
		User user  = userService.findById(id);	
		List<CertificateRequest> requests = certificateRequestService.findRequestsByUserResponsibleForThem(user);
		List<CertificateRequestDto> result = requests.stream().map(r -> modelMapper.map(r, CertificateRequestDto.class)).toList();
		
		return ResponseEntity.ok(result);
	}
	
	@GetMapping
	public ResponseEntity<List<CertificateSummaryItemDto>> getAllCertificates() {
		return ResponseEntity.ok(certificateService.getAllSummary());
	}
	
	
	@GetMapping("/valid/{id}")
	public ResponseEntity<?> isValid(@PathVariable Long id) {
		Certificate cert = certificateService.findById(id);
		boolean isValid = certificateService.isValid(cert);
		return new ResponseEntity<>(isValid, HttpStatus.OK);
	}
	
}
