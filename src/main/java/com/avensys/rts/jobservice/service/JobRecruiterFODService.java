package com.avensys.rts.jobservice.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.avensys.rts.jobservice.apiclient.EmailAPIClient;
import com.avensys.rts.jobservice.email.executerservice.JobFODEmailThread;
import com.avensys.rts.jobservice.email.executerservice.JobFODMailService;
import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.entity.JobRecruiterFODEntity;
import com.avensys.rts.jobservice.entity.UserEntity;
import com.avensys.rts.jobservice.exception.ServiceException;
import com.avensys.rts.jobservice.payload.EmailMultiTemplateRequestDTO;
import com.avensys.rts.jobservice.payload.JobRecruiterFODRequest;
import com.avensys.rts.jobservice.repository.JobRecruiterFODRepository;
import com.avensys.rts.jobservice.repository.JobRepository;
import com.avensys.rts.jobservice.repository.UserRepository;
import com.avensys.rts.jobservice.util.JobCanddateStageUtil;
import com.avensys.rts.jobservice.util.JobFODUtil;

/**
 * @author Rahul Sahu
 * @description Assign/Unassign JOB FOD service
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
	private JobFODMailService jobFODMailService;

	@Autowired
	private EmailAPIClient emailAPIClient;

	@Autowired
	private MessageSource messageSource;

	private final Logger log = LoggerFactory.getLogger(JobRecruiterFODService.class);

	private JobFODEmailThread getEmailThread(Boolean isAssign, JobEntity jobEntity, UserEntity seller,
			LinkedList<String> recruiters) {
		EmailMultiTemplateRequestDTO dto = new EmailMultiTemplateRequestDTO();
		HashMap<String, String> params = new HashMap<String, String>();

		dto.setCategory(JobFODUtil.JOB_TEMPLATE_CATEGORY);
		if (isAssign) {
			dto.setSubject("FOD has been assigned");
			dto.setTemplateName(JobFODUtil.FOD_ASSIGN);
		} else {
			dto.setSubject("FOD has been unassigned");
			dto.setTemplateName(JobFODUtil.FOD_UNASSIGN);
		}

		dto.setTo(recruiters.toArray(String[]::new));
		dto.setCc(new String[] { seller.getEmail() });

		params.put("job.jobTitle", JobCanddateStageUtil.getValue(jobEntity, "jobTitle"));
		params.put("sales.firstName", JobCanddateStageUtil.validateValue(seller.getFirstName()));
		params.put("sales.lastName", JobCanddateStageUtil.validateValue(seller.getLastName()));
		params.put("sales.email", JobCanddateStageUtil.validateValue(seller.getEmail()));
		dto.setTemplateMap(params);

		JobFODEmailThread thread = new JobFODEmailThread(dto, emailAPIClient);
		return thread;
	}

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

			Set<JobFODEmailThread> emailTasks = new HashSet<JobFODEmailThread>();

			Arrays.stream(jobRecruiterFODRequest.getJobId()).forEach(id -> {

				Optional<JobEntity> jobOptional = jobRepository.findById(id);
				Optional<UserEntity> sellerOptional = userRepository.findById(jobRecruiterFODRequest.getSellerId());

				LinkedList<String> recruiters = new LinkedList<String>();

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
						jobRecruiterFODEntity.setUpdatedAt(LocalDateTime.now());
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
					jobRecruiterFODEntity = jobRecruiterFODRepository.save(jobRecruiterFODEntity);
					recruiterOptional.ifPresent((recruiter) -> recruiters.add(recruiter.getEmail()));
				});

				if (jobOptional.isPresent() && sellerOptional.isPresent() && recruiters.size() > 0) {
					emailTasks.add(getEmailThread(true, jobOptional.get(), sellerOptional.get(), recruiters));
				}
			});
			jobFODMailService.sendEmails(emailTasks);
		} else {
			throw new ServiceException(
					messageSource.getMessage("error.provide.jobandrecriter", null, LocaleContextHolder.getLocale()));
		}
	}

	public void deleteByJobId(Long id) throws ServiceException {
		try {
			LinkedList<String> recruiters = new LinkedList<String>();

			Set<JobFODEmailThread> emailTasks = new HashSet<JobFODEmailThread>();

			List<JobRecruiterFODEntity> data = jobRecruiterFODRepository.getAllByJobId(id);

			if (data != null && data.size() > 0) {
				data.stream().forEach(jfod -> {
					recruiters.add(jfod.getRecruiter().getEmail());
				});
				emailTasks.add(getEmailThread(false, data.get(0).getJob(), data.get(0).getSeller(), recruiters));
			}
			jobFODMailService.sendEmails(emailTasks);
		} catch (Exception e) {
			log.error("Error:", e);
		}

		jobRecruiterFODRepository.deleteByJobId(id);
	}

}
