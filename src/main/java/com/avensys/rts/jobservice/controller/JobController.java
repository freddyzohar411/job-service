package com.avensys.rts.jobservice.controller;

import java.util.List;

import com.avensys.rts.jobservice.annotation.RequiresAllPermissions;
import com.avensys.rts.jobservice.enums.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avensys.rts.jobservice.annotation.RequiresAllPermissions;
import com.avensys.rts.jobservice.constant.MessageConstants;
import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.enums.Permission;
import com.avensys.rts.jobservice.exception.ServiceException;
import com.avensys.rts.jobservice.payload.JobListingRequestDTO;
import com.avensys.rts.jobservice.payload.JobRecruiterFODRequest;
import com.avensys.rts.jobservice.payload.JobRequest;
import com.avensys.rts.jobservice.service.JobRecruiterFODService;
import com.avensys.rts.jobservice.service.JobService;
import com.avensys.rts.jobservice.util.JwtUtil;
import com.avensys.rts.jobservice.util.ResponseUtil;

import jakarta.validation.Valid;

/**
 * @author Rahul Sahu This class used to get/save/update/delete job operations
 * 
 */
@CrossOrigin
@RestController
@RequestMapping("/api/job")
public class JobController {

	private static final Logger LOG = LoggerFactory.getLogger(JobController.class);

	@Autowired
	private JobService jobService;

	@Autowired
	private JobRecruiterFODService jobRecruiterFODService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private MessageSource messageSource;

