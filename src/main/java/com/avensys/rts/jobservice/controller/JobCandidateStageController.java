package com.avensys.rts.jobservice.controller;

import java.util.List;

import com.avensys.rts.jobservice.payload.JobCandidateStageWithAttachmentsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.avensys.rts.jobservice.annotation.RequiresAllPermissions;
import com.avensys.rts.jobservice.constant.MessageConstants;
import com.avensys.rts.jobservice.entity.JobCandidateStageEntity;
import com.avensys.rts.jobservice.enums.Permission;
import com.avensys.rts.jobservice.exception.ServiceException;
import com.avensys.rts.jobservice.payload.JobCandidateStageRequest;
import com.avensys.rts.jobservice.service.JobCandidateStageService;
import com.avensys.rts.jobservice.util.JwtUtil;
import com.avensys.rts.jobservice.util.ResponseUtil;

import jakarta.validation.Valid;

/**
 * @author Rahul Sahu This class used to get/save/update/delete job operations
 * 
 */
@CrossOrigin
@RestController
@RequestMapping("/api/jobcandidatestage")
public class JobCandidateStageController {

	private static final Logger LOG = LoggerFactory.getLogger(JobCandidateStageController.class);

	@Autowired
	private JobCandidateStageService jobCandidateStageService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private MessageSource messageSource;

	/**
	 * This method is used to tag a job
	 * 
	 * @param headers
	 * @param jobCandidateStageRequest
	 * @return
	 */
	@RequiresAllPermissions({ Permission.JOB_WRITE })
	@PostMapping
	public ResponseEntity<?> create(@Valid @RequestBody JobCandidateStageRequest jobCandidateStageRequest,
			@RequestHeader(name = "Authorization") String token) {
		LOG.info("create request received");
		try {
			Long userId = jwtUtil.getUserId(token);
			jobCandidateStageRequest.setCreatedBy(userId);
			jobCandidateStageRequest.setUpdatedBy(userId);

			jobCandidateStageService.save(jobCandidateStageRequest);
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.CREATED,
					messageSource.getMessage("job.tag.created", null, LocaleContextHolder.getLocale()));
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@RequiresAllPermissions({ Permission.JOB_WRITE })
	@PostMapping("create-with-attachments")
	public ResponseEntity<?> createWithAttachments(@ModelAttribute JobCandidateStageWithAttachmentsRequest jobCandidateStageWithAttachmentsRequest,
			@RequestHeader(name = "Authorization") String token) {
		LOG.info("create request received");
		try {
			Long userId = jwtUtil.getUserId(token);
			jobCandidateStageWithAttachmentsRequest.setCreatedBy(userId);
			jobCandidateStageWithAttachmentsRequest.setUpdatedBy(userId);

			jobCandidateStageService.saveWithAttachments(jobCandidateStageWithAttachmentsRequest);
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.CREATED,
					messageSource.getMessage("job.tag.created", null, LocaleContextHolder.getLocale()));
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@RequiresAllPermissions({ Permission.JOB_WRITE })
	@PostMapping("createAll")
	public ResponseEntity<?> createAll(@Valid @RequestBody JobCandidateStageRequest[] jobCandidateStageRequests,
			@RequestHeader(name = "Authorization") String token) {
		LOG.info("create all request received");
		try {
			Long userId = jwtUtil.getUserId(token);
			if (jobCandidateStageRequests != null && jobCandidateStageRequests.length > 0) {
				for (JobCandidateStageRequest jobCandidateStageRequest : jobCandidateStageRequests) {
					jobCandidateStageRequest.setCreatedBy(userId);
					jobCandidateStageRequest.setUpdatedBy(userId);
					jobCandidateStageService.save(jobCandidateStageRequest);
				}
			}
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.CREATED,
					messageSource.getMessage("job.tag.created", null, LocaleContextHolder.getLocale()));
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	/**
	 * This method is used to Delete a job
	 * 
	 * @param headers
	 * @param id
	 * @return
	 */
	@RequiresAllPermissions({ Permission.JOB_DELETE })
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteJob(@PathVariable Long id) {
		try {
			jobCandidateStageService.delete(id);
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK,
					messageSource.getMessage(MessageConstants.MESSAGE_DELETED, null, LocaleContextHolder.getLocale()));
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	/**
	 * This method is used to retrieve a job Information
	 * 
	 * @param headers
	 * @param id
	 * @return
	 */
	@GetMapping("/{id}")
	public ResponseEntity<?> find(@PathVariable Long id) {
		try {
			JobCandidateStageEntity entity = jobCandidateStageService.getById(id);
			return ResponseUtil.generateSuccessResponse(entity, HttpStatus.OK, null);
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<?> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize,
			@RequestParam(defaultValue = "modifiedTime") String sortBy) {
		LOG.info("getAllJobs request received");
		try {
			List<JobCandidateStageEntity> jobEntityList = jobCandidateStageService.getAll(pageNo, pageSize, sortBy);
			return ResponseUtil.generateSuccessResponse(jobEntityList, HttpStatus.OK,
					messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

}
