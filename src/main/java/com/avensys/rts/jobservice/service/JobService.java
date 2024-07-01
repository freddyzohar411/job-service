package com.avensys.rts.jobservice.service;

import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.avensys.rts.jobservice.apiclient.EmailAPIClient;
import com.avensys.rts.jobservice.apiclient.EmbeddingAPIClient;
import com.avensys.rts.jobservice.apiclient.FormSubmissionAPIClient;
import com.avensys.rts.jobservice.apiclient.UserAPIClient;
import com.avensys.rts.jobservice.entity.AccountEntity;
import com.avensys.rts.jobservice.entity.CandidateEntity;
import com.avensys.rts.jobservice.entity.CustomFieldsEntity;
import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.entity.TosEntity;
import com.avensys.rts.jobservice.entity.UserEntity;
import com.avensys.rts.jobservice.exception.DuplicateResourceException;
import com.avensys.rts.jobservice.exception.ServiceException;
import com.avensys.rts.jobservice.model.FieldInformation;
import com.avensys.rts.jobservice.model.JobExtraData;
import com.avensys.rts.jobservice.payload.CustomFieldsRequestDTO;
import com.avensys.rts.jobservice.payload.EmailMultiTemplateRequestDTO;
import com.avensys.rts.jobservice.payload.EmbeddingRequestDTO;
import com.avensys.rts.jobservice.payload.FilterDTO;
import com.avensys.rts.jobservice.payload.FormSubmissionsRequestDTO;
import com.avensys.rts.jobservice.payload.JobListingDeleteRequestDTO;
import com.avensys.rts.jobservice.payload.JobListingRequestDTO;
import com.avensys.rts.jobservice.payload.JobRequest;
import com.avensys.rts.jobservice.payload.TosRequestDTO;
import com.avensys.rts.jobservice.repository.AccountRepository;
import com.avensys.rts.jobservice.repository.CandidateRepository;
import com.avensys.rts.jobservice.repository.JobCustomFieldsRepository;
import com.avensys.rts.jobservice.repository.JobRepository;
import com.avensys.rts.jobservice.repository.TempRepository;
import com.avensys.rts.jobservice.repository.TosRepository;
import com.avensys.rts.jobservice.repository.UserRepository;
import com.avensys.rts.jobservice.response.CustomFieldsResponseDTO;
import com.avensys.rts.jobservice.response.EmbeddingResponseDTO;
import com.avensys.rts.jobservice.response.FormSubmissionsResponseDTO;
import com.avensys.rts.jobservice.response.HttpResponse;
import com.avensys.rts.jobservice.response.JobListingDataDTO;
import com.avensys.rts.jobservice.response.JobListingResponseDTO;
import com.avensys.rts.jobservice.response.UserResponseDTO;
import com.avensys.rts.jobservice.search.job.JobSpecificationBuilder;
import com.avensys.rts.jobservice.util.JSONUtil;
import com.avensys.rts.jobservice.util.JobDataExtractionUtil;
import com.avensys.rts.jobservice.util.JobUtil;
import com.avensys.rts.jobservice.util.JwtUtil;
import com.avensys.rts.jobservice.util.MappingUtil;
import com.avensys.rts.jobservice.util.StringUtil;
import com.avensys.rts.jobservice.util.TextProcessingUtil;
import com.avensys.rts.jobservice.util.UserUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author Rahul Sahu
 * 
 */
@AllArgsConstructor
@NoArgsConstructor
@Service
@Transactional
public class JobService {

	private static final Logger LOG = LoggerFactory.getLogger(JobService.class);
	private static final String JOB_TYPE = "job";
	private static final String JOB_DOCUMENT = "job_document";
	private static final String TOS_TYPE = "tos";

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private CandidateRepository candidateRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private FormSubmissionAPIClient formSubmissionAPIClient;

	@Autowired
	private JobCustomFieldsRepository jobCustomFieldsRepository;

	@Autowired
	private TosRepository tosRepository;

	@Autowired
	private UserAPIClient userAPIClient;

	@Autowired
	private UserUtil userUtil;

	@Autowired
	private EmbeddingAPIClient embeddingAPIClient;

	@Autowired
	private EmailAPIClient emailAPIClient;

	@Autowired
	private TempRepository tempRepository;

	public JobService(JobRepository jobRepository, FormSubmissionAPIClient formSubmissionAPIClient,
			UserAPIClient userAPIClient) {
		this.jobRepository = jobRepository;
		this.formSubmissionAPIClient = formSubmissionAPIClient;
		this.userAPIClient = userAPIClient;
	}

	/**
	 * This method is used to convert JobRequest to JobEntity
	 * 
	 * @param jobRequest
	 * @param jobEntity
	 * @return
	 */
	private JobEntity mapRequestToEntity(JobRequest jobRequest) {
		JobEntity entity = new JobEntity();
		if (jobRequest.getId() != null) {
			entity.setId(jobRequest.getId());
		}
		entity.setCreatedBy(jobRequest.getCreatedBy());
		entity.setTitle(jobRequest.getTitle());
		entity.setUpdatedBy(jobRequest.getUpdatedBy());
		entity.setFormId(jobRequest.getFormId());
		entity.setIsDraft(jobRequest.getIsDraft());
		entity.setIsActive(true);
		entity.setIsDeleted(false);
		return entity;
	}

	/**
	 * This method is used to convert TosRequest to TosEntity
	 * 
	 * @param tosRequestDTO
	 * @return
	 */
	private TosEntity mapTosRequestToTosEntity(TosRequestDTO tosRequestDTO) {
		TosEntity entity = new TosEntity();
		if (tosRequestDTO.getId() != null) {
			entity.setId(tosRequestDTO.getId());
		}
		Optional<JobEntity> jobOptional = jobRepository.findById(tosRequestDTO.getJobId());
		entity.setJobEnity(jobOptional.get());
		Optional<CandidateEntity> candidateOptional = candidateRepository.findById(tosRequestDTO.getCandidateId());
		entity.setCandidate(candidateOptional.get());
		Optional<UserEntity> sellerOptional = userRepository.findById(tosRequestDTO.getSalesUserId());
		entity.setSeller(sellerOptional.get());
		entity.setCreatedBy(tosRequestDTO.getCreatedBy());
		entity.setStatus(tosRequestDTO.getStatus());
		entity.setUpdatedBy(tosRequestDTO.getUpdatedBy());
		entity.setFormId(tosRequestDTO.getFormId());
		entity.setIsActive(true);
		entity.setIsDeleted(false);
		return entity;
	}

