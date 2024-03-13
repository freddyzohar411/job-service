package com.avensys.rts.jobservice.service;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.entity.JobRecruiterFODEntity;
import com.avensys.rts.jobservice.entity.UserEntity;
import com.avensys.rts.jobservice.exception.ServiceException;
import com.avensys.rts.jobservice.payload.JobRecruiterFODRequest;
import com.avensys.rts.jobservice.repository.JobRecruiterFODRepository;
import com.avensys.rts.jobservice.repository.JobRepository;
import com.avensys.rts.jobservice.repository.UserRepository;

import jakarta.transaction.Transactional;

/**
 * @author Rahul Sahu
 * 
 */
@Service
@Transactional
public class JobRecruiterFODService {

	@Autowired
	private JobRecruiterFODRepository jobRecruiterFODRepository;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MessageSource messageSource;

	/**
	 * This method is used to save job Need to implement roll back if error occurs.
	 * 
	 * @param jobRecruiterFODRequest
	 * @return
	 */
	public void save(JobRecruiterFODRequest jobRecruiterFODRequest) throws ServiceException {
		// add check for title exists in a DB

		if (jobRecruiterFODRequest.getJobId() != null && jobRecruiterFODRequest.getJobId().length > 0
				&& jobRecruiterFODRequest.getRecruiterId() != null
				&& jobRecruiterFODRequest.getRecruiterId().length > 0) {
			Arrays.stream(jobRecruiterFODRequest.getJobId()).forEach(id -> {

				Optional<JobEntity> jobOptional = jobRepository.findById(id);
				Optional<UserEntity> sellerOptional = userRepository.findById(jobRecruiterFODRequest.getSellerId());

				Arrays.stream(jobRecruiterFODRequest.getRecruiterId()).forEach(recId -> {
					Optional<JobRecruiterFODEntity> jobFODOptional = jobRecruiterFODRepository.findByJobAndRecruiter(id,
							recId);
					Optional<UserEntity> recruiterOptional = userRepository.findById(recId);

					JobRecruiterFODEntity jobRecruiterFODEntity = null;

					if (jobFODOptional.isPresent()) {
						jobRecruiterFODEntity = jobFODOptional.get();
						jobRecruiterFODEntity.setJob(jobOptional.get());
						jobRecruiterFODEntity.setRecruiter(recruiterOptional.get());
						jobRecruiterFODEntity.setSeller(sellerOptional.get());
						jobRecruiterFODEntity.setStatus("FOD");
						jobRecruiterFODEntity.setUpdatedBy(jobRecruiterFODRequest.getUpdatedBy());
						jobRecruiterFODEntity.setIsActive(true);
						jobRecruiterFODEntity.setIsDeleted(false);
					} else {
						jobRecruiterFODEntity = new JobRecruiterFODEntity();
						jobRecruiterFODEntity.setJob(jobOptional.get());
						jobRecruiterFODEntity.setRecruiter(recruiterOptional.get());
						jobRecruiterFODEntity.setSeller(sellerOptional.get());
						jobRecruiterFODEntity.setStatus("FOD");
						jobRecruiterFODEntity.setCreatedBy(jobRecruiterFODRequest.getCreatedBy());
						jobRecruiterFODEntity.setUpdatedBy(jobRecruiterFODRequest.getUpdatedBy());
						jobRecruiterFODEntity.setIsActive(true);
						jobRecruiterFODEntity.setIsDeleted(false);
					}
					jobRecruiterFODRepository.save(jobRecruiterFODEntity);
				});
			});
		} else {
			throw new ServiceException(
					messageSource.getMessage("error.provide.jobandrecriter", null, LocaleContextHolder.getLocale()));
		}
	}

	public void deleteByJobId(Long id) throws ServiceException {
		jobRecruiterFODRepository.deleteByJobId(id);
	}

}
