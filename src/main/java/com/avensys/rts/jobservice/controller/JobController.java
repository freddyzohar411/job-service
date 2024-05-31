package com.avensys.rts.jobservice.controller;

import java.util.List;

import com.avensys.rts.jobservice.util.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import com.avensys.rts.jobservice.entity.TosEntity;
import com.avensys.rts.jobservice.enums.Permission;
import com.avensys.rts.jobservice.exception.ServiceException;
import com.avensys.rts.jobservice.payload.CustomFieldsRequestDTO;
import com.avensys.rts.jobservice.payload.JobListingDeleteRequestDTO;
import com.avensys.rts.jobservice.payload.JobListingRequestDTO;
import com.avensys.rts.jobservice.payload.JobRecruiterFODRequest;
import com.avensys.rts.jobservice.payload.JobRequest;
import com.avensys.rts.jobservice.payload.TosRequestDTO;
import com.avensys.rts.jobservice.response.CustomFieldsResponseDTO;
import com.avensys.rts.jobservice.service.JobRecruiterFODService;
import com.avensys.rts.jobservice.service.JobService;

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

	@Autowired
	private UserUtil userUtil;

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
			return ResponseUtil.generateSuccessResponse(jobService.save(jobRequest), HttpStatus.CREATED,
					messageSource.getMessage(MessageConstants.MESSAGE_CREATED, null, LocaleContextHolder.getLocale()));
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	/**
	 * This method is used to clone a job from existing job
	 * 
	 * @param headers
	 * @param jobRequest
	 * @return
	 */
	@RequiresAllPermissions({ Permission.JOB_WRITE })
	@PostMapping("/clone")
	public ResponseEntity<?> cloneJob(@Valid @RequestBody JobRequest jobRequest,
			@RequestHeader(name = "Authorization") String token) {
		LOG.info("cloneJob request received");
		try {
			Long userId = jwtUtil.getUserId(token);
			jobRequest.setCreatedBy(userId);
			jobRequest.setUpdatedBy(userId);
			Long id = jobRequest.getId();
			JobEntity jobEntity = jobService.getById(id);
			JobRequest cloneJobRequest = jobEntityToJobRequest(jobEntity);
			String jobDataClone = cloneJobRequest.getFormData();
			JsonNode jobDataNodeClone = MappingUtil.convertJSONStringToJsonNode(jobDataClone);
			ObjectNode objectNodeClone = (ObjectNode) jobDataNodeClone;
			if (jobDataNodeClone.isObject()) {
				objectNodeClone.put("jobId", jobRequest.getCloneJobId());
			}
			cloneJobRequest.setFormData(MappingUtil.convertJsonNodeToJSONString(objectNodeClone));
			JobEntity cloneJobEntity = jobService.save(cloneJobRequest);
			return ResponseUtil.generateSuccessResponse(cloneJobEntity, HttpStatus.CREATED,
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
	 * This method is used to Delete a job
	 * 
	 * @param headers
	 * @param id
	 * @return
	 */
	@RequiresAllPermissions({ Permission.JOB_DELETE })
	@DeleteMapping("/deleteFOD/{id}")
	public ResponseEntity<?> deleteFOD(@PathVariable Long id) {
		try {
			jobRecruiterFODService.deleteByJobId(id);
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
		String email = JwtUtil.getEmailFromContext();
		Boolean isAdmin = userUtil.checkIsAdmin();
		jobListingRequestDTO.setUserId(userId);
		jobListingRequestDTO.setEmail(email);
		jobListingRequestDTO.setGetAll(isAdmin);

		if (jobListingRequestDTO.getSearchTerm() == null || jobListingRequestDTO.getSearchTerm().isEmpty()) {
			return ResponseUtil.generateSuccessResponse(jobService.getJobListingPage(jobListingRequestDTO),
					HttpStatus.OK,
					messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
		} else {
			return ResponseUtil.generateSuccessResponse(jobService.getJobListingPageWithSearch(jobListingRequestDTO),
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

	/*
	 * save all the fields in the custom view
	 */
	@PostMapping("/save/customfields")
	public ResponseEntity<Object> saveCustomFields(@Valid @RequestBody CustomFieldsRequestDTO customFieldsRequestDTO,
			@RequestHeader(name = "Authorization") String token) {
		LOG.info("Save Job customFields: Controller");
		try {
			Long userId = jwtUtil.getUserId(token);
			customFieldsRequestDTO.setCreatedBy(userId);
			customFieldsRequestDTO.setUpdatedBy(userId);
			CustomFieldsResponseDTO customFieldsResponseDTO = jobService.saveCustomFields(customFieldsRequestDTO);
			return ResponseUtil.generateSuccessResponse(customFieldsResponseDTO, HttpStatus.CREATED,
					messageSource.getMessage(MessageConstants.JOB_CUSTOM_VIEW, null, LocaleContextHolder.getLocale()));
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.BAD_REQUEST, e.getMessage());
		}

	}

	@GetMapping("/customView/all")
	public ResponseEntity<Object> getAllCreatedCustomViews(@RequestHeader(name = "Authorization") String token) {
		LOG.info("Job get all custom views: Controller");
		Long userId = jwtUtil.getUserId(token);
		return ResponseUtil.generateSuccessResponse(jobService.getAllCreatedCustomViews(userId), HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	@PutMapping("/customView/update/{id}")
	public ResponseEntity<Object> updateCustomView(@PathVariable Long id,
			@RequestHeader(name = "Authorization") String token) {
		LOG.info("Job custom view update: Controller");
		try {
			Long userId = jwtUtil.getUserId(token);
			CustomFieldsResponseDTO response = jobService.updateCustomView(id, userId);
			return ResponseUtil.generateSuccessResponse(response, HttpStatus.OK, messageSource
					.getMessage(MessageConstants.JOB_CUSTOM_VIEW_UPDATED, null, LocaleContextHolder.getLocale()));
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.BAD_REQUEST, e.getMessage());
		}

	}

	@DeleteMapping("/customView/delete/{id}")
	public ResponseEntity<?> softDeleteCustomView(@PathVariable Long id) {
		try {
			jobService.softDelete(id);
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK, messageSource
					.getMessage(MessageConstants.JOB_CUSTOM_VIEW_DELETED, null, LocaleContextHolder.getLocale()));
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("/clone/{jobId}")
	public ResponseEntity<Object> getJobById(@PathVariable Integer jobId) {
		LOG.info("Job get by id data: Controller");
		return ResponseUtil.generateSuccessResponse(jobService.getJobByIdData(jobId), HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	public JobRequest jobEntityToJobRequest(JobEntity jobEntity) {
		JobRequest jobRequest = new JobRequest();
		jobRequest.setTitle(jobEntity.getTitle());
		jobRequest.setFormData(jobEntity.getJobSubmissionData().toString());
		jobRequest.setFormId(jobEntity.getFormId());
		jobRequest.setCreatedBy(jobEntity.getCreatedBy());
		jobRequest.setIsDraft(jobEntity.getIsDraft());
		jobRequest.setUpdatedBy(jobEntity.getUpdatedBy());
		jobRequest.setClone(true);
		return jobRequest;
	}

	/**
	 * Get job embedding by id and type (default or openAI)
	 * 
	 * @param jobId
	 * @param type
	 * @return
	 */
	@GetMapping("/{jobId}/embeddings/get/{type}")
	public ResponseEntity<Object> getEmbeddingsById(@PathVariable Long jobId, @PathVariable String type) {
		LOG.info("Get embeddings by id: Controller");
		return ResponseUtil.generateSuccessResponse(jobService.getJobEmbeddingsById(jobId, type), HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	/**
	 * Update/create embeddings by id
	 * 
	 * @param jobId
	 * @return
	 */
	@GetMapping("/{jobId}/embeddings/create")
	public ResponseEntity<Object> updateEmbeddingsById(@PathVariable Long jobId) {
		LOG.info("Create/Update embeddings by id: Controller");
		return ResponseUtil.generateSuccessResponse(jobService.updateJobEmbeddings(jobId), HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	@GetMapping("create-embeddings/all")
	public ResponseEntity<Object> updateAllEmbeddings() {
		LOG.info("Create/Update all embeddings: Controller");
		jobService.updateJobEmbeddingsAll();
		return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	@PostMapping("/listing/delete")
	public ResponseEntity<Object> deleteJobListing(@RequestBody JobListingDeleteRequestDTO jobListingDeleteRequestDTO) {
		LOG.info("Job listing delete: Controller");
		jobService.softDeleteJobs(jobListingDeleteRequestDTO);
		return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
	}

	/**
	 * This method is used to Prepare TOS.
	 * 
	 * @param tosRequestDTO
	 * @param token
	 * @return
	 */
	@PostMapping("/tos/create")
	public ResponseEntity<?> createTos(@Valid @RequestBody TosRequestDTO tosRequestDTO,
			@RequestHeader(name = "Authorization") String token) {
		LOG.info("create Tos request received");
		try {
			Long userId = jwtUtil.getUserId(token);
			tosRequestDTO.setCreatedBy(userId);
			tosRequestDTO.setUpdatedBy(userId);
			return ResponseUtil.generateSuccessResponse(jobService.saveTos(tosRequestDTO), HttpStatus.CREATED,
					messageSource.getMessage(MessageConstants.TOS_MESSAGE_CREATED, null,
							LocaleContextHolder.getLocale()));
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	/**
	 * This method is used to Update the TOS.
	 * 
	 * @param tosRequestDTO
	 * @param token
	 * @return
	 */
	@PutMapping("/tos/{id}")
	public ResponseEntity<?> updateTos(@RequestBody TosRequestDTO tosRequestDTO,
			@RequestHeader(name = "Authorization") String token) {
		LOG.info("update Tos request received");
		try {
			Long userId = jwtUtil.getUserId(token);
			tosRequestDTO.setUpdatedBy(userId);
			jobService.updateTos(tosRequestDTO);
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK, messageSource
					.getMessage(MessageConstants.TOS_MESSAGE_UPDATED, null, LocaleContextHolder.getLocale()));
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	/**
	 * This method is used to Delete the TOS.
	 * 
	 * @param id
	 * @return
	 */
	@DeleteMapping("/tos/{id}")
	public ResponseEntity<?> deleteTos(@PathVariable Long id) {
		LOG.info("delete Tos request received");
		try {
			jobService.deleteTos(id);
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.OK, messageSource
					.getMessage(MessageConstants.TOS_MESSAGE_DELETED, null, LocaleContextHolder.getLocale()));
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	/**
	 * This method is used to retrieve a TOS Information.
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/tos/{id}")
	public ResponseEntity<?> findTos(@PathVariable Long id) {
		LOG.info("find Tos request received");
		try {
			TosEntity tosEntity = jobService.getByTosId(id);
			return ResponseUtil.generateSuccessResponse(tosEntity, HttpStatus.OK, null);
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

}