	/**
	 * This method is used to save job Need to implement roll back if error occurs.
	 * 
	 * @param jobRequest
	 * @return
	 */
	@Transactional
	public JobEntity save(JobRequest jobRequest) throws ServiceException {
		// add check for title exists in a DB

		JobEntity jobEntity = mapRequestToEntity(jobRequest);

		jobEntity = jobRepository.save(jobEntity);

		FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
		formSubmissionsRequestDTO.setUserId(jobRequest.getCreatedBy());
		formSubmissionsRequestDTO.setFormId(jobRequest.getFormId());
		formSubmissionsRequestDTO.setSubmissionData(MappingUtil.convertJSONStringToJsonNode(jobRequest.getFormData()));
		formSubmissionsRequestDTO.setEntityId(jobEntity.getId());
		formSubmissionsRequestDTO.setEntityType(JOB_TYPE);

		HttpResponse formSubmissionResponse = formSubmissionAPIClient.addFormSubmission(formSubmissionsRequestDTO);
		FormSubmissionsResponseDTO formSubmissionData = MappingUtil
				.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

		JsonNode submittedData = formSubmissionsRequestDTO.getSubmissionData();

		if (submittedData.get("accountOwner").asText() != null) {
			String accountOwnerEmail = submittedData.get("accountOwner").asText();
			accountOwnerEmail = accountOwnerEmail.substring(accountOwnerEmail.lastIndexOf("(")).replace("(", "")
					.replace(")", "");
			userRepository.findByEmail(accountOwnerEmail)
					.ifPresent(usr -> ((ObjectNode) submittedData).put("accountOwnerId", usr.getId()));
		}

		jobEntity.setJobSubmissionData(submittedData);
		jobEntity.setFormSubmissionId(formSubmissionData.getId());

		jobRepository.updateDocumentEntityId(jobRequest.getTempDocId(), jobEntity.getId(), jobRequest.getCreatedBy(),
				JOB_DOCUMENT);

		JobEntity savedJob = jobRepository.save(jobEntity);

		if (!jobRequest.isClone() && !savedJob.getIsDraft()) {
			sendEmail(savedJob);
			savedJob.setIsEmailSent(true);
			jobRepository.save(savedJob);
		}

		return savedJob;
	}

	/**
	 * This method is used to save TOS.
	 * 
	 * @param tosRequestDTO
	 * @return
	 */
	@Transactional
	public TosEntity saveTos(TosRequestDTO tosRequestDTO) throws ServiceException {

		TosEntity tosEntity = mapTosRequestToTosEntity(tosRequestDTO);

		tosEntity = tosRepository.save(tosEntity);
		FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
		formSubmissionsRequestDTO.setUserId(tosRequestDTO.getCreatedBy());
		formSubmissionsRequestDTO.setFormId(tosRequestDTO.getFormId());
		formSubmissionsRequestDTO.setEntityId(tosRequestDTO.getId());
		formSubmissionsRequestDTO.setEntityType(TOS_TYPE);
		HttpResponse formSubmissionResponse = formSubmissionAPIClient.addFormSubmission(formSubmissionsRequestDTO);
		FormSubmissionsResponseDTO formSubmissionData = MappingUtil
				.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

		tosEntity.setTosSubmissionData(formSubmissionsRequestDTO.getSubmissionData());
		tosEntity.setFormSubmissionId(formSubmissionData.getId());

		tosRepository.save(tosEntity);

		return tosEntity;
	}

	/**
	 * This method is used to update a job
	 * 
	 * @param id
	 * @param jobRequest
	 * @return
	 */
	public void update(JobRequest jobRequest) throws ServiceException {
		LOG.info("Job updated : Service");
		Optional<JobEntity> dbJob = jobRepository.findById(jobRequest.getId());

		// add check for title exists in a DB
		if (dbJob.isPresent() && dbJob.get().getId() != jobRequest.getId()) {
			throw new ServiceException(
					messageSource.getMessage("error.jobtitletaken", null, LocaleContextHolder.getLocale()));
		}
		JobEntity jobEntity = getById(jobRequest.getId());
		jobEntity.setTitle(jobRequest.getTitle());
		jobEntity.setUpdatedBy(jobRequest.getUpdatedBy());
		jobEntity.setIsActive(true);
		jobEntity.setIsDeleted(false);

		JsonNode submittedData = MappingUtil.convertJSONStringToJsonNode(jobRequest.getFormData());

		if (submittedData.get("accountOwner").asText() != null) {
			String accountOwnerEmail = submittedData.get("accountOwner").asText();
			accountOwnerEmail = accountOwnerEmail.substring(accountOwnerEmail.lastIndexOf("(")).replace("(", "")
					.replace(")", "");
			userRepository.findByEmail(accountOwnerEmail)
					.ifPresent(usr -> ((ObjectNode) submittedData).put("accountOwnerId", usr.getId()));
		}

		jobEntity.setJobSubmissionData(submittedData);
		jobEntity.setIsDraft(jobRequest.getIsDraft());
		JobEntity updatedJob = jobRepository.save(jobEntity);

		FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
		formSubmissionsRequestDTO.setUserId(jobRequest.getUpdatedBy());
		formSubmissionsRequestDTO.setFormId(jobRequest.getFormId());
		formSubmissionsRequestDTO.setSubmissionData(MappingUtil.convertJSONStringToJsonNode(jobRequest.getFormData()));
		formSubmissionsRequestDTO.setEntityId(jobEntity.getId());
		formSubmissionsRequestDTO.setEntityType(JOB_TYPE);

		formSubmissionAPIClient.updateFormSubmission(jobEntity.getFormSubmissionId().intValue(),
				formSubmissionsRequestDTO);

		if (!updatedJob.getIsDraft() && updatedJob.getIsEmailSent() != null && !updatedJob.getIsEmailSent()) {
			sendEmail(updatedJob);
			updatedJob.setIsEmailSent(true);
			jobRepository.save(updatedJob);
		}

	}

