package com.avensys.rts.jobservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.avensys.rts.jobservice.util.JobUtil;

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
			LinkedList<UserEntity> recruiters) {
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

		String emails[] = recruiters.stream().map(rct -> rct.getEmail()).toArray(String[]::new);

		dto.setTo(emails);
		dto.setCc(new String[] { seller.getEmail() });

		// Get a list of all the key in the job submission data
		List<String> jobSubmissionDataKeys = new ArrayList<>();
		if (jobEntity.getJobSubmissionData() != null) {
			Iterator<String> jobFieldNames = jobEntity.getJobSubmissionData().fieldNames();
			while (jobFieldNames.hasNext()) {
				String fieldName = jobFieldNames.next();
				jobSubmissionDataKeys.add(fieldName);
			}
		}

		// loop and add the job submission data to the params
		for (String key : jobSubmissionDataKeys) {
			params.put("Jobs.jobInfo." + key, JobUtil.getValue(jobEntity, key));
		}

		// Get AccountOwner Name if exists
		String accountOwner = JobUtil.getValue(jobEntity, "accountOwner");
		HashMap<String, String> accountOwnerData = new HashMap<>();
		if (accountOwner != null) {
			accountOwnerData = extractAccountOwnerDetails(accountOwner);
			params.put("Jobs.jobInfo.accountOwner", accountOwnerData.get("accountName"));
		}

		params.put("Sales", JobCanddateStageUtil.validateValue(seller.getFirstName()) + " "
				+ JobCanddateStageUtil.validateValue(seller.getLastName()));

		dto.setTemplateMap(params);

		JobFODEmailThread thread = new JobFODEmailThread(dto, emailAPIClient);
		return thread;
	}

	private HashMap<String, String> extractAccountOwnerDetails(String accountOwner) {
		HashMap<String, String> accountOwnerDetails = new HashMap<>();
		try {
			String[] accountOwnerArray = accountOwner.split("\\(");
			accountOwnerDetails.put("accountName", accountOwnerArray[0].trim());
			accountOwnerDetails.put("email", accountOwnerArray[1].replace(")", "").trim());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return accountOwnerDetails;
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

				LinkedList<UserEntity> recruiters = new LinkedList<UserEntity>();

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
					recruiterOptional.ifPresent((recruiter) -> recruiters.add(recruiter));
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
			LinkedList<UserEntity> recruiters = new LinkedList<UserEntity>();

			Set<JobFODEmailThread> emailTasks = new HashSet<JobFODEmailThread>();

			List<JobRecruiterFODEntity> data = jobRecruiterFODRepository.getAllByJobId(id);

			if (data != null && data.size() > 0) {
				data.stream().forEach(jfod -> {
					recruiters.add(jfod.getRecruiter());
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
