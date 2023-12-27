package com.avensys.rts.jobservice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
public class JobRecruiterFODService {

	@Autowired
	private JobRecruiterFODRepository jobRecruiterFODRepository;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private UserRepository userRepository;

	/**
	 * This method is used to save job Need to implement roll back if error occurs.
	 * 
	 * @param jobRecruiterFODRequest
	 * @return
	 */
	@Transactional
	public JobRecruiterFODEntity save(JobRecruiterFODRequest jobRecruiterFODRequest) throws ServiceException {
		// add check for title exists in a DB

		Optional<JobRecruiterFODEntity> jobFODOptional = jobRecruiterFODRepository
				.findByJob(jobRecruiterFODRequest.getJobId());

		Optional<JobEntity> jobOptional = jobRepository.findById(jobRecruiterFODRequest.getJobId());

		Optional<UserEntity> recruiterOptional = userRepository.findById(jobRecruiterFODRequest.getRecruiterId());

		Optional<UserEntity> sellerOptional = userRepository.findById(jobRecruiterFODRequest.getSellerId());

		JobRecruiterFODEntity jobRecruiterFODEntity = null;

		if (jobFODOptional.isPresent()) {
			jobRecruiterFODEntity = jobFODOptional.get();
			jobRecruiterFODEntity.setJob(jobOptional.get());
			jobRecruiterFODEntity.setRecruiter(recruiterOptional.get());
			jobRecruiterFODEntity.setSeller(sellerOptional.get());
			jobRecruiterFODEntity.setUpdatedBy(jobRecruiterFODRequest.getUpdatedBy());
			jobRecruiterFODEntity.setIsActive(true);
			jobRecruiterFODEntity.setIsDeleted(false);
		} else {
			jobRecruiterFODEntity = new JobRecruiterFODEntity();
			jobRecruiterFODEntity.setJob(jobOptional.get());
			jobRecruiterFODEntity.setRecruiter(recruiterOptional.get());
			jobRecruiterFODEntity.setSeller(sellerOptional.get());
			jobRecruiterFODEntity.setCreatedBy(jobRecruiterFODRequest.getCreatedBy());
			jobRecruiterFODEntity.setUpdatedBy(jobRecruiterFODRequest.getUpdatedBy());
			jobRecruiterFODEntity.setIsActive(true);
			jobRecruiterFODEntity.setIsDeleted(false);
		}

		jobRecruiterFODEntity = jobRecruiterFODRepository.save(jobRecruiterFODEntity);

		return jobRecruiterFODEntity;
	}

}
