package com.avensys.jobservice.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.avensys.jobservice.dto.JobRequest;
import com.avensys.jobservice.entity.JobEntity;
import com.avensys.jobservice.service.JobService;
import com.avensys.jobservice.util.ResponseUtil;

/**
 * This class used to get/save/update/delete job operations
 */
@RestController
@RequestMapping("/api/job")
public class JobController {
	private final JobService jobService;

	private final MessageSource messageSource;

	private final AuthenticationManager authenticationManager;

	public JobController(JobService jobService, MessageSource messageSource,
			AuthenticationManager authenticationManager) {
		this.jobService = jobService;
		this.messageSource = messageSource;
		this.authenticationManager = authenticationManager;
	}

	/**
	 * This method is used to create a job
	 * 
	 * @param headers
	 * @param jobRequest
	 * @return
	 */
	@PostMapping
	public ResponseEntity<?> createJob(@RequestHeader Map<String, String> headers, @RequestBody JobRequest jobRequest) {
	
		Authentication authenticate = authenticationManager
				.authenticate(new BearerTokenAuthenticationToken(headers.get("Authorization")));
		if (authenticate.isAuthenticated()) {
			JobEntity jobEntity = jobService.createJob(jobRequest);
			return ResponseUtil.generateSuccessResponse(jobEntity, HttpStatus.CREATED,
					messageSource.getMessage("job.created", null, LocaleContextHolder.getLocale()));
		}
		return new ResponseEntity<>("Not Authorized", HttpStatus.UNAUTHORIZED);
	}
}
