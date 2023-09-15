package com.avensys.rts.jobservice.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avensys.rts.jobservice.constant.MessageConstants;
import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.payloadrequest.JobRequest;
import com.avensys.rts.jobservice.service.JobService;
import com.avensys.rts.jobservice.util.ResponseUtil;

import jakarta.validation.Valid;

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

	/**
	 * This method is used to create a job
	 * 
	 * @param headers
	 * @param jobRequest
	 * @return
	 */
	@PostMapping
	public ResponseEntity<?> createJob(@RequestHeader Map<String, String> headers, @Valid @ModelAttribute JobRequest jobRequest) {
		LOG.info("createJob request received");

			JobEntity jobEntity = jobService.createJob(jobRequest);
			return ResponseUtil.generateSuccessResponse(jobEntity, HttpStatus.CREATED,
					messageSource.getMessage(MessageConstants.MESSAGE_CREATED, null, LocaleContextHolder.getLocale()));
		
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
				messageSource.getMessage(MessageConstants.MESSAGE_UPDATED, null, LocaleContextHolder.getLocale()));
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
				messageSource.getMessage(MessageConstants.MESSAGE_DELETED, null, LocaleContextHolder.getLocale()));
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
		return ResponseUtil.generateSuccessResponse(jobEntity, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}
	
	@GetMapping
	public ResponseEntity<?> getAllJobs (
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "modifiedTime") String sortBy){
		LOG.info("getAllJobs request received");
		List<JobEntity> jobEntityList = jobService.getAllJobs(pageNo, pageSize, sortBy);
		return ResponseUtil.generateSuccessResponse(jobEntityList, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}
	@GetMapping("/search")
	public Page<JobEntity> searchJob(@RequestParam("search") String search, Pageable pageable) {
		return jobService.search(search, pageable);
	}
	


}