	/**
	 * This method is used to create a job
	 * 
	 * @param headers
	 * @param jobRequest
	 * @return
	 */
	@RequiresAllPermissions({ Permission.JOB_WRITE })
	@PostMapping("/add")
	public ResponseEntity<?> createJob(@Valid @RequestBody JobRequest jobRequest,
			@RequestHeader(name = "Authorization") String token) {
		LOG.info("createJob request received");
		try {
			Long userId = jwtUtil.getUserId(token);
			jobRequest.setCreatedBy(userId);
			jobRequest.setUpdatedBy(userId);

			jobService.save(jobRequest);
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.CREATED,
					messageSource.getMessage(MessageConstants.MESSAGE_CREATED, null, LocaleContextHolder.getLocale()));
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	/**
	 * This method is used to update a job
	 * 
	 * @param headers
	 * @param id
	 * @param jobRequest
	 * @return
	 */

	@PutMapping("/{id}")
	@RequiresAllPermissions({ Permission.JOB_EDIT })
	public ResponseEntity<?> updateJob(@RequestBody JobRequest jobRequest,
			@RequestHeader(name = "Authorization") String token) {
		try {
			Long userId = jwtUtil.getUserId(token);
			jobRequest.setUpdatedBy(userId);
			jobService.update(jobRequest);
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK,
					messageSource.getMessage(MessageConstants.MESSAGE_UPDATED, null, LocaleContextHolder.getLocale()));
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
			jobService.delete(id);
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
			JobEntity jobEntity = jobService.getById(id);
			return ResponseUtil.generateSuccessResponse(jobEntity, HttpStatus.OK, null);
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	/**
	 * Get all accounts field for all forms related to accounts
	 * 
	 * @return
	 */
	@RequiresAllPermissions({ Permission.JOB_WRITE })
	@GetMapping("/fields")
	public ResponseEntity<Object> getAllAccountsFields(@RequestHeader(name = "Authorization") String token) {
		try {
			Long userId = jwtUtil.getUserId(token);
			return ResponseUtil.generateSuccessResponse(jobService.getAllJobFields(userId), HttpStatus.OK,
					messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@RequiresAllPermissions({ Permission.JOB_READ })
	@PostMapping("/listing")
	public ResponseEntity<Object> getJobListing(@RequestBody JobListingRequestDTO jobListingRequestDTO,
			@RequestHeader(name = "Authorization") String token) {
		Long userId = jwtUtil.getUserId(token);
		Integer page = jobListingRequestDTO.getPage();
		Integer pageSize = jobListingRequestDTO.getPageSize();
		String sortBy = jobListingRequestDTO.getSortBy();
		String sortDirection = jobListingRequestDTO.getSortDirection();
		String searchTerm = jobListingRequestDTO.getSearchTerm();
		List<String> searchFields = jobListingRequestDTO.getSearchFields();
		String jobType = jobListingRequestDTO.getJobType();
		if (searchTerm == null || searchTerm.isEmpty()) {
			return ResponseUtil.generateSuccessResponse(
					jobService.getJobListingPage(page, pageSize, sortBy, sortDirection, userId, jobType), HttpStatus.OK,
					messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
		} else {
			return ResponseUtil.generateSuccessResponse(
					jobService.getJobListingPageWithSearch(page, pageSize, sortBy, sortDirection, searchTerm,
							searchFields, userId, jobType),
					HttpStatus.OK,
					messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
		}
	}

	@RequiresAllPermissions({ Permission.JOB_READ })
	@PostMapping("/listing/all")
	public ResponseEntity<Object> getJobListingAll(@RequestBody JobListingRequestDTO jobListingRequestDTO,
			@RequestHeader(name = "Authorization") String token) {
		Long userId = jwtUtil.getUserId(token);
		Integer page = jobListingRequestDTO.getPage();
		Integer pageSize = jobListingRequestDTO.getPageSize();
		String sortBy = jobListingRequestDTO.getSortBy();
		String sortDirection = jobListingRequestDTO.getSortDirection();
		String searchTerm = jobListingRequestDTO.getSearchTerm();
		List<String> searchFields = jobListingRequestDTO.getSearchFields();
		String jobType = jobListingRequestDTO.getJobType();
		if (searchTerm == null || searchTerm.isEmpty()) {
			return ResponseUtil.generateSuccessResponse(
					jobService.getJobListingPage(page, pageSize, sortBy, sortDirection, userId, jobType), HttpStatus.OK,
					messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
		} else {
			return ResponseUtil.generateSuccessResponse(
					jobService.getJobListingPageWithSearch(page, pageSize, sortBy, sortDirection, searchTerm,
							searchFields, userId, jobType),
					HttpStatus.OK,
					messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
		}
	}

	@GetMapping
	public ResponseEntity<?> getAllJobs(@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize,
			@RequestParam(defaultValue = "modifiedTime") String sortBy) {
		LOG.info("getAllJobs request received");
		try {
			List<JobEntity> jobEntityList = jobService.getAllJobs(pageNo, pageSize, sortBy);
			return ResponseUtil.generateSuccessResponse(jobEntityList, HttpStatus.OK,
					messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("/search")
	public Page<JobEntity> searchJob(@RequestParam("search") String search, Pageable pageable) throws ServiceException {
		return jobService.search(search, pageable);
	}

	/**
	 * Get an account draft if exists
	 * 
	 * @return
	 */
	@RequiresAllPermissions({ Permission.JOB_WRITE })
	@GetMapping("/draft")
	public ResponseEntity<Object> getAccountIfDraft(@RequestHeader(name = "Authorization") String token) {
		try {
			Long userId = jwtUtil.getUserId(token);
			JobEntity jobEntity = jobService.getJobDraft(userId);
			return ResponseUtil.generateSuccessResponse(jobEntity, HttpStatus.OK,
					messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	/**
	 * Save a job fod
	 * 
	 * @return
	 */
	@RequiresAllPermissions({ Permission.JOB_WRITE })
	@PostMapping("/jobfod")
	public ResponseEntity<?> jobFOD(@Valid @RequestBody JobRecruiterFODRequest jobRecruiterFODRequest,
			@RequestHeader(name = "Authorization") String token) {
		LOG.info("jobFOD request received");
		try {
			Long userId = jwtUtil.getUserId(token);
			jobRecruiterFODRequest.setSellerId(userId);
			jobRecruiterFODRequest.setCreatedBy(userId);
			jobRecruiterFODRequest.setUpdatedBy(userId);

			jobRecruiterFODService.save(jobRecruiterFODRequest);
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.CREATED,
					messageSource.getMessage("jobfod.created", null, LocaleContextHolder.getLocale()));
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	/**
	 * Get job data
	 * 
	 * @param jobId
	 * @return
	 */
	@GetMapping("/{jobId}/data")
	public ResponseEntity<Object> getJobByIdData(@PathVariable Integer jobId) {
		LOG.info("Job get by id data: Controller");
		return ResponseUtil.generateSuccessResponse(jobService.getJobByIdData(jobId), HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	/**
	 * Get all job fields including all related microservices
	 * 
	 * @return
	 */
	@GetMapping("/fields/all")
	public ResponseEntity<Object> getAllJobsFieldsAll() {
		LOG.info("Job get by id data: Controller");
		return ResponseUtil.generateSuccessResponse(jobService.getAllJobsFieldsAll(), HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	/**
	 * Get job data including all related microservices
	 * 
	 * @param jobId
	 * @return
	 */
	@GetMapping("/{jobId}/data/all")
	public ResponseEntity<Object> getJobByIdDataAll(@PathVariable Long jobId) {
		LOG.info("Job get by id data: Controller");
		return ResponseUtil.generateSuccessResponse(jobService.getJobByIdDataAll(jobId), HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

}
