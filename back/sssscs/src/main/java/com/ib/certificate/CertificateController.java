package com.ib.certificate;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.ib.util.security.IAuthenticationFacade;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cert")
public class CertificateController {
	@Autowired
	private ICertificateService certificateService;
	@Autowired
	private ICertificateRequestService certificateRequestService;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
    private IAuthenticationFacade auth;

	
	private CertificateRequest requestFromCreateDto(CertificateRequestCreateDto dto) {
		CertificateRequest req = new CertificateRequest();
		req.setSubjectData(dto.getSubjectData());
		req.setType(dto.getType());
		req.setValidTo(dto.getValidTo());
		req.setStatus(Status.PENDING);
		req.setCreator(auth.getUser());
		if (dto.getParentId() != null) {
			Certificate parent = certificateService.findById(dto.getParentId());
			req.setParent(parent);
		}
		return req;
	}
	
	@PreAuthorize("hasAnyAuthority('ROLE_REGULAR', 'ROLE_ADMIN')")
	@PostMapping("/request")
	public ResponseEntity<?> makeRequest(@Valid @RequestBody CertificateRequestCreateDto requestCreateDto) {
		CertificateRequest req = requestFromCreateDto(requestCreateDto);
		req = certificateRequestService.makeRequest(req);
		
		if (certificateRequestService.canAutoAccept(req)) {
			User user = auth.getUser();
			certificateService.accept(req, user);
		}
		
		return new ResponseEntity<Void>((Void)null, HttpStatus.NO_CONTENT);
	}
	
	@PreAuthorize("hasAnyAuthority('ROLE_REGULAR', 'ROLE_ADMIN')")
	@PutMapping("/request/accept/{id}")
	public ResponseEntity<String> acceptRequest(@PathVariable Long id) {
		CertificateRequest req = certificateRequestService.findByIdAndStatus(id, Status.PENDING);
		User user = auth.getUser();
		
		Certificate cert = certificateService.accept(req, user);
		return ResponseEntity.ok(cert.toString());
	}
	
	@PreAuthorize("hasAnyAuthority('ROLE_REGULAR', 'ROLE_ADMIN')")
	@PutMapping("/request/reject/{id}")
	public ResponseEntity<?> rejectRequest(@PathVariable Long id, @RequestBody String reason) {
		CertificateRequest req = certificateRequestService.findByIdAndStatus(id, Status.PENDING);
		User user = auth.getUser();
		
		certificateService.reject(req, reason, user);
		return new ResponseEntity<Void>((Void)null, HttpStatus.NO_CONTENT);
	}
	
	@PreAuthorize("hasAnyAuthority('ROLE_REGULAR', 'ROLE_ADMIN')")
	@GetMapping("/request/created")
	public ResponseEntity<?> getOwnRequests() {
		User creator = auth.getUser();
		
		List<CertificateRequest> requests = certificateRequestService.findByCreator(creator);
		List<CertificateRequestDto> result = requests.stream().map(r -> modelMapper.map(r, CertificateRequestDto.class)).toList();
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyAuthority('ROLE_REGULAR', 'ROLE_ADMIN')")
	@GetMapping("/request/incoming")
	public ResponseEntity<?> getRequestsIssuedTo() {
		User user = auth.getUser();
		
		List<CertificateRequest> requests = certificateRequestService.findRequestsByUserResponsibleForThem(user);
		List<CertificateRequestDto> result = requests.stream().map(r -> modelMapper.map(r, CertificateRequestDto.class)).toList();
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyAuthority('ROLE_REGULAR', 'ROLE_ADMIN')")
	@GetMapping
	public ResponseEntity<List<CertificateSummaryItemDto>> getAllCertificates() {
		return ResponseEntity.ok(certificateService.getAllSummary());
	}
	
	@PreAuthorize("hasAnyAuthority('ROLE_REGULAR', 'ROLE_ADMIN')")
	@GetMapping("/valid/{id}")
	public ResponseEntity<?> isValid(@PathVariable Long id) {
		Certificate cert = certificateService.findById(id);
		boolean isValid = certificateService.isValid(cert);
		return new ResponseEntity<>(isValid, HttpStatus.OK);
	}
}