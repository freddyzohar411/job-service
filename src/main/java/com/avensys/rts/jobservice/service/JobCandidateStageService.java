package com.avensys.rts.jobservice.service;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.avensys.rts.jobservice.apiclient.DocumentAPIClient;
import com.avensys.rts.jobservice.apiclient.EmailAPIClient;
import com.avensys.rts.jobservice.apiclient.FormSubmissionAPIClient;
import com.avensys.rts.jobservice.entity.CandidateEntity;
import com.avensys.rts.jobservice.entity.JobCandidateStageEntity;
import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.entity.JobStageEntity;
import com.avensys.rts.jobservice.entity.JobTimelineEntity;
import com.avensys.rts.jobservice.entity.TosEntity;
import com.avensys.rts.jobservice.entity.UserEntity;
import com.avensys.rts.jobservice.exception.ServiceException;
import com.avensys.rts.jobservice.payload.EmailMultiRequestDTO;
import com.avensys.rts.jobservice.payload.EmailMultiTemplateRequestDTO;
import com.avensys.rts.jobservice.payload.FormSubmissionsRequestDTO;
import com.avensys.rts.jobservice.payload.JobCandidateStageGetRequest;
import com.avensys.rts.jobservice.payload.JobCandidateStageRequest;
import com.avensys.rts.jobservice.payload.JobCandidateStageWithAttachmentsRequest;
import com.avensys.rts.jobservice.payload.JobCandidateStageWithFilesRequest;
import com.avensys.rts.jobservice.repository.CandidateRepository;
import com.avensys.rts.jobservice.repository.JobCandidateStageRepository;
import com.avensys.rts.jobservice.repository.JobRepository;
import com.avensys.rts.jobservice.repository.JobStageRepository;
import com.avensys.rts.jobservice.repository.JobTimelineRepository;
import com.avensys.rts.jobservice.repository.TosRepository;
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

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private TosRepository tosRepository;

	@Autowired
	private ConditionalOfferService conditionalOfferService;

	@Autowired
	private DocumentAPIClient documentAPIClient;

	@Autowired
	private TosService tosService;

	private final Logger log = LoggerFactory.getLogger(JobCandidateStageService.class);

	/**
	 * @author Rahul Sahu
	 * @param jobCandidateStageEntity
	 * @description TO send an email on one step completion of job time-line.
	 */
	private void sendEmail(JobCandidateStageEntity jobCandidateStageEntity) {
		EmailMultiTemplateRequestDTO dto = new EmailMultiTemplateRequestDTO();
		dto.setCategory(JobCanddateStageUtil.JOB_TEMPLATE_CATEGORY);

		JobEntity jobEntity = jobCandidateStageEntity.getJob();
		CandidateEntity candidateEntity = jobCandidateStageEntity.getCandidate();
		JobStageEntity jobStageEntity = jobCandidateStageEntity.getJobStage();
		JsonNode submissionData = jobCandidateStageEntity.getSubmissionData();

		HashMap<String, String> params = new HashMap<String, String>();

		// Candidate params
		UserEntity client = null;
		String clientName = null;
		try {
			Long ownerId = Long.parseLong(JobCanddateStageUtil.getValue(jobEntity, "accountOwnerId"));
			client = userRepository.findById(ownerId).get();
			clientName = client.getFirstName() + " " + client.getLastName();

			params.put("candidate.accountOwner", JobCanddateStageUtil.validateValue(clientName));
			params.put("candidate.accountOwnerEmail", JobCanddateStageUtil.validateValue(client.getEmail()));
			params.put("candidate.accountOwnerMobile", JobCanddateStageUtil.validateValue(client.getMobile()));
		} catch (Exception e) {
			log.error("Error:", e);
		}
		params.put("candidate.firstName", JobCanddateStageUtil.getValue(candidateEntity, "firstName"));
		params.put("candidate.lastName", JobCanddateStageUtil.getValue(candidateEntity, "lastName"));
		params.put("candidate.gender", JobCanddateStageUtil.getValue(candidateEntity, "gender"));
		params.put("candidate.phoneCode", JobCanddateStageUtil.getValue(candidateEntity, "phoneCode"));
		params.put("candidate.phone", JobCanddateStageUtil.getValue(candidateEntity, "phone"));
		params.put("candidate.email", JobCanddateStageUtil.getValue(candidateEntity, "email"));
		params.put("candidate.candidateNationality",
				JobCanddateStageUtil.getValue(candidateEntity, "candidateNationality"));
		params.put("candidate.visaStatus", JobCanddateStageUtil.getValue(candidateEntity, "visaStatus"));
		params.put("candidate.currentLocation", JobCanddateStageUtil.getValue(candidateEntity, "currentLocation"));

		params.put("candidate.profileSummary", JobCanddateStageUtil.getValue(candidateEntity, "profileSummary"));
		params.put("candidate.currentPositionTitle",
				JobCanddateStageUtil.getValue(candidateEntity, "currentPositionTitle"));
		params.put("candidate.totalExperience", JobCanddateStageUtil.getValue(candidateEntity, "totalExperience"));
		params.put("candidate.relevantExperience",
				JobCanddateStageUtil.getValue(candidateEntity, "relevantExperience"));
		params.put("candidate.currentPositionTitle",
				JobCanddateStageUtil.getValue(candidateEntity, "currentPositionTitle"));
		params.put("candidate.currentEmployer", JobCanddateStageUtil.getValue(candidateEntity, "currentEmployer"));
		params.put("candidate.primarySkill", JobCanddateStageUtil.getValue(candidateEntity, "primarySkill"));

		params.put("candidate.reasonForChange", JobCanddateStageUtil.getValue(candidateEntity, "reasonForChange"));
		params.put("candidate.currentSalary", JobCanddateStageUtil.getValue(candidateEntity, "candidateCurrentSalary"));
		params.put("candidate.expectedSalary",
				JobCanddateStageUtil.getValue(candidateEntity, "candidateExpectedSalary"));
		params.put("candidate.noticePeriod", JobCanddateStageUtil.getValue(candidateEntity, "noticePeriod"));

		// Job Params
		params.put("job.clientName", JobCanddateStageUtil.getValue(jobEntity, "clientName"));
		params.put("job.jobTitle", JobCanddateStageUtil.getValue(jobEntity, "jobTitle"));
		params.put("job.jobType", JobCanddateStageUtil.getValue(jobEntity, "jobType"));
		params.put("job.durationOfContract", JobCanddateStageUtil.getValue(jobEntity, "duration"));
		params.put("job.jobDescription", JobCanddateStageUtil.getValue(jobEntity, "jobDescription"));

		try {
			if (submissionData != null
					&& (jobStageEntity.getName().equals(JobCanddateStageUtil.FIRST_INTERVIEW_SCHEDULED_NAME)
							|| jobStageEntity.getName().equals(JobCanddateStageUtil.SECOND_INTERVIEW_SCHEDULED_NAME)
							|| jobStageEntity.getName().equals(JobCanddateStageUtil.THIRD_INTERVIEW_SCHEDULED_NAME))
					&& jobCandidateStageEntity.getStatus().equals(JobCanddateStageUtil.COMPLETED)) {
				params.put("interviews.interviewDate",
						JobCanddateStageUtil.validateValue(submissionData.get("interviewDate").asText()));
				params.put("interviews.interviewTimeEnd",
						JobCanddateStageUtil.validateValue(submissionData.get("interviewTimeEnd").asText()));
				params.put("interviews.interviewTimeStart",
						JobCanddateStageUtil.validateValue(submissionData.get("interviewTimeStart").asText()));
				params.put("interviews.location",
						JobCanddateStageUtil.validateValue(submissionData.get("location").asText()));
				params.put("interviews.interviewer",
						JobCanddateStageUtil.validateValue(submissionData.get("interviewer").asText()));
				params.put("interviews.interviewerContactNumber",
						JobCanddateStageUtil.validateValue(submissionData.get("interviewerContactNumber").asText()));
			}
		} catch (Exception e) {
			log.error("Error:", e);
		}

		if (jobCandidateStageEntity.getStatus().equals(JobCanddateStageUtil.REJECTED)) {
			// Associate
			dto.setTemplateName(JobCanddateStageUtil.REJECT_TEMPLATE);
			dto.setTo(new String[] { candidateEntity.getCandidateSubmissionData().get("email").asText() });
			dto.setSubject("Interview Feedback");
		} else if (jobCandidateStageEntity.getStatus().equals(JobCanddateStageUtil.WITHDRAWN)) {
			// Associate
			dto.setTemplateName(JobCanddateStageUtil.WITHDRAWN_TEMPLATE);
			dto.setTo(new String[] { jobEntity.getJobSubmissionData().get("clientEmail").asText(),
					params.get("candidate.accountOwnerEmail") });
			dto.setSubject("Candidate has withdrawn their application");
		} else if (jobStageEntity.getName().equals(JobCanddateStageUtil.ASSOCIATE_TEMPLATE)
				&& jobCandidateStageEntity.getStatus().equals(JobCanddateStageUtil.COMPLETED)) {
			// Associate
			dto.setTemplateName(JobCanddateStageUtil.ASSOCIATE_TEMPLATE);
			dto.setTo(new String[] { candidateEntity.getCandidateSubmissionData().get("email").asText() });
			dto.setSubject("Avensys | Job Application for " + JobCanddateStageUtil.getValue(jobEntity, "jobTitle"));
		} else if (jobStageEntity.getName().equals(JobCanddateStageUtil.SUBMIT_TO_SALES_TEMPLATE)
				&& jobCandidateStageEntity.getStatus().equals(JobCanddateStageUtil.COMPLETED)
				&& params.get("candidate.accountOwnerEmail") != null) {
			// Submit to Sales
			dto.setTemplateName(JobCanddateStageUtil.SUBMIT_TO_SALES_TEMPLATE);
			dto.setTo(new String[] { params.get("candidate.accountOwnerEmail") });
			dto.setSubject(clientName + "_" + JobCanddateStageUtil.getValue(jobEntity, "jobTitle") + "_"
					+ JobCanddateStageUtil.getValue(candidateEntity, "firstName") + " "
					+ JobCanddateStageUtil.getValue(candidateEntity, "lastName"));
		} else if (jobStageEntity.getName().equals(JobCanddateStageUtil.SUBMIT_TO_CLIENT_TEMPLATE)
				&& jobCandidateStageEntity.getStatus().equals(JobCanddateStageUtil.COMPLETED)) {
			// Submit to Client
			dto.setTemplateName(JobCanddateStageUtil.SUBMIT_TO_CLIENT_TEMPLATE);
			dto.setTo(new String[] { jobEntity.getJobSubmissionData().get("clientEmail").asText() });
			dto.setSubject(clientName + "_" + JobCanddateStageUtil.getValue(jobEntity, "jobTitle") + "_"
					+ JobCanddateStageUtil.getValue(candidateEntity, "firstName") + " "
					+ JobCanddateStageUtil.getValue(candidateEntity, "lastName"));
		} else if ((jobStageEntity.getName().equals(JobCanddateStageUtil.FIRST_INTERVIEW_SCHEDULED_NAME)
				|| jobStageEntity.getName().equals(JobCanddateStageUtil.SECOND_INTERVIEW_SCHEDULED_NAME)
				|| jobStageEntity.getName().equals(JobCanddateStageUtil.THIRD_INTERVIEW_SCHEDULED_NAME))
				&& jobCandidateStageEntity.getStatus().equals(JobCanddateStageUtil.COMPLETED)
				&& params.get("candidate.accountOwnerEmail") != null) {
			// Interview Scheduled
			dto.setTemplateName(JobCanddateStageUtil.INTERVIEW_SCHEDULED_TEMPLATE);
			dto.setTo(new String[] { jobEntity.getJobSubmissionData().get("clientEmail").asText(),
					params.get("candidate.accountOwnerEmail"),
					candidateEntity.getCandidateSubmissionData().get("email").asText() });
			dto.setSubject("Appointment | Interview for " + JobCanddateStageUtil.getValue(jobEntity, "jobTitle")
					+ " by Avensys");
		} else if (jobStageEntity.getName().equals(JobCanddateStageUtil.CONDITIONAL_OFFER_SENT_NAME)
				&& jobCandidateStageEntity.getStatus().equals(JobCanddateStageUtil.COMPLETED)
				&& params.get("candidate.accountOwnerEmail") != null) {
			// Interview Scheduled
			dto.setTemplateName(JobCanddateStageUtil.CONDITIONAL_OFFER_TEMPLATE);
			dto.setTo(new String[] { jobEntity.getJobSubmissionData().get("clientEmail").asText(),
					params.get("candidate.accountOwnerEmail"),
					candidateEntity.getCandidateSubmissionData().get("email").asText() });
			dto.setSubject("[AVENSYS CONSULTING] Job Confirmation for "
					+ JobCanddateStageUtil.getValue(candidateEntity, "firstName") + " "
					+ JobCanddateStageUtil.getValue(candidateEntity, "lastName") + " with "
					+ JobCanddateStageUtil.validateValue(params.get("job.clientName")));

			try {
				Resource resource = resourceLoader.getResource("classpath:ViewAttachment.pdf");
				File file = resource.getFile();
				MultipartFile multipartFile = new MockMultipartFile("ViewAttachment.pdf", new FileInputStream(file));

//				dto.setAttachments(new MultipartFile[] { multipartFile });
			} catch (Exception e) {
				log.error("Error:", e);
			}
		} else if (jobStageEntity.getName().equals(JobCanddateStageUtil.CONDITIONAL_OFFER_ACCEPTED_NAME)
				&& jobCandidateStageEntity.getStatus().equals(JobCanddateStageUtil.COMPLETED)
				&& params.get("candidate.accountOwnerEmail") != null) {
			// Interview Scheduled
			dto.setTemplateName(JobCanddateStageUtil.OFFER_ACCEPTED_TEMPLATE);
			dto.setTo(new String[] { jobEntity.getJobSubmissionData().get("clientEmail").asText(),
					params.get("candidate.accountOwnerEmail"),
					candidateEntity.getCandidateSubmissionData().get("email").asText() });
			dto.setSubject("Avensys Notification | Job Offer Acceptance");
		}

		dto.setTemplateMap(params);
		emailAPIClient.sendEmailServiceTemplate(dto);
	}

	private void sendEmailWithAttachment(EmailMultiRequestDTO emailRequest) {
		emailAPIClient.sendEmail(emailRequest);
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

		// Modify candidate isTagged
		if (jobCandidateStageRequest.getStatus().equals(JobCanddateStageUtil.REJECTED)
				|| jobCandidateStageRequest.getStatus().equals(JobCanddateStageUtil.WITHDRAWN)) {
			candidateOptional.get().setTagged(false);
			candidateRepository.save(candidateOptional.get());
		} else {
			candidateOptional.get().setTagged(true);
			candidateRepository.save(candidateOptional.get());
		}

		// Coding Test In progress
		if (jobCandidateStageRequest.getJobStageId() == JobCanddateStageUtil.CODING_TEST_ID
				&& jobCandidateStageRequest.getStatus().equals(JobCanddateStageUtil.IN_PROGRESS)) {

			Optional<JobStageEntity> skillsAssessmentOptional = jobStageRepository
					.findByOrder(JobCanddateStageUtil.SKILLS_ASSESSMENT_ID);

			Optional<JobCandidateStageEntity> skillsAssessmentStageOptional = jobCandidateStageRepository
					.findByJobAndStageAndCandidate(jobCandidateStageRequest.getJobId(),
							skillsAssessmentOptional.get().getOrder(), jobCandidateStageRequest.getCandidateId());
			if (skillsAssessmentStageOptional.isPresent()) {
				JobCandidateStageEntity skillsAssessmentEntity = skillsAssessmentStageOptional.get();
				skillsAssessmentEntity.setStatus(JobCanddateStageUtil.COMPLETED);
				skillsAssessmentEntity.setUpdatedBy(jobCandidateStageRequest.getUpdatedBy());
				skillsAssessmentEntity.setIsActive(true);
				skillsAssessmentEntity.setIsDeleted(false);
				jobCandidateStageRepository.save(skillsAssessmentEntity);
			} else {
				JobCandidateStageEntity skillsAssessmentEntity = new JobCandidateStageEntity();
				skillsAssessmentEntity.setJob(jobOptional.get());
				skillsAssessmentEntity.setJobStage(skillsAssessmentOptional.get());
				skillsAssessmentEntity.setCandidate(candidateOptional.get());
				skillsAssessmentEntity.setStatus(JobCanddateStageUtil.COMPLETED);
				skillsAssessmentEntity.setCreatedBy(jobCandidateStageRequest.getCreatedBy());
				skillsAssessmentEntity.setUpdatedBy(jobCandidateStageRequest.getUpdatedBy());
				skillsAssessmentEntity.setIsActive(true);
				skillsAssessmentEntity.setIsDeleted(false);
				jobCandidateStageRepository.save(skillsAssessmentEntity);
			}
		} else if (jobCandidateStageRequest.getJobStageId() == JobCanddateStageUtil.TECHNICAL_INTERVIEW_ID
				&& jobCandidateStageRequest.getStatus().equals(JobCanddateStageUtil.IN_PROGRESS)) {
			// Technical Interview In progress
			Optional<JobStageEntity> codingTestOptional = jobStageRepository
					.findByOrder(JobCanddateStageUtil.CODING_TEST_ID);

			Optional<JobCandidateStageEntity> codingTestStageOptional = jobCandidateStageRepository
					.findByJobAndStageAndCandidate(jobCandidateStageRequest.getJobId(),
							codingTestOptional.get().getOrder(), jobCandidateStageRequest.getCandidateId());
			if (codingTestStageOptional.isPresent()) {
				JobCandidateStageEntity codingTestEntity = codingTestStageOptional.get();
				codingTestEntity.setStatus(JobCanddateStageUtil.COMPLETED);
				codingTestEntity.setUpdatedBy(jobCandidateStageRequest.getUpdatedBy());
				codingTestEntity.setIsActive(true);
				codingTestEntity.setIsDeleted(false);
				jobCandidateStageRepository.save(codingTestEntity);
			} else {
				JobCandidateStageEntity codingTestEntity = new JobCandidateStageEntity();
				codingTestEntity.setJob(jobOptional.get());
				codingTestEntity.setJobStage(codingTestOptional.get());
				codingTestEntity.setCandidate(candidateOptional.get());
				codingTestEntity.setStatus(JobCanddateStageUtil.COMPLETED);
				codingTestEntity.setCreatedBy(jobCandidateStageRequest.getCreatedBy());
				codingTestEntity.setUpdatedBy(jobCandidateStageRequest.getUpdatedBy());
				codingTestEntity.setIsActive(true);
				codingTestEntity.setIsDeleted(false);
				jobCandidateStageRepository.save(codingTestEntity);
			}
		} else if (jobCandidateStageRequest.getJobStageId() == JobCanddateStageUtil.CULTURAL_FIT_TEST_ID
				&& jobCandidateStageRequest.getStatus().equals(JobCanddateStageUtil.IN_PROGRESS)) {
			// Cultural fit test In progress
			Optional<JobStageEntity> technicalIntrvwOptional = jobStageRepository
					.findByOrder(JobCanddateStageUtil.TECHNICAL_INTERVIEW_ID);

			Optional<JobCandidateStageEntity> technicalIntrvwStageOptional = jobCandidateStageRepository
					.findByJobAndStageAndCandidate(jobCandidateStageRequest.getJobId(),
							technicalIntrvwOptional.get().getOrder(), jobCandidateStageRequest.getCandidateId());
			if (technicalIntrvwStageOptional.isPresent()) {
				JobCandidateStageEntity technicalIntrvwEntity = technicalIntrvwStageOptional.get();
				technicalIntrvwEntity.setStatus(JobCanddateStageUtil.COMPLETED);
				technicalIntrvwEntity.setUpdatedBy(jobCandidateStageRequest.getUpdatedBy());
				technicalIntrvwEntity.setIsActive(true);
				technicalIntrvwEntity.setIsDeleted(false);
				jobCandidateStageRepository.save(technicalIntrvwEntity);
			} else {
				JobCandidateStageEntity technicalIntrvwEntity = new JobCandidateStageEntity();
				technicalIntrvwEntity.setJob(jobOptional.get());
				technicalIntrvwEntity.setJobStage(technicalIntrvwOptional.get());
				technicalIntrvwEntity.setCandidate(candidateOptional.get());
				technicalIntrvwEntity.setStatus(JobCanddateStageUtil.COMPLETED);
				technicalIntrvwEntity.setCreatedBy(jobCandidateStageRequest.getCreatedBy());
				technicalIntrvwEntity.setUpdatedBy(jobCandidateStageRequest.getUpdatedBy());
				technicalIntrvwEntity.setIsActive(true);
				technicalIntrvwEntity.setIsDeleted(false);
				jobCandidateStageRepository.save(technicalIntrvwEntity);
			}
		} else if (jobCandidateStageRequest.getJobStageId() == JobCanddateStageUtil.FIRST_INTERVIEW_SCHEDULED_ID
				&& jobCandidateStageRequest.getStatus().equals(JobCanddateStageUtil.COMPLETED)) {
			// First Interview schedules
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

		if (jobCandidateStageRequest.getJobType() != null) {
			// If Jobtype is TOS, save into TOS table
			if (jobCandidateStageRequest.getJobType().equals("tos_approval")) {
				tosService.createTosEntity(jobCandidateStageRequest, candidateOptional.get(), jobOptional.get());
			}

			if (jobCandidateStageRequest.getJobType().equals("conditional_offer_prepare")) {
				conditionalOfferService.createConditionalOfferEntity(jobCandidateStageRequest, candidateOptional.get(),
						jobOptional.get());
			} else if (jobCandidateStageRequest.getJobType().equals("conditional_offer_edit")) {
				conditionalOfferService.updateConditionalOfferEntity(jobCandidateStageRequest, candidateOptional.get(),
						jobOptional.get());
			} else if (jobCandidateStageRequest.getJobType().equals("conditional_offer_rejected")) {
				conditionalOfferService.createRejectedConditionalOfferEntityEntityWithForm(jobCandidateStageRequest,
						candidateOptional.get(), jobOptional.get());
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

			// Check for null as the previous stage no dynamic form was submitted (E.g. new
			// submit to sales)
			if (jobCandidateStageOptional.isPresent() && jobCandidateStageEntity.getFormSubmissionId() != null) {
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
		} else {
			if (jobCandidateStageRequest.getFormData() != null) {
				jobCandidateStageEntity.setSubmissionData(
						MappingUtil.convertJSONStringToJsonNode(jobCandidateStageRequest.getFormData()));
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
			jobTimelineEntity.setStepName(jobCandidateStageRequest.getStepName());
			jobTimelineEntity.setSubStepName(jobCandidateStageRequest.getSubStepName());
			jobTimelineEntity.setUpdatedBy(jobCandidateStageRequest.getUpdatedBy());
		} else {
			jobTimelineEntity = new JobTimelineEntity();
			jobTimelineEntity.setJob(jobOptional.get());
			jobTimelineEntity.setCandidate(candidateOptional.get());
			jobTimelineEntity.setStepName(jobCandidateStageRequest.getStepName());
			jobTimelineEntity.setSubStepName(jobCandidateStageRequest.getSubStepName());
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

	@Transactional
	public JobCandidateStageEntity saveWithAttachments(
			JobCandidateStageWithAttachmentsRequest jobCandidateStageWithAttachmentsRequest) throws ServiceException {

		Optional<JobCandidateStageEntity> jobCandidateStageOptional = jobCandidateStageRepository
				.findByJobAndStageAndCandidate(jobCandidateStageWithAttachmentsRequest.getJobId(),
						jobCandidateStageWithAttachmentsRequest.getJobStageId(),
						jobCandidateStageWithAttachmentsRequest.getCandidateId());

		Optional<JobEntity> jobOptional = jobRepository.findById(jobCandidateStageWithAttachmentsRequest.getJobId());

		Optional<JobStageEntity> jobStageOptional = jobStageRepository
				.findByOrder(jobCandidateStageWithAttachmentsRequest.getJobStageId());

		Optional<CandidateEntity> candidateOptional = candidateRepository
				.findById(jobCandidateStageWithAttachmentsRequest.getCandidateId());

		JobCandidateStageEntity jobCandidateStageEntity = null;

		if (jobCandidateStageOptional.isPresent()) {
			jobCandidateStageEntity = jobCandidateStageOptional.get();
			jobCandidateStageEntity.setJob(jobOptional.get());
			jobCandidateStageEntity.setJobStage(jobStageOptional.get());
			jobCandidateStageEntity.setCandidate(candidateOptional.get());
			jobCandidateStageEntity.setStatus(jobCandidateStageWithAttachmentsRequest.getStatus());
			jobCandidateStageEntity.setUpdatedBy(jobCandidateStageWithAttachmentsRequest.getUpdatedBy());
			jobCandidateStageEntity.setIsActive(true);
			jobCandidateStageEntity.setIsDeleted(false);
		} else {
			jobCandidateStageEntity = new JobCandidateStageEntity();
			jobCandidateStageEntity.setJob(jobOptional.get());
			jobCandidateStageEntity.setJobStage(jobStageOptional.get());
			jobCandidateStageEntity.setCandidate(candidateOptional.get());
			jobCandidateStageEntity.setStatus(jobCandidateStageWithAttachmentsRequest.getStatus());
			jobCandidateStageEntity.setCreatedBy(jobCandidateStageWithAttachmentsRequest.getCreatedBy());
			jobCandidateStageEntity.setUpdatedBy(jobCandidateStageWithAttachmentsRequest.getUpdatedBy());
			jobCandidateStageEntity.setIsActive(true);
			jobCandidateStageEntity.setIsDeleted(false);
		}

		if (jobCandidateStageWithAttachmentsRequest.getStatus().equals(JobCanddateStageUtil.REJECTED)) {
			candidateOptional.get().setTagged(false);
			candidateRepository.save(candidateOptional.get());
		} else {
			candidateOptional.get().setTagged(true);
			candidateRepository.save(candidateOptional.get());
		}

		// Conditional Offer Release
		if (jobCandidateStageWithAttachmentsRequest.getJobType() != null) {
			if (jobCandidateStageWithAttachmentsRequest.getJobType().equals("conditional_offer_release")) {
				conditionalOfferService.createReleaseConditionalOfferEntity(jobCandidateStageWithAttachmentsRequest,
						candidateOptional.get(), jobOptional.get());
			}
		}

		jobCandidateStageEntity = jobCandidateStageRepository.save(jobCandidateStageEntity);

		if (jobCandidateStageWithAttachmentsRequest.getFormId() != null) {
			FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
			formSubmissionsRequestDTO.setUserId(jobCandidateStageWithAttachmentsRequest.getCreatedBy());
			formSubmissionsRequestDTO.setFormId(jobCandidateStageWithAttachmentsRequest.getFormId());
			formSubmissionsRequestDTO.setSubmissionData(
					MappingUtil.convertJSONStringToJsonNode(jobCandidateStageWithAttachmentsRequest.getFormData()));
			formSubmissionsRequestDTO.setEntityId(jobCandidateStageEntity.getId());
			formSubmissionsRequestDTO.setEntityType(jobCandidateStageWithAttachmentsRequest.getJobType());

			if (jobCandidateStageOptional.isPresent() && jobCandidateStageEntity.getFormSubmissionId() != null) {
				formSubmissionAPIClient.updateFormSubmission(jobCandidateStageEntity.getFormSubmissionId().intValue(),
						formSubmissionsRequestDTO);
			} else {
				HttpResponse formSubmissionResponse = formSubmissionAPIClient
						.addFormSubmission(formSubmissionsRequestDTO);
				FormSubmissionsResponseDTO formSubmissionData = MappingUtil
						.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

				jobCandidateStageEntity.setFormId(jobCandidateStageWithAttachmentsRequest.getFormId());
				jobCandidateStageEntity.setSubmissionData(formSubmissionsRequestDTO.getSubmissionData());
				jobCandidateStageEntity.setFormSubmissionId(formSubmissionData.getId());
				jobCandidateStageEntity = jobCandidateStageRepository.save(jobCandidateStageEntity);
			}
		}

		// Send email with content (Submit to sales/submit to client)
		try {
			sendEmailWithAttachment(jobCandidateStageWithAttachmentsRequest.getEmailRequest());
		} catch (Exception e) {
			log.error("Error:", e);
		}

		Optional<JobTimelineEntity> timelineoptional = jobTimelineRepository.findByJobAndCandidate(
				jobCandidateStageWithAttachmentsRequest.getJobId(),
				jobCandidateStageWithAttachmentsRequest.getCandidateId());

		JobTimelineEntity jobTimelineEntity = null;

		if (timelineoptional.isPresent()) {
			jobTimelineEntity = timelineoptional.get();
			jobTimelineEntity.setJob(jobOptional.get());
			jobTimelineEntity.setCandidate(candidateOptional.get());
			jobTimelineEntity.setStepName(jobCandidateStageWithAttachmentsRequest.getStepName());
			jobTimelineEntity.setSubStepName(jobCandidateStageWithAttachmentsRequest.getSubStepName());
			jobTimelineEntity.setUpdatedBy(jobCandidateStageWithAttachmentsRequest.getUpdatedBy());
		} else {
			jobTimelineEntity = new JobTimelineEntity();
			jobTimelineEntity.setJob(jobOptional.get());
			jobTimelineEntity.setCandidate(candidateOptional.get());
			jobTimelineEntity.setStepName(jobCandidateStageWithAttachmentsRequest.getStepName());
			jobTimelineEntity.setSubStepName(jobCandidateStageWithAttachmentsRequest.getSubStepName());
			jobTimelineEntity.setCreatedBy(jobCandidateStageWithAttachmentsRequest.getCreatedBy());
			jobTimelineEntity.setUpdatedBy(jobCandidateStageWithAttachmentsRequest.getUpdatedBy());
		}

		List<JobCandidateStageEntity> jobCandidateStageEntities = jobCandidateStageRepository.findByJobAndCandidate(
				jobCandidateStageWithAttachmentsRequest.getJobId(),
				jobCandidateStageWithAttachmentsRequest.getCandidateId());

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

			for (long i = 1; i <= jobCandidateStageWithAttachmentsRequest.getJobStageId(); i++) {
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

	@Transactional
	public JobCandidateStageEntity saveWithFiles(JobCandidateStageWithFilesRequest jobCandidateStageWithFilesRequest)
			throws ServiceException {

		Optional<JobCandidateStageEntity> jobCandidateStageOptional = jobCandidateStageRepository
				.findByJobAndStageAndCandidate(jobCandidateStageWithFilesRequest.getJobId(),
						jobCandidateStageWithFilesRequest.getJobStageId(),
						jobCandidateStageWithFilesRequest.getCandidateId());

		Optional<JobEntity> jobOptional = jobRepository.findById(jobCandidateStageWithFilesRequest.getJobId());

		Optional<JobStageEntity> jobStageOptional = jobStageRepository
				.findByOrder(jobCandidateStageWithFilesRequest.getJobStageId());

		Optional<CandidateEntity> candidateOptional = candidateRepository
				.findById(jobCandidateStageWithFilesRequest.getCandidateId());

		JobCandidateStageEntity jobCandidateStageEntity = null;

		if (jobCandidateStageOptional.isPresent()) {
			jobCandidateStageEntity = jobCandidateStageOptional.get();
			jobCandidateStageEntity.setJob(jobOptional.get());
			jobCandidateStageEntity.setJobStage(jobStageOptional.get());
			jobCandidateStageEntity.setCandidate(candidateOptional.get());
			jobCandidateStageEntity.setStatus(jobCandidateStageWithFilesRequest.getStatus());
			jobCandidateStageEntity.setUpdatedBy(jobCandidateStageWithFilesRequest.getUpdatedBy());
			jobCandidateStageEntity.setIsActive(true);
			jobCandidateStageEntity.setIsDeleted(false);
		} else {
			jobCandidateStageEntity = new JobCandidateStageEntity();
			jobCandidateStageEntity.setJob(jobOptional.get());
			jobCandidateStageEntity.setJobStage(jobStageOptional.get());
			jobCandidateStageEntity.setCandidate(candidateOptional.get());
			jobCandidateStageEntity.setStatus(jobCandidateStageWithFilesRequest.getStatus());
			jobCandidateStageEntity.setCreatedBy(jobCandidateStageWithFilesRequest.getCreatedBy());
			jobCandidateStageEntity.setUpdatedBy(jobCandidateStageWithFilesRequest.getUpdatedBy());
			jobCandidateStageEntity.setIsActive(true);
			jobCandidateStageEntity.setIsDeleted(false);
		}

		if (jobCandidateStageWithFilesRequest.getStatus().equals(JobCanddateStageUtil.REJECTED)) {
			candidateOptional.get().setTagged(false);
			candidateRepository.save(candidateOptional.get());
		} else {
			candidateOptional.get().setTagged(true);
			candidateRepository.save(candidateOptional.get());
		}

		// Conditional Offer Approval
		if (jobCandidateStageWithFilesRequest.getJobType() != null) {
			if (jobCandidateStageWithFilesRequest.getJobType().equals("conditional_offer_approval")) {
				conditionalOfferService.createConditionalOfferEntityWithFiles(jobCandidateStageWithFilesRequest,
						candidateOptional.get(), jobOptional.get());
			}
		}

		jobCandidateStageEntity = jobCandidateStageRepository.save(jobCandidateStageEntity);

		if (jobCandidateStageWithFilesRequest.getFormId() != null) {
			FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
			formSubmissionsRequestDTO.setUserId(jobCandidateStageWithFilesRequest.getCreatedBy());
			formSubmissionsRequestDTO.setFormId(jobCandidateStageWithFilesRequest.getFormId());
			formSubmissionsRequestDTO.setSubmissionData(
					MappingUtil.convertJSONStringToJsonNode(jobCandidateStageWithFilesRequest.getFormData()));
			formSubmissionsRequestDTO.setEntityId(jobCandidateStageEntity.getId());
			formSubmissionsRequestDTO.setEntityType(jobCandidateStageWithFilesRequest.getJobType());

			if (jobCandidateStageOptional.isPresent() && jobCandidateStageEntity.getFormSubmissionId() != null) {
				formSubmissionAPIClient.updateFormSubmission(jobCandidateStageEntity.getFormSubmissionId().intValue(),
						formSubmissionsRequestDTO);
			} else {
				HttpResponse formSubmissionResponse = formSubmissionAPIClient
						.addFormSubmission(formSubmissionsRequestDTO);
				FormSubmissionsResponseDTO formSubmissionData = MappingUtil
						.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);

				jobCandidateStageEntity.setFormId(jobCandidateStageWithFilesRequest.getFormId());
				jobCandidateStageEntity.setSubmissionData(formSubmissionsRequestDTO.getSubmissionData());
				jobCandidateStageEntity.setFormSubmissionId(formSubmissionData.getId());
				jobCandidateStageEntity = jobCandidateStageRepository.save(jobCandidateStageEntity);
			}
		}

		System.out.println("Job Type: " + jobCandidateStageWithFilesRequest.getJobType());

		// Prepare TOS
		if (jobCandidateStageWithFilesRequest.getJobType().equals("prepare_tos")) {
			Optional<TosEntity> tosEntityOptional = tosRepository.findByJobAndCandidate(
					jobCandidateStageWithFilesRequest.getJobId(), jobCandidateStageWithFilesRequest.getCandidateId());
			if (tosEntityOptional.isPresent()) {
				tosService.updateTosEntity(tosEntityOptional.get(), jobCandidateStageWithFilesRequest);
			} else {
				tosService.createTosEntityWithFiles(jobCandidateStageWithFilesRequest, candidateOptional.get(),
						jobOptional.get());
			}
		}

		Optional<JobTimelineEntity> timelineoptional = jobTimelineRepository.findByJobAndCandidate(
				jobCandidateStageWithFilesRequest.getJobId(), jobCandidateStageWithFilesRequest.getCandidateId());

		JobTimelineEntity jobTimelineEntity = null;

		if (timelineoptional.isPresent()) {
			jobTimelineEntity = timelineoptional.get();
			jobTimelineEntity.setJob(jobOptional.get());
			jobTimelineEntity.setCandidate(candidateOptional.get());
			jobTimelineEntity.setStepName(jobCandidateStageWithFilesRequest.getStepName());
			jobTimelineEntity.setSubStepName(jobCandidateStageWithFilesRequest.getSubStepName());
			jobTimelineEntity.setUpdatedBy(jobCandidateStageWithFilesRequest.getUpdatedBy());
		} else {
			jobTimelineEntity = new JobTimelineEntity();
			jobTimelineEntity.setJob(jobOptional.get());
			jobTimelineEntity.setCandidate(candidateOptional.get());
			jobTimelineEntity.setStepName(jobCandidateStageWithFilesRequest.getStepName());
			jobTimelineEntity.setSubStepName(jobCandidateStageWithFilesRequest.getSubStepName());
			jobTimelineEntity.setCreatedBy(jobCandidateStageWithFilesRequest.getCreatedBy());
			jobTimelineEntity.setUpdatedBy(jobCandidateStageWithFilesRequest.getUpdatedBy());
		}

		List<JobCandidateStageEntity> jobCandidateStageEntities = jobCandidateStageRepository.findByJobAndCandidate(
				jobCandidateStageWithFilesRequest.getJobId(), jobCandidateStageWithFilesRequest.getCandidateId());

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

			for (long i = 1; i <= jobCandidateStageWithFilesRequest.getJobStageId(); i++) {
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

	public JobCandidateStageEntity getStage(JobCandidateStageGetRequest jobCandidateStageGetRequest)
			throws ServiceException {
		Optional<JobCandidateStageEntity> jobCandidateStageOptional = jobCandidateStageRepository
				.findByJobAndStageAndCandidate(jobCandidateStageGetRequest.getJobId(),
						jobCandidateStageGetRequest.getJobStageId(), jobCandidateStageGetRequest.getCandidateId());
		if (jobCandidateStageOptional.isPresent()) {
			return jobCandidateStageOptional.get();
		}
		return null;
	}

	@Transactional
	public void untagCandidate(Long jobId, Long candidateId) throws ServiceException {
		// Untagged candidate
		Optional<CandidateEntity> candidateOptional = candidateRepository.findById(candidateId);
		if (candidateOptional.isPresent()) {
			CandidateEntity candidateEntity = candidateOptional.get();
			candidateEntity.setTagged(false);
			candidateRepository.save(candidateEntity);
		}

		jobCandidateStageRepository.deleteByJobAndCandidate(jobId, candidateId);
		jobTimelineRepository.deleteByJobAndCandidate(jobId, candidateId);
	}

}
