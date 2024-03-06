package com.avensys.rts.jobservice.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.avensys.rts.jobservice.apiclient.EmailAPIClient;
import com.avensys.rts.jobservice.apiclient.FormSubmissionAPIClient;
import com.avensys.rts.jobservice.entity.CandidateEntity;
import com.avensys.rts.jobservice.entity.JobCandidateStageEntity;
import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.entity.JobStageEntity;
import com.avensys.rts.jobservice.entity.JobTimelineEntity;
import com.avensys.rts.jobservice.exception.ServiceException;
import com.avensys.rts.jobservice.payload.EmailMultiTemplateRequestDTO;
import com.avensys.rts.jobservice.payload.FormSubmissionsRequestDTO;
import com.avensys.rts.jobservice.payload.JobCandidateStageRequest;
import com.avensys.rts.jobservice.repository.CandidateRepository;
import com.avensys.rts.jobservice.repository.JobCandidateStageRepository;
import com.avensys.rts.jobservice.repository.JobRepository;
import com.avensys.rts.jobservice.repository.JobStageRepository;
import com.avensys.rts.jobservice.repository.JobTimelineRepository;
import com.avensys.rts.jobservice.repository.UserRepository;
import com.avensys.rts.jobservice.response.FormSubmissionsResponseDTO;
import com.avensys.rts.jobservice.response.HttpResponse;
import com.avensys.rts.jobservice.response.JobTimelineTagDTO;
import com.avensys.rts.jobservice.util.JobCanddateStageUtil;
import com.avensys.rts.jobservice.util.MappingUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

/**
 * @author Rahul Sahu
 * 
 */
@Service
public class JobCandidateStageService {

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private JobStageRepository jobStageRepository;

	@Autowired
	private CandidateRepository candidateRepository;

	@Autowired
	private JobCandidateStageRepository jobCandidateStageRepository;

	@Autowired
	private JobTimelineRepository jobTimelineRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FormSubmissionAPIClient formSubmissionAPIClient;

	@Autowired
	private EmailAPIClient emailAPIClient;

	@Autowired
	private MessageSource messageSource;

	private final Logger log = LoggerFactory.getLogger(JobCandidateStageService.class);

	/**
	 * @author Rahul Sahu
	 * @param jobCandidateStageEntity
	 */
	private void sendEmail(JobCandidateStageEntity jobCandidateStageEntity) {
		EmailMultiTemplateRequestDTO dto = new EmailMultiTemplateRequestDTO();
		dto.setTemplateName(JobCanddateStageUtil.JOB_ASSOCIATE_TEMPLATE);
		dto.setCategory(JobCanddateStageUtil.JOB_TEMPLATE_CATEGORY);

		JobEntity jobEntity = jobCandidateStageEntity.getJob();
		CandidateEntity candidateEntity = jobCandidateStageEntity.getCandidate();
		JobStageEntity jobStageEntity = jobCandidateStageEntity.getJobStage();
		HashMap<String, String> params = new HashMap<String, String>();

		// Associate
		if (jobStageEntity.getId() == JobCanddateStageUtil.ASSOCIATE
				&& jobCandidateStageEntity.getStatus().equals(JobCanddateStageUtil.COMPLETED)) {
			dto.setTemplateName(JobCanddateStageUtil.JOB_ASSOCIATE_TEMPLATE);
			dto.setTo(new String[] { candidateEntity.getCandidateSubmissionData().get("email").asText() });
			dto.setSubject("Avensys | Job Application for " + JobCanddateStageUtil.getValue(jobEntity, "jobTitle"));
			params.put("candidate.firstName", JobCanddateStageUtil.getValue(candidateEntity, "firstName"));
			params.put("candidate.lastName", JobCanddateStageUtil.getValue(candidateEntity, "lastName"));
			params.put("candidate.reasonForChange", JobCanddateStageUtil.getValue(candidateEntity, "reasonForChange"));
			// 2
			params.put("candidate.currentSalary",
					JobCanddateStageUtil.getValue(candidateEntity, "candidateCurrentSalary"));
			params.put("candidate.expectedSalary",
					JobCanddateStageUtil.getValue(candidateEntity, "candidateExpectedSalary"));
			params.put("candidate.noticePeriod", JobCanddateStageUtil.getValue(candidateEntity, "noticePeriod"));
			// 2e
			params.put("candidate.accountOwner", JobCanddateStageUtil.getValue(candidateEntity, ""));
			params.put("candidate.accountOwnerEmail", JobCanddateStageUtil.getValue(candidateEntity, ""));
			params.put("candidate.accountOwnerMobile", JobCanddateStageUtil.getValue(candidateEntity, ""));
			params.put("candidate.noticePeriod", JobCanddateStageUtil.getValue(candidateEntity, ""));

			params.put("job.jobTitle", JobCanddateStageUtil.getValue(jobEntity, ""));
			params.put("job.clientName", JobCanddateStageUtil.getValue(jobEntity, ""));
			params.put("job.jobType", JobCanddateStageUtil.getValue(jobEntity, ""));
			params.put("job.durationOfContract", JobCanddateStageUtil.getValue(jobEntity, ""));
			params.put("job.jobDescription", JobCanddateStageUtil.getValue(jobEntity, ""));

			dto.setTemplateMap(params);
		}

	}

