package com.avensys.rts.jobservice.service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.avensys.rts.jobservice.apiclient.FormSubmissionAPIClient;
import com.avensys.rts.jobservice.entity.CandidateEntity;
import com.avensys.rts.jobservice.entity.JobCandidateStageEntity;
import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.entity.JobStageEntity;
import com.avensys.rts.jobservice.entity.JobTimelineEntity;
import com.avensys.rts.jobservice.exception.ServiceException;
import com.avensys.rts.jobservice.payload.FormSubmissionsRequestDTO;
import com.avensys.rts.jobservice.payload.JobCandidateStageRequest;
import com.avensys.rts.jobservice.repository.CandidateRepository;
import com.avensys.rts.jobservice.repository.JobCandidateStageRepository;
import com.avensys.rts.jobservice.repository.JobRepository;
import com.avensys.rts.jobservice.repository.JobStageRepository;
import com.avensys.rts.jobservice.repository.JobTimelineRepository;
import com.avensys.rts.jobservice.response.FormSubmissionsResponseDTO;
import com.avensys.rts.jobservice.response.HttpResponse;
import com.avensys.rts.jobservice.response.JobTimelineTagDTO;
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
	private FormSubmissionAPIClient formSubmissionAPIClient;

	@Autowired
	private MessageSource messageSource;

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
				.findById(jobCandidateStageRequest.getJobStageId());

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
				Long id = item.getJobStage().getId();
				candidateJobstages.put(id, item);
			});

			HashMap<Long, JobStageEntity> jobStages = new HashMap<Long, JobStageEntity>();
			jobStageEntities.forEach(item -> {
				Long id = item.getId();
				jobStages.put(id, item);
			});

			HashMap<String, JobTimelineTagDTO> timeline = new HashMap<String, JobTimelineTagDTO>();

			for (long i = 1; i <= jobCandidateStageRequest.getJobStageId(); i++) {
				JobCandidateStageEntity ob = candidateJobstages.get(i);
				JobStageEntity jOb = jobStages.get(i);

				JobTimelineTagDTO jobTimelineTagDTO = new JobTimelineTagDTO();
				jobTimelineTagDTO.setOrder(jOb.getOrder());

				if (ob != null) {
					jobTimelineTagDTO.setDate(Timestamp.valueOf(ob.getUpdatedAt()));
					jobTimelineTagDTO.setStatus(ob.getStatus());
				} else {
					jobTimelineTagDTO.setDate(null);
					jobTimelineTagDTO.setStatus("SKIPPED");
				}
				timeline.put(jOb.getName(), jobTimelineTagDTO);
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
