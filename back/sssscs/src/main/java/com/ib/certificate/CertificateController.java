package com.ib.certificate;

import java.time.LocalDateTime;
import java.util.List;

import com.ib.util.recaptcha.ReCAPTCHAUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ib.certificate.Certificate.Type;
import com.ib.certificate.dto.CertificateRequestCreateDto;
import com.ib.certificate.dto.CertificateRequestDto;
import com.ib.certificate.dto.CertificateSummaryItemDto;
import com.ib.certificate.request.CertificateRequest;
import com.ib.certificate.request.CertificateRequest.Status;
import com.ib.certificate.request.ICertificateRequestService;
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
	@Autowired
	private ReCAPTCHAUtil captchaUtil;
	
	private void setRequestValidFor(CertificateRequest req, long months) {
		LocalDateTime desired = LocalDateTime.now().plusMonths(months);
		
		if (req.getParent() != null) {
			if (desired.isAfter(req.getParent().getValidTo())) {
				desired = req.getParent().getValidTo().minusDays(1);
			}
		}
		req.setValidTo(desired);
	}

	private CertificateRequest requestFromCreateDto(CertificateRequestCreateDto dto) {
		CertificateRequest req = new CertificateRequest();
		req.setSubjectData(dto.getSubjectData());
		req.setType(dto.getType());

		req.setStatus(Status.PENDING);
		req.setCreator(auth.getUser());
		if (dto.getParentId() != null) {
			Certificate parent = certificateService.findById(dto.getParentId());
			req.setParent(parent);
		}

		if (dto.getType() == Type.ROOT) {
			setRequestValidFor(req, 12);
		} else if (dto.getType() == Type.INTERMEDIATE) {
			setRequestValidFor(req, 6);
		} else {
			setRequestValidFor(req, 2);
		}

		return req;
	}

	@PreAuthorize("hasAnyAuthority('ROLE_REGULAR', 'ROLE_ADMIN')")
	@PostMapping("/request")
	public ResponseEntity<?> makeRequest(@Valid @RequestBody CertificateRequestCreateDto requestCreateDto) {
		captchaUtil.processResponse(requestCreateDto.getToken());
		CertificateRequest req = requestFromCreateDto(requestCreateDto);
		req = certificateRequestService.makeRequest(req);

		if (certificateRequestService.canAutoAccept(req)) {
			User user = auth.getUser();
			certificateService.accept(req, user);
		}

		return new ResponseEntity<Void>((Void) null, HttpStatus.NO_CONTENT);
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
		return new ResponseEntity<Void>((Void) null, HttpStatus.NO_CONTENT);
	}

	@PreAuthorize("hasAnyAuthority('ROLE_REGULAR', 'ROLE_ADMIN')")
	@GetMapping("/request/created")
	public ResponseEntity<?> getOwnRequests() {
		User creator = auth.getUser();

		List<CertificateRequest> requests = certificateRequestService.findByCreator(creator);
		List<CertificateRequestDto> result = requests.stream().map(r -> modelMapper.map(r, CertificateRequestDto.class))
				.toList();

		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/request/all")
	public ResponseEntity<?> getAllRequests() {
		List<CertificateRequest> requests = certificateRequestService.findAll();
		List<CertificateRequestDto> result = requests.stream().map(r -> modelMapper.map(r, CertificateRequestDto.class))
				.toList();

		return new ResponseEntity<>(result, HttpStatus.OK);
	}	

	@PreAuthorize("hasAnyAuthority('ROLE_REGULAR', 'ROLE_ADMIN')")
	@GetMapping("/request/incoming")
	public ResponseEntity<?> getPendingRequestsIssuedTo() {
		User user = auth.getUser();

		List<CertificateRequest> requests = certificateRequestService.findPendingRequestsIssuedTo(user);
		List<CertificateRequestDto> result = requests.stream().map(r -> modelMapper.map(r, CertificateRequestDto.class))
				.toList();

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
	
	@PreAuthorize("hasAnyAuthority('ROLE_REGULAR', 'ROLE_ADMIN')")
	@PutMapping("/revoke/{certificateId}")
	public void revoke(@PathVariable Long certificateId, @RequestBody String revocationReason) {
		certificateService.revoke(certificateId, revocationReason);
	}

	@PreAuthorize("hasAnyAuthority('ROLE_REGULAR', 'ROLE_ADMIN')")
	@GetMapping("/download/{certificateId}")
	public ResponseEntity<FileSystemResource> download(@PathVariable Long certificateId) {
		return ResponseEntity.ok(certificateService.download(certificateId));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_REGULAR', 'ROLE_ADMIN')")
	@GetMapping("/download/private/{certificateId}")
	public ResponseEntity<FileSystemResource> downloadPrivate(@PathVariable Long certificateId) {
		return ResponseEntity.ok(certificateService.downloadPrivateKey(certificateId));
	}

	@PreAuthorize("hasAnyAuthority('ROLE_REGULAR', 'ROLE_ADMIN')")
	@PostMapping(value = "/valid")
	public ResponseEntity<Boolean> isValidFile(@RequestPart MultipartFile certFile, @RequestPart String token) {
		captchaUtil.processResponse(token);
		boolean isValid = false;
		isValid = certificateService.isValid(certFile);
		return ResponseEntity.ok(isValid);
	}
}