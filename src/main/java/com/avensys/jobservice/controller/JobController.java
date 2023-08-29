package com.avensys.jobservice.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.avensys.jobservice.dto.JobRequest;
import com.avensys.jobservice.entity.JobEntity;
import com.avensys.jobservice.service.JobService;
import com.avensys.jobservice.util.ResponseUtil;

/**
 * @author Kotaiah nalleboina
 *  This class used to get/save/update/delete job operations
 *        
 */
@RestController
@RequestMapping("/api/job")
public class JobController {
	
	private static final Logger LOG = LoggerFactory.getLogger(JobController.class);
	@Autowired
	private JobService jobService;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private AuthenticationManager authenticationManager;

	/**
	 * This method is used to create a job
	 * 
	 * @param headers
	 * @param jobRequest
	 * @return
	 */
	@PostMapping
	public ResponseEntity<?> createJob(@RequestHeader Map<String, String> headers, @RequestBody JobRequest jobRequest) {
		LOG.info("createJob request received");
		Authentication authenticate = authenticationManager
				.authenticate(new BearerTokenAuthenticationToken(headers.get("Authorization")));
		if (authenticate.isAuthenticated()) {
			JobEntity jobEntity = jobService.createJob(jobRequest);
			return ResponseUtil.generateSuccessResponse(jobEntity, HttpStatus.CREATED,
					messageSource.getMessage("job.created", null, LocaleContextHolder.getLocale()));
		}
		return new ResponseEntity<>("Not Authorized", HttpStatus.UNAUTHORIZED);
	}
	
	
	/**
	 * This method is used to update a job
	 * @param headers
	 * @param id
	 * @param jobRequest
	 * @return
	 */

	@PutMapping("/{id}")
	public ResponseEntity<?> updateJob(@RequestHeader Map<String, String> headers, 
			@PathVariable Integer id, @RequestBody JobRequest jobRequest) {
		JobEntity jobEntity = jobService.updateJob(id, jobRequest);
		return ResponseUtil.generateSuccessResponse(jobEntity, HttpStatus.OK,
				messageSource.getMessage("job.updated", null, LocaleContextHolder.getLocale()));
	}
	
	
	/**
	 * This method is used to Delete a job
	 * @param headers
	 * @param id
	 * @return
	 */

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteJob(@RequestHeader Map<String, String> headers, @PathVariable Integer id) {
	
		jobService.deleteJob(id);
		return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK,
				messageSource.getMessage("job.deleted", null, LocaleContextHolder.getLocale()));
	}
	
	
	
	/**
	 *  This method is used to retrieve a job Information
	 * @param headers
	 * @param id
	 * @return
	 */

	@GetMapping("/get/{id}")
	public ResponseEntity<?> getJob(@RequestHeader Map<String, String> headers, @PathVariable Integer id) {
		JobEntity jobEntity = jobService.getJob(id);
		if(!ObjectUtils.isEmpty(jobEntity))
			return ResponseUtil.generateSuccessResponse(jobEntity, HttpStatus.OK,
				messageSource.getMessage("job.deleted", null, LocaleContextHolder.getLocale()));
		else
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.BAD_REQUEST,
					messageSource.getMessage("Job not found", null, LocaleContextHolder.getLocale()));
	}

}
