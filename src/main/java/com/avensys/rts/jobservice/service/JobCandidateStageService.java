package com.avensys.rts.jobservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.avensys.rts.jobservice.entity.CandidateEntity;
import com.avensys.rts.jobservice.entity.JobCandidateStageEntity;
import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.entity.JobStageEntity;
import com.avensys.rts.jobservice.exception.ServiceException;
import com.avensys.rts.jobservice.payload.JobCandidateStageRequest;
import com.avensys.rts.jobservice.repository.CandidateRepository;
import com.avensys.rts.jobservice.repository.JobCandidateStageRepository;
import com.avensys.rts.jobservice.repository.JobRepository;
import com.avensys.rts.jobservice.repository.JobStageRepository;

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
			jobCandidateStageEntity.setStatus("COMPLETED");
			jobCandidateStageEntity.setUpdatedBy(jobCandidateStageRequest.getUpdatedBy());
			jobCandidateStageEntity.setIsActive(true);
			jobCandidateStageEntity.setIsDeleted(false);
		} else {
			jobCandidateStageEntity = new JobCandidateStageEntity();
			jobCandidateStageEntity.setJob(jobOptional.get());
			jobCandidateStageEntity.setJobStage(jobStageOptional.get());
			jobCandidateStageEntity.setCandidate(candidateOptional.get());
			jobCandidateStageEntity.setStatus("COMPLETED");
			jobCandidateStageEntity.setCreatedBy(jobCandidateStageRequest.getCreatedBy());
			jobCandidateStageEntity.setUpdatedBy(jobCandidateStageRequest.getUpdatedBy());
			jobCandidateStageEntity.setIsActive(true);
			jobCandidateStageEntity.setIsDeleted(false);
		}

		jobCandidateStageEntity = jobCandidateStageRepository.save(jobCandidateStageEntity);

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

}
