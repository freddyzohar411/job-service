package com.avensys.rts.jobservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.avensys.rts.jobservice.apiclient.EmbeddingAPIClient;
import com.avensys.rts.jobservice.payload.EmbeddingRequestDTO;
import com.avensys.rts.jobservice.response.*;
import com.avensys.rts.jobservice.util.*;
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

import com.avensys.rts.jobservice.apiclient.FormSubmissionAPIClient;
import com.avensys.rts.jobservice.apiclient.UserAPIClient;
import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.exception.ServiceException;
import com.avensys.rts.jobservice.model.FieldInformation;
import com.avensys.rts.jobservice.model.JobExtraData;
import com.avensys.rts.jobservice.payload.FormSubmissionsRequestDTO;
import com.avensys.rts.jobservice.payload.JobListingRequestDTO;
import com.avensys.rts.jobservice.payload.JobRequest;
import com.avensys.rts.jobservice.repository.JobRepository;
import com.avensys.rts.jobservice.search.job.JobSpecificationBuilder;
import com.fasterxml.jackson.databind.JsonNode;

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
public class JobService {

	private static final Logger LOG = LoggerFactory.getLogger(JobService.class);
	private static final String JOB_TYPE = "job";
	private static final String JOB_DOCUMENT = "job_document";

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private FormSubmissionAPIClient formSubmissionAPIClient;

	@Autowired
	private UserAPIClient userAPIClient;

	@Autowired
	private UserUtil userUtil;

	@Autowired
	private EmbeddingAPIClient embeddingAPIClient;

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
	 * This method is used to save job Need to implement roll back if error occurs.
	 * 
	 * @param jobRequest
	 * @return
	 */
	@Transactional
	public JobEntity save(JobRequest jobRequest) throws ServiceException {
		// add check for title exists in a DB
		if (jobRepository.existsByTitle(jobRequest.getTitle())) {
			throw new ServiceException(
					messageSource.getMessage("error.jobtitletaken", null, LocaleContextHolder.getLocale()));
		}

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

		jobEntity.setJobSubmissionData(formSubmissionsRequestDTO.getSubmissionData());
		jobEntity.setFormSubmissionId(formSubmissionData.getId());

		jobRepository.updateDocumentEntityId(jobRequest.getTempDocId(), jobEntity.getId(), jobRequest.getCreatedBy(),
				JOB_DOCUMENT);

		jobRepository.save(jobEntity);

		return jobEntity;
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
		Optional<JobEntity> dbJob = jobRepository.findByTitle(jobRequest.getTitle());

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
		jobEntity.setJobSubmissionData(MappingUtil.convertJSONStringToJsonNode(jobRequest.getFormData()));
		jobEntity.setIsDraft(jobRequest.getIsDraft());
		jobRepository.save(jobEntity);

		FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
		formSubmissionsRequestDTO.setUserId(jobRequest.getUpdatedBy());
		formSubmissionsRequestDTO.setFormId(jobRequest.getFormId());
		formSubmissionsRequestDTO.setSubmissionData(MappingUtil.convertJSONStringToJsonNode(jobRequest.getFormData()));
		formSubmissionsRequestDTO.setEntityId(jobEntity.getId());
		formSubmissionsRequestDTO.setEntityType(JOB_TYPE);

		formSubmissionAPIClient.updateFormSubmission(jobEntity.getFormSubmissionId().intValue(),
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

		Optional<JobEntity> permission = jobRepository.findById(id);
		if (permission.isPresent() && !permission.get().getIsDeleted()) {
			return permission.get();
		} else {
			throw new ServiceException(messageSource.getMessage("error.jobnotfound", new Object[] { id },
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

		// Get sort direction
		Sort.Direction direction = Sort.DEFAULT_DIRECTION;
		if (sortDirection != null && !sortDirection.isEmpty()) {
			direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
		}
		if (sortBy == null || sortBy.isEmpty() || sortBy.equals("")) {
			sortBy = "updated_at";
			direction = Sort.Direction.DESC;
		}
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

		Page<JobEntity> jobEntitiesPage = null;
		List<Long> userIds = new ArrayList<>();
		if (!getAll) {
			userIds = userUtil.getUsersIdUnderManager();
		}

		// Find user id
		// Try with numeric first else try with string (jsonb)
		try {
			jobEntitiesPage = jobRepository.findAllByOrderByNumericWithUserIds(userIds, false, true, pageRequest,
					jobType, userId);
		} catch (Exception e) {
			jobEntitiesPage = jobRepository.findAllByOrderByStringWithUserIds(userIds, false, true, pageRequest,
					jobType, userId);
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

		// Get sort direction
		Sort.Direction direction = Sort.DEFAULT_DIRECTION;
		if (sortDirection != null) {
			direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
		}
		if (sortBy == null) {
			sortBy = "updated_at";
			direction = Sort.Direction.DESC;
		}
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

		Page<JobEntity> jobEntityPage = null;
		// Try with numeric first else try with string (jsonb)
		List<Long> userIds = new ArrayList<>();
		if (!getAll) {
			userIds = userUtil.getUsersIdUnderManager();
		}

		try {
			jobEntityPage = jobRepository.findAllByOrderByAndSearchNumericWithUserIds(userIds, false, true, pageRequest,
					searchFields, searchTerm, jobType, userId);
		} catch (Exception e) {
			jobEntityPage = jobRepository.findAllByOrderByAndSearchStringWithUserIds(userIds, false, true, pageRequest,
					searchFields, searchTerm, jobType, userId);
		}

		return pageJobListingToJobListingResponseDTO(jobEntityPage);
	}

	public Set<FieldInformation> getAllJobFields(Long userId) throws ServiceException {

		List<JobEntity> jobEntities = jobRepository.findAllByUserIdsAndDeleted(userUtil.getUsersIdUnderManager(), false,
				true);
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

	public HashMap<String, Object> updateJobEmbeddings(Long jobId) {
		HashMap<String, Object> jobHashMapData = getJobByIdDataAll(jobId);

		// Convert HashMap to JSON String
		JsonNode candidateDataJsonNode = MappingUtil.convertHashMapToJsonNode(jobHashMapData);

		String jobDetails = JobDataExtractionUtil.extractJobInfo(candidateDataJsonNode);
		System.out.println("Job Details: " + jobDetails);

		EmbeddingRequestDTO embeddingRequestDTO = new EmbeddingRequestDTO();
		embeddingRequestDTO.setText(TextProcessingUtil.removeStopWords(jobDetails));

		HttpResponse jobEmbeddingResponse = embeddingAPIClient
				.getEmbeddingSinglePy(embeddingRequestDTO);
		EmbeddingResponseDTO jobEmbeddingData = MappingUtil
				.mapClientBodyToClass(jobEmbeddingResponse.getData(), EmbeddingResponseDTO.class);

		// Update the candidate with the embedding
		jobRepository.updateVector(jobId, "job_embeddings",
				jobEmbeddingData.getEmbedding());

		return jobHashMapData;
	}

	public EmbeddingResponseDTO getJobEmbeddingsById(Long jobId) {
		List<Float> jobEmbeddings = jobRepository.getEmbeddingsById(jobId, "job_embeddings").orElseThrow(
				() -> new RuntimeException("Job Embeddings not found"));
		EmbeddingResponseDTO embeddingResponseDTO = new EmbeddingResponseDTO();
		embeddingResponseDTO.setEmbedding(jobEmbeddings);
		return embeddingResponseDTO;
	}

}