	/**
	 * This method is used to save job Need to implement roll back if error occurs.
	 * 
	 * @param jobCandidateStageRequest
	 * @return
	 */
	@Transactional
	public JobCandidateStageEntity save(JobCandidateStageRequest jobCandidateStageRequest) throws ServiceException {

		Optional<JobCandidateStageEntity> jobCandidateStageOptional = jobCandidateStageRepository
				.findByJobAndStageAndCandidate(jobCandidateStageRequest.getJobId(),
						jobCandidateStageRequest.getJobStageId(), jobCandidateStageRequest.getCandidateId());

		Optional<JobEntity> jobOptional = jobRepository.findById(jobCandidateStageRequest.getJobId());

		Optional<JobStageEntity> jobStageOptional = jobStageRepository
				.findByOrder(jobCandidateStageRequest.getJobStageId());

		Optional<CandidateEntity> candidateOptional = candidateRepository
				.findById(jobCandidateStageRequest.getCandidateId());

		JobCandidateStageEntity jobCandidateStageEntity = null;

		if (jobCandidateStageOptional.isPresent()) {
			jobCandidateStageEntity = jobCandidateStageOptional.get();
			jobCandidateStageEntity.setJob(jobOptional.get());
			jobCandidateStageEntity.setJobStage(jobStageOptional.get());
			jobCandidateStageEntity.setCandidate(candidateOptional.get());
			jobCandidateStageEntity.setStatus(jobCandidateStageRequest.getStatus());
			jobCandidateStageEntity.setUpdatedBy(jobCandidateStageRequest.getUpdatedBy());
			jobCandidateStageEntity.setIsActive(true);
			jobCandidateStageEntity.setIsDeleted(false);
		} else {
			jobCandidateStageEntity = new JobCandidateStageEntity();
			jobCandidateStageEntity.setJob(jobOptional.get());
			jobCandidateStageEntity.setJobStage(jobStageOptional.get());
			jobCandidateStageEntity.setCandidate(candidateOptional.get());
			jobCandidateStageEntity.setStatus(jobCandidateStageRequest.getStatus());
			jobCandidateStageEntity.setCreatedBy(jobCandidateStageRequest.getCreatedBy());
			jobCandidateStageEntity.setUpdatedBy(jobCandidateStageRequest.getUpdatedBy());
			jobCandidateStageEntity.setIsActive(true);
			jobCandidateStageEntity.setIsDeleted(false);
		}

		// First Interview schedules
		if (jobCandidateStageRequest.getJobStageId() == JobCanddateStageUtil.FIRST_INTERVIEW_SCHEDULED_ID
				&& jobCandidateStageRequest.getStatus().equals(JobCanddateStageUtil.COMPLETED)) {

			Optional<JobStageEntity> profileOptional = jobStageRepository
					.findByOrder(JobCanddateStageUtil.PROFILE_FEEDBACK_PENDING_ID);

			Optional<JobCandidateStageEntity> profileStageOptional = jobCandidateStageRepository
					.findByJobAndStageAndCandidate(jobCandidateStageRequest.getJobId(), profileOptional.get().getId(),
							jobCandidateStageRequest.getCandidateId());
			if (profileStageOptional.isEmpty()) {
				JobCandidateStageEntity profileFeedbackEntity = new JobCandidateStageEntity();
				profileFeedbackEntity.setJob(jobOptional.get());
				profileFeedbackEntity.setJobStage(profileOptional.get());
				profileFeedbackEntity.setCandidate(candidateOptional.get());
				profileFeedbackEntity.setStatus(JobCanddateStageUtil.COMPLETED);
				profileFeedbackEntity.setCreatedBy(jobCandidateStageRequest.getCreatedBy());
				profileFeedbackEntity.setUpdatedBy(jobCandidateStageRequest.getUpdatedBy());
				profileFeedbackEntity.setIsActive(true);
				profileFeedbackEntity.setIsDeleted(false);
				jobCandidateStageRepository.save(profileFeedbackEntity);
			}
		}

		jobCandidateStageEntity = jobCandidateStageRepository.save(jobCandidateStageEntity);

		if (jobCandidateStageRequest.getFormId() != null) {
			FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
			formSubmissionsRequestDTO.setUserId(jobCandidateStageRequest.getCreatedBy());
			formSubmissionsRequestDTO.setFormId(jobCandidateStageRequest.getFormId());
			formSubmissionsRequestDTO
					.setSubmissionData(MappingUtil.convertJSONStringToJsonNode(jobCandidateStageRequest.getFormData()));
			formSubmissionsRequestDTO.setEntityId(jobCandidateStageEntity.getId());
			formSubmissionsRequestDTO.setEntityType(jobCandidateStageRequest.getJobType());

			if (jobCandidateStageOptional.isPresent()) {
				formSubmissionAPIClient.updateFormSubmission(jobCandidateStageEntity.getFormSubmissionId().intValue(),
						formSubmissionsRequestDTO);
			} else {
				HttpResponse formSubmissionResponse = formSubmissionAPIClient
						.addFormSubmission(formSubmissionsRequestDTO);
				FormSubmissionsResponseDTO formSubmissionData = MappingUtil
						.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

				jobCandidateStageEntity.setFormId(jobCandidateStageRequest.getFormId());
				jobCandidateStageEntity.setSubmissionData(formSubmissionsRequestDTO.getSubmissionData());
				jobCandidateStageEntity.setFormSubmissionId(formSubmissionData.getId());
				jobCandidateStageEntity = jobCandidateStageRepository.save(jobCandidateStageEntity);
			}
		}

		// Send Email
		try {
			sendEmail(jobCandidateStageEntity);
		} catch (Exception e) {
			log.error("Error:", e);
		}

		Optional<JobTimelineEntity> timelineoptional = jobTimelineRepository
				.findByJobAndCandidate(jobCandidateStageRequest.getJobId(), jobCandidateStageRequest.getCandidateId());

		JobTimelineEntity jobTimelineEntity = null;

		if (timelineoptional.isPresent()) {
			jobTimelineEntity = timelineoptional.get();
			jobTimelineEntity.setJob(jobOptional.get());
			jobTimelineEntity.setCandidate(candidateOptional.get());
			jobTimelineEntity.setUpdatedBy(jobCandidateStageRequest.getUpdatedBy());
		} else {
			jobTimelineEntity = new JobTimelineEntity();
			jobTimelineEntity.setJob(jobOptional.get());
			jobTimelineEntity.setCandidate(candidateOptional.get());
			jobTimelineEntity.setCreatedBy(jobCandidateStageRequest.getCreatedBy());
			jobTimelineEntity.setUpdatedBy(jobCandidateStageRequest.getUpdatedBy());
		}

		List<JobCandidateStageEntity> jobCandidateStageEntities = jobCandidateStageRepository
				.findByJobAndCandidate(jobCandidateStageRequest.getJobId(), jobCandidateStageRequest.getCandidateId());

		if (jobCandidateStageEntities.size() > 0) {
			List<JobStageEntity> jobStageEntities = jobStageRepository.findAllAndIsDeleted(false);

			HashMap<Long, JobCandidateStageEntity> candidateJobstages = new HashMap<Long, JobCandidateStageEntity>();
			jobCandidateStageEntities.forEach(item -> {
				Long id = item.getJobStage().getOrder();
				candidateJobstages.put(id, item);
			});

			HashMap<Long, JobStageEntity> jobStages = new HashMap<Long, JobStageEntity>();
			jobStageEntities.forEach(item -> {
				Long id = item.getOrder();
				jobStages.put(id, item);
			});

			HashMap<String, JobTimelineTagDTO> timeline = new HashMap<String, JobTimelineTagDTO>();

			for (long i = 1; i <= jobCandidateStageRequest.getJobStageId(); i++) {
				JobCandidateStageEntity ob = candidateJobstages.get(i);
				JobStageEntity job = jobStages.get(i);

				JobTimelineTagDTO jobTimelineTagDTO = new JobTimelineTagDTO();
				jobTimelineTagDTO.setOrder(job.getOrder());

				if (ob != null) {
					jobTimelineTagDTO.setDate(Timestamp.valueOf(ob.getUpdatedAt()));
					jobTimelineTagDTO.setStatus(ob.getStatus());
				} else {
					jobTimelineTagDTO.setDate(null);
					jobTimelineTagDTO.setStatus("SKIPPED");
				}
				timeline.put(job.getName(), jobTimelineTagDTO);
			}

			JsonNode timelineJson = new ObjectMapper().valueToTree(timeline);
			jobTimelineEntity.setTimeline(timelineJson);
			jobTimelineRepository.save(jobTimelineEntity);
		}
		return jobCandidateStageEntity;
	}