	/**
	 * This method is used to update the TOS.
	 * 
	 * @param tosRequestDTO
	 * @throws ServiceException
	 */
	public void updateTos(TosRequestDTO tosRequestDTO) throws ServiceException {
		LOG.info("Tos updated : Service");
		TosEntity tosEntity = getByTosId(tosRequestDTO.getId());
		tosEntity.setStatus(tosRequestDTO.getStatus());
		tosEntity.setUpdatedBy(tosRequestDTO.getUpdatedBy());
		tosEntity.setIsActive(true);
		tosEntity.setIsDeleted(false);
		tosRepository.save(tosEntity);

		FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
		formSubmissionsRequestDTO.setUserId(tosRequestDTO.getUpdatedBy());
		formSubmissionsRequestDTO.setFormId(tosRequestDTO.getFormId());
		formSubmissionsRequestDTO.setEntityId(tosRequestDTO.getId());
		formSubmissionsRequestDTO.setEntityType(TOS_TYPE);

		formSubmissionAPIClient.updateFormSubmission(tosEntity.getFormSubmissionId().intValue(),
				formSubmissionsRequestDTO);
	}

	/**
	 * This method is used to retrieve a job Information
	 * 
	 * @param id
	 * @return
	 */
	public JobEntity getById(Long id) throws ServiceException {
		if (id == null) {
			throw new ServiceException(
					messageSource.getMessage("error.provide.id", new Object[] { id }, LocaleContextHolder.getLocale()));
		}

		Optional<JobEntity> jobOpt = jobRepository.findById(id);
		if (jobOpt.isPresent() && !jobOpt.get().getIsDeleted()) {
			String fodRecruiters = jobRepository.getRecruiters(id);
			JobEntity jobEntity = jobOpt.get();
			try {
				JsonNode submittedData = jobEntity.getJobSubmissionData();
				((ObjectNode) submittedData).put("fodRecruiters", fodRecruiters);
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
			return jobEntity;
		} else {
			throw new ServiceException(messageSource.getMessage("error.jobnotfound", new Object[] { id },
					LocaleContextHolder.getLocale()));
		}
	}

	/**
	 * This method is used to retrieve a TOS Information
	 * 
	 * @param id
	 * @return
	 * @throws ServiceException
	 */
	public TosEntity getByTosId(Long id) throws ServiceException {
		if (id == null) {
			throw new ServiceException(
					messageSource.getMessage("error.provide.id", new Object[] { id }, LocaleContextHolder.getLocale()));
		}

		Optional<TosEntity> permission = tosRepository.findById(id);
		if (permission.isPresent() && !permission.get().getIsDeleted()) {
			return permission.get();
		} else {
			throw new ServiceException(messageSource.getMessage("error.tosnotfound", new Object[] { id },
					LocaleContextHolder.getLocale()));
		}
	}

	/**
	 * This method is used to Delete a job
	 * 
	 * @param id
	 */
	public void delete(Long id) throws ServiceException {
		JobEntity dbJob = getById(id);
		dbJob.setIsDeleted(true);
		jobRepository.save(dbJob);
	}

	/**
	 * This method is used to Delete a TOS
	 * 
	 * @param id
	 * @throws ServiceException
	 */
	public void deleteTos(Long id) throws ServiceException {
		TosEntity tos = getByTosId(id);
		tos.setIsDeleted(true);
		tosRepository.save(tos);
	}

	public void softDelete(Long id) throws ServiceException {
		CustomFieldsEntity customFieldsEntity = jobCustomFieldsRepository.findByIdAndDeleted(id, false, true)
				.orElseThrow(() -> new RuntimeException("Custom view not found"));
		customFieldsEntity.setIsDeleted(true);
		customFieldsEntity.setSelected(false);
		jobCustomFieldsRepository.save(customFieldsEntity);
	}

	public List<JobEntity> getAllJobs(Integer pageNo, Integer pageSize, String sortBy) throws ServiceException {
		LOG.info("getAllJobs request processing");
		try {
			Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
			List<JobEntity> jobEntityList = jobRepository.findAllAndIsDeleted(false, paging);
			LOG.info("jobEntityList retrieved : Service");
			return jobEntityList;
		} catch (Exception e) {
			throw new ServiceException(e.getLocalizedMessage());
		}
	}

	public Page<JobEntity> search(String searchTerms, Pageable pageable) throws ServiceException {
		LOG.info("search request processing");
		try {
			JobSpecificationBuilder builder = new JobSpecificationBuilder();
			Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
			Matcher matcher = pattern.matcher(searchTerms + ",");

			while (matcher.find()) {
				builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
			}

			return jobRepository.findAll(builder.build(), pageable);
		} catch (Exception e) {
			throw new ServiceException(e.getLocalizedMessage());
		}
	}

	public JobListingResponseDTO getJobListingPage(JobListingRequestDTO jobListingRequestDTO) {
		Integer page = jobListingRequestDTO.getPage();
		Integer size = jobListingRequestDTO.getPageSize();
		String sortBy = jobListingRequestDTO.getSortBy();
		String sortDirection = jobListingRequestDTO.getSortDirection();
		String jobType = jobListingRequestDTO.getJobType();
		Boolean getAll = jobListingRequestDTO.getGetAll();
		Long userId = jobListingRequestDTO.getUserId();
		Boolean isDownload = jobListingRequestDTO.getIsDownload();
		List<FilterDTO> filters = jobListingRequestDTO.getFilters();

		// Get sort direction
		Sort.Direction direction = Sort.DEFAULT_DIRECTION;
		if (sortDirection != null && !sortDirection.isEmpty()) {
			direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
		}
		if (sortBy == null || sortBy.isEmpty() || sortBy.equals("")) {
			sortBy = "updated_at";
			direction = Sort.Direction.DESC;
		}

		PageRequest pageRequest = null;
		if (isDownload) {
			pageRequest = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(direction, sortBy));
		} else {
			pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));
		}

		Page<JobEntity> jobEntitiesPage = null;
		List<Long> userIds = new ArrayList<>();
		if (!getAll) {
			userIds = userUtil.getUsersIdUnderManager();
		}
		// Find user id
		// Try with numeric first else try with string (jsonb)
		try {
			jobEntitiesPage = jobRepository.findAllByOrderByNumericWithUserIds(userIds, false, true, pageRequest,
					jobType, userId, filters);
		} catch (Exception e) {
			jobEntitiesPage = jobRepository.findAllByOrderByStringWithUserIds(userIds, false, true, pageRequest,
					jobType, userId, filters);
		}

		try {
			addPulseId();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			addAccountPulseId();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			addCandidatePulseId();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return pageJobListingToJobListingResponseDTO(jobEntitiesPage);
	}

	public JobListingResponseDTO getJobListingPageWithSearch(JobListingRequestDTO jobListingRequestDTO) {
		Integer page = jobListingRequestDTO.getPage();
		Integer size = jobListingRequestDTO.getPageSize();
		String sortBy = jobListingRequestDTO.getSortBy();
		String sortDirection = jobListingRequestDTO.getSortDirection();
		List<String> searchFields = jobListingRequestDTO.getSearchFields();
		String searchTerm = jobListingRequestDTO.getSearchTerm();
		String jobType = jobListingRequestDTO.getJobType();
		Boolean getAll = jobListingRequestDTO.getGetAll();
		Long userId = jobListingRequestDTO.getUserId();
		Boolean isDownload = jobListingRequestDTO.getIsDownload();
		List<FilterDTO> filters = jobListingRequestDTO.getFilters();

		// Get sort direction
		Sort.Direction direction = Sort.DEFAULT_DIRECTION;
		if (sortDirection != null) {
			direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
		}
		if (sortBy == null) {
			sortBy = "updated_at";
			direction = Sort.Direction.DESC;
		}

		PageRequest pageRequest = null;
		if (isDownload) {
			pageRequest = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(direction, sortBy));
		} else {
			pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));
		}

		Page<JobEntity> jobEntityPage = null;
		// Try with numeric first else try with string (jsonb)
		List<Long> userIds = new ArrayList<>();
		if (!getAll) {
			userIds = userUtil.getUsersIdUnderManager();
		}

		try {
			jobEntityPage = jobRepository.findAllByOrderByAndSearchNumericWithUserIds(userIds, false, true, pageRequest,
					searchFields, searchTerm, jobType, userId, filters);
		} catch (Exception e) {
			jobEntityPage = jobRepository.findAllByOrderByAndSearchStringWithUserIds(userIds, false, true, pageRequest,
					searchFields, searchTerm, jobType, userId, filters);
		}

		return pageJobListingToJobListingResponseDTO(jobEntityPage);
	}

	public Set<FieldInformation> getAllJobFields(Long userId) throws ServiceException {

		List<JobEntity> jobEntities = jobRepository.findAllByIsDraftAndIsDeletedAndIsActive(false, false, true);

		if (jobEntities.isEmpty()) {
			return null;
		}

		// Declare a new haspmap to store the label and value
		Set<FieldInformation> fieldColumn = new HashSet<>();
		fieldColumn.add(new FieldInformation("Title", "title", true, "title"));
		fieldColumn.add(new FieldInformation("Created At", "createdAt", true, "created_at"));
		fieldColumn.add(new FieldInformation("Updated At", "updatedAt", true, "updated_at"));
		fieldColumn.add(new FieldInformation("Created By", "createdByName", false, null));

		// Loop through the job submission data jsonNode
		for (JobEntity jobEntity : jobEntities) {
			if (jobEntity.getJobSubmissionData() != null) {
				Iterator<String> jobFieldNames = jobEntity.getJobSubmissionData().fieldNames();
				while (jobFieldNames.hasNext()) {
					String fieldName = jobFieldNames.next();
					fieldColumn.add(new FieldInformation(StringUtil.convertCamelCaseToTitleCase2(fieldName),
							"jobSubmissionData." + fieldName, true, "job_submission_data." + fieldName));
				}
			}
		}
		return fieldColumn;
	}

	/**
	 * This method is used to get all jobs fields including all other related
	 * microservice
	 *
	 * @return
	 */
	public HashMap<String, List<HashMap<String, String>>> getAllJobsFieldsAll() {
		HashMap<String, List<HashMap<String, String>>> allFields = new HashMap<>();

		// Get job fields from job microservice
		List<HashMap<String, String>> jobFields = getJobFields();
		allFields.put("jobInfo", jobFields);

		return allFields;
	}

	/**
	 * This method is used to get all jobs data (Only from job microservice)
	 *
	 * @param jobId
	 * @return
	 */
	public JobListingDataDTO getJobByIdData(Integer jobId) {
		return jobEntityToJobNewListingDataDTO(jobRepository.findByIdAndDeleted(jobId.longValue(), false, true)
				.orElseThrow(() -> new RuntimeException("Job not found")));
	}

	/**
	 * This method is used to get all jobs data including all other related
	 * microservice
	 *
	 * @return
	 */
	public HashMap<String, Object> getJobByIdDataAll(Long jobId) {
		HashMap<String, Object> jobData = new HashMap<>();
		// Get Job fields from job microservice
		JsonNode jobInfo = getJobInfoByIDJsonNode(jobId);
		jobData.put("jobInfo", jobInfo);

		return jobData;
	}

	private JsonNode getJobInfoByIDJsonNode(Long jobId) {
		// Get basic information from form submission
		JobEntity jobEntity = jobRepository.findByIdAndDeleted(jobId, false, true)
				.orElseThrow(() -> new RuntimeException("Job not found"));
		// Get the form submission data from candidate microservice
		JsonNode jobSubmissionData = jobEntity.getJobSubmissionData();
		// Get additional data in JSon node format too
		JsonNode jobExtraDataJsonNode = getJobExtraData(jobEntity).getSelectedFieldsJsonNode();
		return MappingUtil.mergeJsonNodes(List.of(jobSubmissionData, jobExtraDataJsonNode));
	}

	private List<HashMap<String, String>> getJobFields() {
		JobExtraData jobExtraData = new JobExtraData();

		// Get job dynamic fields from form service
		HttpResponse jobFormFieldResponse = formSubmissionAPIClient.getFormFieldNameList("job");
		List<HashMap<String, String>> jobFields = MappingUtil.mapClientBodyToClass(jobFormFieldResponse.getData(),
				List.class);

		// Merge lists using addAll()
		List<HashMap<String, String>> mergedList = new ArrayList<>(jobExtraData.getAllFieldsMap());
		mergedList.addAll(jobFields);

		return mergedList;
	}

	private JobExtraData getJobExtraData(JobEntity jobEntity) {
		JobExtraData jobExtraData = new JobExtraData(jobEntity);
		// Get created by User data from user microservice
		HttpResponse createUserResponse = userAPIClient.getUserById(jobEntity.getCreatedBy().intValue());
		UserResponseDTO createUserData = MappingUtil.mapClientBodyToClass(createUserResponse.getData(),
				UserResponseDTO.class);
		jobExtraData.setCreatedByName(createUserData.getFirstName() + " " + createUserData.getLastName());
		HttpResponse updateUserResponse = userAPIClient.getUserById(jobEntity.getUpdatedBy().intValue());
		UserResponseDTO updateUserData = MappingUtil.mapClientBodyToClass(updateUserResponse.getData(),
				UserResponseDTO.class);
		jobExtraData.setUpdatedByName(updateUserData.getFirstName() + " " + updateUserData.getLastName());
		return jobExtraData;
	}

	private JobListingResponseDTO pageJobListingToJobListingResponseDTO(Page<JobEntity> jobEntityPage) {
		JobListingResponseDTO jobListingNewResponseDTO = new JobListingResponseDTO();
		jobListingNewResponseDTO.setTotalPages(jobEntityPage.getTotalPages());
		jobListingNewResponseDTO.setTotalElements(jobEntityPage.getTotalElements());
		jobListingNewResponseDTO.setPage(jobEntityPage.getNumber());
		jobListingNewResponseDTO.setPageSize(jobEntityPage.getSize());
		List<JobListingDataDTO> jobListingDataDTOs = new ArrayList<>();
		jobListingDataDTOs = jobEntityPage.getContent().stream().map(jobEntity -> {
			JobListingDataDTO jobListingDataDTO = new JobListingDataDTO(jobEntity);

			try {
				// Get created by User data from user service
				// Cast to Integer
				HttpResponse userResponse = userAPIClient.getUserById(jobEntity.getCreatedBy().intValue());
				UserResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(),
						UserResponseDTO.class);
				jobListingDataDTO.setCreatedByName(userData.getFirstName() + " " + userData.getLastName());
			} catch (Exception e) {
			}

			try {
				// Get updated by User data from user service
				HttpResponse updatedByUserResponse = userAPIClient.getUserById(jobEntity.getUpdatedBy().intValue());
				UserResponseDTO updatedByUserData = MappingUtil.mapClientBodyToClass(updatedByUserResponse.getData(),
						UserResponseDTO.class);
				jobListingDataDTO
						.setUpdatedByName(updatedByUserData.getFirstName() + " " + updatedByUserData.getLastName());
			} catch (Exception e) {
			}

			try {
				String fodRecruiters = jobRepository.getRecruiters(jobEntity.getId());
				jobListingDataDTO.setRecruiterName(fodRecruiters);
			} catch (Exception e) {
			}

			return jobListingDataDTO;
		}).toList();
		jobListingNewResponseDTO.setJobs(jobListingDataDTOs);
		return jobListingNewResponseDTO;
	}

	public JobEntity getJobDraft(Long userId) throws ServiceException {
		Optional<JobEntity> jobEntity = jobRepository.findByUserAndDraftAndDeleted(userId, true, false, true);
		try {
			if (jobEntity.isPresent() && !jobEntity.get().getIsDeleted()) {
				return jobEntity.get();
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new ServiceException(e.getLocalizedMessage());
		}
	}

	private JobListingDataDTO jobEntityToJobNewListingDataDTO(JobEntity jobEntity) {
		JobListingDataDTO jobListingDataDTO = new JobListingDataDTO(jobEntity);
		// Get created by User data from user microservice
		HttpResponse createUserResponse = userAPIClient.getUserById(jobEntity.getCreatedBy().intValue());
		UserResponseDTO createUserData = MappingUtil.mapClientBodyToClass(createUserResponse.getData(),
				UserResponseDTO.class);
		jobListingDataDTO.setCreatedByName(createUserData.getFirstName() + " " + createUserData.getLastName());
		HttpResponse updateUserResponse = userAPIClient.getUserById(jobEntity.getUpdatedBy().intValue());
		UserResponseDTO updateUserData = MappingUtil.mapClientBodyToClass(updateUserResponse.getData(),
				UserResponseDTO.class);
		jobListingDataDTO.setUpdatedByName(updateUserData.getFirstName() + " " + updateUserData.getLastName());
		return jobListingDataDTO;
	}

	/**
	 * This method is used to update/create job embeddings
	 * 
	 * @param jobId
	 * @return
	 */
	public HashMap<String, Object> updateJobEmbeddings(Long jobId) {
		HashMap<String, Object> jobHashMapData = getJobByIdDataAll(jobId);

		// Convert HashMap to JSON String
		JsonNode candidateDataJsonNode = MappingUtil.convertHashMapToJsonNode(jobHashMapData);
		String jobDetails = JobDataExtractionUtil.extractJobInfo(candidateDataJsonNode);

		EmbeddingRequestDTO embeddingRequestDTO = new EmbeddingRequestDTO();
		embeddingRequestDTO.setText(TextProcessingUtil.removeStopWords(jobDetails));

		HttpResponse jobEmbeddingResponse = embeddingAPIClient.getEmbeddingSinglePy(embeddingRequestDTO);
		EmbeddingResponseDTO jobEmbeddingData = MappingUtil.mapClientBodyToClass(jobEmbeddingResponse.getData(),
				EmbeddingResponseDTO.class);

		// Update the candidate with the embedding
		jobRepository.updateVector(jobId, "job_embeddings", jobEmbeddingData.getEmbedding());

		return jobHashMapData;
	}

	/**
	 * Get job embeddings by id and type (default or openai)
	 * 
	 * @param jobId
	 * @param type
	 * @return
	 */
	public EmbeddingResponseDTO getJobEmbeddingsById(Long jobId, String type) {
		List<Float> jobEmbeddings = new ArrayList<>();
		if (type.equals("default")) {
			jobEmbeddings = jobRepository.getEmbeddingsById(jobId, "job_embeddings")
					.orElseThrow(() -> new RuntimeException("Job Embeddings not found"));
		} else {
			jobEmbeddings = jobRepository.getEmbeddingsById(jobId, "job_embeddings_openai")
					.orElseThrow(() -> new RuntimeException("Job Embeddings not found"));
		}
		EmbeddingResponseDTO embeddingResponseDTO = new EmbeddingResponseDTO();
		embeddingResponseDTO.setEmbedding(jobEmbeddings);
		return embeddingResponseDTO;
	}

	public List<CustomFieldsEntity> getAllCreatedCustomViews(Long userId) {
		List<CustomFieldsEntity> customfields = jobCustomFieldsRepository.findAllByUser(userId, "Job", false);
		return customfields;
	}

	public CustomFieldsResponseDTO saveCustomFields(CustomFieldsRequestDTO customFieldsRequestDTO)
			throws ServiceException {

		if (jobCustomFieldsRepository.findByNameAndTypeAndIsDeletedAndCreatedBy(customFieldsRequestDTO.getName(), "Job",
				false, getUserId())) {
			throw new DuplicateResourceException(
					messageSource.getMessage("error.jobcustomnametaken", null, LocaleContextHolder.getLocale()));
		}

		List<CustomFieldsEntity> selectedCustomView = jobCustomFieldsRepository
				.findAllByUser(customFieldsRequestDTO.getCreatedBy(), "Job", false);

		if (selectedCustomView != null) {
			for (CustomFieldsEntity customView : selectedCustomView) {
				if (customView.isSelected() == true) {
					customView.setSelected(false);
					jobCustomFieldsRepository.save(customView);
				}
			}

		}
		CustomFieldsEntity jobCustomFieldsEntity = customFieldsRequestDTOToCustomFieldsEntity(customFieldsRequestDTO);
		return customFieldsEntityToCustomFieldsResponseDTO(jobCustomFieldsEntity);
	}

	CustomFieldsEntity customFieldsRequestDTOToCustomFieldsEntity(CustomFieldsRequestDTO customFieldsRequestDTO) {
		CustomFieldsEntity customFieldsEntity = new CustomFieldsEntity();
		customFieldsEntity.setName(customFieldsRequestDTO.getName());
		customFieldsEntity.setType(customFieldsRequestDTO.getType());

		// converting list of string to comma saparated string
		String columnNames = String.join(",", customFieldsRequestDTO.getColumnName());
		customFieldsEntity.setColumnName(columnNames);
		// customFieldsEntity.setColumnName(customFieldsRequestDTO.getColumnName());
		// Get Filters
		List<FilterDTO> filters = customFieldsRequestDTO.getFilters();
		if (filters != null) {
			customFieldsEntity.setFilters(JSONUtil.convertObjectToJsonNode(filters));
		}
		customFieldsEntity.setCreatedBy(customFieldsRequestDTO.getCreatedBy());
		customFieldsEntity.setUpdatedBy(customFieldsRequestDTO.getUpdatedBy());
		customFieldsEntity.setSelected(true);
		return jobCustomFieldsRepository.save(customFieldsEntity);
	}

	CustomFieldsResponseDTO customFieldsEntityToCustomFieldsResponseDTO(CustomFieldsEntity jobCustomFieldsEntity) {
		CustomFieldsResponseDTO customFieldsResponseDTO = new CustomFieldsResponseDTO();
		// Converting String to List of String.
		String columnNames = jobCustomFieldsEntity.getColumnName();
		List<String> columnNamesList = Arrays.asList(columnNames.split("\\s*,\\s*"));
		customFieldsResponseDTO.setColumnName(columnNamesList);
		// customFieldsResponseDTO.setColumnName(jobCustomFieldsEntity.getColumnName());
		customFieldsResponseDTO.setCreatedBy(jobCustomFieldsEntity.getCreatedBy());
		customFieldsResponseDTO.setName(jobCustomFieldsEntity.getName());
		customFieldsResponseDTO.setType(jobCustomFieldsEntity.getType());
		customFieldsResponseDTO.setUpdatedBy(jobCustomFieldsEntity.getUpdatedBy());
		customFieldsResponseDTO.setId(jobCustomFieldsEntity.getId());
		// Get Filters
		JsonNode filters = jobCustomFieldsEntity.getFilters();
		if (filters != null) {
			customFieldsResponseDTO.setFilters(MappingUtil.convertJsonNodeToList(filters, FilterDTO.class));
		}
		return customFieldsResponseDTO;
	}

	public CustomFieldsResponseDTO updateCustomView(Long id, Long userId) throws ServiceException {
		if (jobCustomFieldsRepository.findById(id).get().getIsDeleted()) {
			throw new ServiceException(messageSource.getMessage("error.jobcustomViewAlreadyDeleted", null,
					LocaleContextHolder.getLocale()));
		}
		List<CustomFieldsEntity> selectedCustomView = jobCustomFieldsRepository.findAllByUser(userId, "Job", false);
		for (CustomFieldsEntity customView : selectedCustomView) {
			if (customView.isSelected() == true) {
				customView.setSelected(false);
				jobCustomFieldsRepository.save(customView);
			}
		}
		Optional<CustomFieldsEntity> customFieldsEntity = jobCustomFieldsRepository.findById(id);
		customFieldsEntity.get().setSelected(true);
		jobCustomFieldsRepository.save(customFieldsEntity.get());

		return customFieldsEntityToCustomFieldsResponseDTO(customFieldsEntity.get());

	}

	public void updateJobEmbeddingsAll() {
		List<JobEntity> jobs = jobRepository.findAllByEmbeddingIsNull();
		if (jobs != null && !jobs.isEmpty()) {
			System.out.println("Total jobs to update: " + jobs.size());
			int count = 0;
			int passedCount = 0;
			int failedCount = 0;
			for (JobEntity job : jobs) {
				try {
					updateJobEmbeddings(job.getId());
					passedCount++;
				} catch (Exception e) {
					e.printStackTrace();
					failedCount++;
				}
				count++;
				System.out.println("Updated: " + count + " jobs");
			}
			System.out.println("All jobs updated...");
			System.out.println("Total jobs: " + jobs.size());
			System.out.println("Total passed: " + passedCount);
			System.out.println("Total failed: " + failedCount);
		}

	}

	public void softDeleteJobs(JobListingDeleteRequestDTO jobListingDeleteRequestDTO) {

		if (jobListingDeleteRequestDTO.getJobIds().isEmpty()) {
			throw new RuntimeException("No job selected");
		}
		List<JobEntity> jobEntities = jobRepository
				.findAllByIdsAndDraftAndDeleted(jobListingDeleteRequestDTO.getJobIds(), false, false, true);

		if (jobEntities.isEmpty()) {
			throw new RuntimeException("No job found");
		}

		for (JobEntity jobEntity : jobEntities) {
			jobEntity.setIsDeleted(true);
		}

		jobRepository.saveAll(jobEntities);
	}

	private void sendEmail(JobEntity jobEntity) {
		EmailMultiTemplateRequestDTO dto = new EmailMultiTemplateRequestDTO();
		dto.setCategory(JobUtil.EMAIL_TEMPLATE);

		// Get a list of all the key in the job submission data
		List<String> jobSubmissionDataKeys = new ArrayList<>();
		if (jobEntity.getJobSubmissionData() != null) {
			Iterator<String> jobFieldNames = jobEntity.getJobSubmissionData().fieldNames();
			while (jobFieldNames.hasNext()) {
				String fieldName = jobFieldNames.next();
				jobSubmissionDataKeys.add(fieldName);
			}
		}

		// Set Subject Line
		String jobTitle = JobUtil.getValue(jobEntity, "jobTitle");
		String clientName = JobUtil.getValue(jobEntity, "accountName");
		String newJobSubject = "New Job Notification | " + jobTitle + " | " + clientName;

		Map<String, String> params = new HashMap<>();

		// loop and add the job submission data to the params
		for (String key : jobSubmissionDataKeys) {
			// Check if JobUtil.getValue(jobEntity, key) is not null or empty
			String value;
			if (JobUtil.getValue(jobEntity, key) != null && !JobUtil.getValue(jobEntity, key).isEmpty()) {
				value = JobUtil.getValue(jobEntity, key);
			} else {
				value = "-";
			}
			params.put("Jobs.jobInfo." + key, value);
		}

		// Get AccountOwner Name if exists
		String accountOwner = JobUtil.getValue(jobEntity, "accountOwner");
		HashMap<String, String> accountOwnerData = new HashMap<>();
		if (accountOwner != null) {
			accountOwnerData = extractAccountOwnerDetails(accountOwner);
			if (!accountOwnerData.get("accountName").isEmpty()) {
				params.put("Jobs.jobInfo.accountOwner", accountOwnerData.get("accountName"));
			} else {
				params.put("Jobs.jobInfo.accountOwner", "-");
			}
		}

		// Set template Name
		dto.setTemplateName(JobUtil.NEW_JOB_NOTIFICATION);

		Optional<UserEntity> createdBy = userRepository.findById(jobEntity.getCreatedBy());

		List<String> emailsList = new ArrayList<String>();
		emailsList.add("delivery@aven-sys.com");
		emailsList.add("SGTAG@aven-sys.com");
		emailsList.add("avensys.itag@aven-sys.com");
		emailsList.add("avensys.mtag@aven-sys.com");

		if (createdBy.isPresent()) {
			String createdByEmail = createdBy.get().getEmail();
			emailsList.add(createdByEmail);
		}

		// Set the email to send
		if (accountOwnerData.get("email") != null) {
			emailsList.add(accountOwnerData.get("email"));
		}

		String[] to = emailsList.stream().toArray(String[]::new);
		dto.setTo(to);

		// Set the subject
		dto.setSubject(newJobSubject);

		dto.setTemplateMap(params);
		emailAPIClient.sendEmailServiceTemplate(dto);
	}

	private HashMap<String, String> extractAccountOwnerDetails(String accountOwner) {
		HashMap<String, String> accountOwnerDetails = new HashMap<>();
		try {
			String[] accountOwnerArray = accountOwner.split("\\(");
			accountOwnerDetails.put("accountName", accountOwnerArray[0].trim());
			accountOwnerDetails.put("email", accountOwnerArray[1].replace(")", "").trim());
		} catch (Exception e) {
			accountOwnerDetails.put("accountName", accountOwner);
			accountOwnerDetails.put("email", "");
		}
		return accountOwnerDetails;
	}

	public CustomFieldsResponseDTO getCustomFieldsById(Long id) {
		CustomFieldsEntity customFieldsEntity = jobCustomFieldsRepository.findByIdAndDeleted(id, false, true)
				.orElseThrow(() -> new RuntimeException("Custom view not found"));
		return customFieldsEntityToCustomFieldsResponseDTO(customFieldsEntity);
	}

	public CustomFieldsResponseDTO editCustomFieldsById(Long id, CustomFieldsRequestDTO customFieldsRequestDTO)
			throws ServiceException {
		CustomFieldsEntity customFieldsEntity = jobCustomFieldsRepository.findByIdAndDeleted(id, false, true)
				.orElseThrow(() -> new RuntimeException("Custom view not found"));
		if (!Objects.equals(customFieldsEntity.getName(), customFieldsRequestDTO.getName())
				&& jobCustomFieldsRepository.findByNameAndTypeAndIsDeletedAndCreatedBy(customFieldsRequestDTO.getName(),
						"Job", false, getUserId())) {
			throw new DuplicateResourceException(
					messageSource.getMessage("error.jobcustomnametaken", null, LocaleContextHolder.getLocale()));
		}
		customFieldsEntity.setName(customFieldsRequestDTO.getName());
		customFieldsEntity.setSelected(customFieldsEntity.isSelected());
		if (customFieldsRequestDTO.getColumnName() != null) {
			if (!customFieldsRequestDTO.getColumnName().isEmpty()) {
				String columnNames = String.join(",", customFieldsRequestDTO.getColumnName());
				customFieldsEntity.setColumnName(columnNames);
			}
		}
		customFieldsEntity.setUpdatedBy(getUserId().longValue());
		List<FilterDTO> filters = customFieldsRequestDTO.getFilters();
		if (filters != null) {
			customFieldsEntity.setFilters(JSONUtil.convertObjectToJsonNode(filters));
		}

		// Save custom view
		CustomFieldsEntity updatedCustomFieldEntity = jobCustomFieldsRepository.save(customFieldsEntity);
		return customFieldsEntityToCustomFieldsResponseDTO(updatedCustomFieldEntity);
	}

	public void unSelectAllCustomViews() {
		jobCustomFieldsRepository.updateIsSelected(false, "Job", false, getUserId());
	}

	private Integer getUserId() {
		String email = JwtUtil.getEmailFromContext();
		HttpResponse userResponse = userAPIClient.getUserByEmail(email);
		UserResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(), UserResponseDTO.class);
		return userData.getId();
	}

	public void addPulseId() throws Exception {
		List<Long> jobIds = tempRepository.getNullJobs();
		if (jobIds.size() > 0) {
			for (Long jobId : jobIds) {
				try {
					StringBuilder sb = new StringBuilder("J");
					String year = (Year.now().getValue() + "").substring(2);

					System.out.println("JobId: " + jobId);

					JobEntity jobEntity = jobRepository.findById(jobId).get();
					if (jobId != null) {
						JsonNode submittedData = jobEntity.getJobSubmissionData();
						String accountName = submittedData.get("accountName").asText();

						if (accountName != null) {
							accountRepository.findByName(accountName).ifPresent(accountEntity -> {
								String country = accountEntity.getAccountSubmissionData().get("addressCountry")
										.asText();
								if (country != null) {
									String iso3 = tempRepository.getISO3(country);
									sb.append(iso3);
								}
							});
						}

						sb.append(year + '-');

						Long pulseId = tempRepository.getPulseId("job");

						if (pulseId < 1000) {
							String val = "0000" + pulseId;
							String dynamicId = val.substring(val.length() - 4);
							sb.append(dynamicId);
						}

						((ObjectNode) submittedData).put("jobId", sb.toString());

						jobEntity.setJobSubmissionData(submittedData);

						FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
						formSubmissionsRequestDTO.setUserId(jobEntity.getUpdatedBy());
						formSubmissionsRequestDTO.setFormId(jobEntity.getFormId());
						formSubmissionsRequestDTO.setSubmissionData(submittedData);
						formSubmissionsRequestDTO.setEntityId(jobEntity.getId());
						formSubmissionsRequestDTO.setEntityType(JOB_TYPE);

						formSubmissionAPIClient.updateFormSubmission(jobEntity.getFormSubmissionId().intValue(),
								formSubmissionsRequestDTO);

						tempRepository.updateJobId(jobEntity.getFormSubmissionId(), jobId);
					}
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void addAccountPulseId() throws Exception {
		List<Integer> accountIds = tempRepository.getNullAccounts();
		if (accountIds.size() > 0) {
			for (Integer accountId : accountIds) {
				try {
					StringBuilder sb = new StringBuilder("A");
					String year = (Year.now().getValue() + "").substring(2);

					System.out.println("AccountId: " + accountId);

					AccountEntity accountEntity = accountRepository.findById(accountId).get();
					if (accountId != null) {
						JsonNode submittedData = accountEntity.getAccountSubmissionData();

						String country = submittedData.get("addressCountry").asText();
						if (country != null) {
							String iso3 = tempRepository.getISO3(country);
							sb.append(iso3);
						}

						sb.append(year + '-');

						Long pulseId = tempRepository.getPulseId("account");

						if (pulseId < 1000) {
							String val = "0000" + pulseId;
							String dynamicId = val.substring(val.length() - 4);
							sb.append(dynamicId);
						}

						((ObjectNode) submittedData).put("accountId", sb.toString());

						accountEntity.setAccountSubmissionData(submittedData);

						FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
						formSubmissionsRequestDTO.setUserId(Long.valueOf(accountEntity.getUpdatedBy().longValue()));
						formSubmissionsRequestDTO.setFormId(Long.valueOf(accountEntity.getFormId().longValue()));
						formSubmissionsRequestDTO.setSubmissionData(submittedData);
						formSubmissionsRequestDTO.setEntityId(Long.valueOf(accountEntity.getId()));
						formSubmissionsRequestDTO.setEntityType("account_account");

						formSubmissionAPIClient.updateFormSubmission(accountEntity.getFormSubmissionId().intValue(),
								formSubmissionsRequestDTO);

						tempRepository.updateAccountId(accountEntity.getFormSubmissionId(), accountId);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void addCandidatePulseId() throws Exception {
		List<Long> candidateIds = tempRepository.getNullCandidates();
		if (candidateIds.size() > 0) {
			for (Long candidateId : candidateIds) {
				try {
					StringBuilder sb = new StringBuilder("C");
					String year = (Year.now().getValue() + "").substring(2);

					System.out.println("CandidateId: " + candidateId);

					CandidateEntity candidateEntity = candidateRepository.findById(candidateId).get();
					if (candidateId != null) {
						JsonNode submittedData = candidateEntity.getCandidateSubmissionData();

						sb.append(year + '-');

						Long pulseId = tempRepository.getPulseId("candidate");

						if (pulseId < 1000) {
							String val = "0000" + pulseId;
							String dynamicId = val.substring(val.length() - 4);
							sb.append(dynamicId);
						}

						((ObjectNode) submittedData).put("candidateId", sb.toString());

						candidateEntity.setCandidateSubmissionData(submittedData);

						FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
						formSubmissionsRequestDTO.setUserId(candidateEntity.getUpdatedBy());
						formSubmissionsRequestDTO.setFormId(Long.valueOf(candidateEntity.getFormId().longValue()));
						formSubmissionsRequestDTO.setSubmissionData(submittedData);
						formSubmissionsRequestDTO.setEntityId(Long.valueOf(candidateEntity.getId()));
						formSubmissionsRequestDTO.setEntityType("candidate_basic_info");

						formSubmissionAPIClient.updateFormSubmission(candidateEntity.getFormSubmissionId().intValue(),
								formSubmissionsRequestDTO);

						tempRepository.updateCandidateId(candidateEntity.getFormSubmissionId(), candidateId);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
