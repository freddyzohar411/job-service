package com.avensys.rts.jobservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.avensys.rts.jobservice.annotation.RequiresAllPermissions;
import com.avensys.rts.jobservice.entity.JobStageEntity;
import com.avensys.rts.jobservice.enums.Permission;
import com.avensys.rts.jobservice.exception.ServiceException;
import com.avensys.rts.jobservice.service.JobStageService;
import com.avensys.rts.jobservice.util.JwtUtil;
import com.avensys.rts.jobservice.util.ResponseUtil;

import jakarta.validation.Valid;

/**
 * @author Rahul Sahu This class used to get/save/update/delete job operations
 * 
 */
@CrossOrigin
@RestController
@RequestMapping("/api/jobstage")
public class JobStageController {

	private static final Logger LOG = LoggerFactory.getLogger(JobStageController.class);

	@Autowired
	private JobStageService jobStageService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private MessageSource messageSource;

	/**
	 * This method is used to create a job
	 * 
	 * @param headers
	 * @param jobStageEntity
	 * @return
	 */
	@RequiresAllPermissions({ Permission.JOB_WRITE })
	@PostMapping
	public ResponseEntity<?> create(@Valid @RequestBody JobStageEntity jobStageEntity,
			@RequestHeader(name = "Authorization") String token) {
		LOG.info("create request received");
		try {
			Long userId = jwtUtil.getUserId(token);
			jobStageEntity.setCreatedBy(userId);
			jobStageEntity.setUpdatedBy(userId);

			jobStageService.save(jobStageEntity);
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.CREATED,
					messageSource.getMessage("job.stage.created", null, LocaleContextHolder.getLocale()));
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	/**
	 * This method is used to update a job
	 * 
	 * @param headers
	 * @param id
	 * @param jobStageEntity
	 * @return
	 */

	@PutMapping
	@RequiresAllPermissions({ Permission.JOB_EDIT })
	public ResponseEntity<?> updateJob(@RequestBody JobStageEntity jobStageEntity,
			@RequestHeader(name = "Authorization") String token) {
		try {
			Long userId = jwtUtil.getUserId(token);
			jobStageEntity.setUpdatedBy(userId);
			jobStageService.update(jobStageEntity);
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK,
					messageSource.getMessage("job.stage.updated", null, LocaleContextHolder.getLocale()));
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.NOT_FOUND, e.getMessage());
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
			jobStageService.delete(id);
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK,
					messageSource.getMessage("job.stage.deleted", null, LocaleContextHolder.getLocale()));
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
			JobStageEntity jobStageEntity = jobStageService.getById(id);
			return ResponseUtil.generateSuccessResponse(jobStageEntity, HttpStatus.OK, null);
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<?> getAll() {
		LOG.info("getAll request received");
		try {
			List<JobStageEntity> jobEntityList = jobStageService.getAll();
			return ResponseUtil.generateSuccessResponse(jobEntityList, HttpStatus.OK,
					messageSource.getMessage("ob.stage.success", null, LocaleContextHolder.getLocale()));
		} catch (Exception e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

}