	public void delete(Long id) throws ServiceException {
		JobCandidateStageEntity dbUser = getById(id);
		dbUser.setIsDeleted(true);
		dbUser.setIsActive(false);
		jobCandidateStageRepository.save(dbUser);
	}

	public JobCandidateStageEntity getById(Long id) throws ServiceException {
		if (id == null) {
			throw new ServiceException(
					messageSource.getMessage("error.provide.id", new Object[] { id }, LocaleContextHolder.getLocale()));
		}
		Optional<JobCandidateStageEntity> jobCandidate = jobCandidateStageRepository.findById(id);
		if (jobCandidate.isPresent() && !jobCandidate.get().getIsDeleted()) {
			return jobCandidate.get();
		} else {
			throw new ServiceException(messageSource.getMessage("error.stagenotfound", new Object[] { id },
					LocaleContextHolder.getLocale()));
		}
	}

	public List<JobCandidateStageEntity> getAll() {
		List<JobCandidateStageEntity> jobStageList = jobCandidateStageRepository.findAllAndIsDeleted(false);
		return jobStageList;
	}

	public List<JobCandidateStageEntity> getAll(Integer pageNo, Integer pageSize, String sortBy)
			throws ServiceException {
		try {
			Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
			List<JobCandidateStageEntity> jobCandidateStageEntities = jobCandidateStageRepository
					.findAllAndIsDeleted(false, paging);
			return jobCandidateStageEntities;
		} catch (Exception e) {
			throw new ServiceException(e.getLocalizedMessage());
		}
	}

}
