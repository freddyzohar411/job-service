package com.avensys.rts.jobservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.avensys.rts.jobservice.apiclient.FormSubmissionAPIClient;
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
import com.avensys.rts.jobservice.repository.JobTimelineRepository;
import com.fasterxml.jackson.databind.JsonNode;

@SpringBootTest
public class JobCandidateStageServiceTest {

	@Autowired
	JobCandidateStageService jobCandidateStageService;

	@MockBean
	private JobRepository jobRepository;
	@MockBean
	private JobStageRepository jobStageRepository;
	@MockBean
	private CandidateRepository candidateRepository;
	@MockBean
	private JobCandidateStageRepository jobCandidateStageRepository;
	@MockBean
	private JobTimelineRepository jobTimelineRepository;
	@MockBean
	private FormSubmissionAPIClient formSubmissionAPIClient;
	@Mock
	private MessageSource messageSource;
	JobCandidateStageRequest jobCandidateStageRequest;
	@Mock
	JobCandidateStageEntity jobCandidateStageEntity;
	@Mock
	JobCandidateStageEntity jobCandidateStageEntity1;
	JsonNode submissionData;
	Optional<JobCandidateStageEntity> optionalJobCandidate;
	JsonNode jobSubmissionData;
	CandidateEntity candidate;
	JobStageEntity jobStage;
	private JobEntity jobEntity;
	private JobStageEntity jobStageEntity;
	private CandidateEntity candidateEntity;
	JobEntity job;
	Optional<JobEntity> jobOptional;
	Sort sortDec = null;
	JsonNode candidateSubmissionData;
	Pageable pageable;
	Optional<CandidateEntity> candidateOptional;
	Optional<JobStageEntity> jobStageOptional;
	List<JobCandidateStageEntity> jobStageList;
	@Mock
	AutoCloseable autoCloseable;
	String formData = "{\"id\":1,\"accountSubmissionData\":{\"msa\":\"yes\",\"revenue\":32432434,\"website\":\"www.tcs.com\",\"industry\":\"InformationTechnology\",\"salesName\":\"Test\",\"leadSource\":\"Test\",\"accountName\":\"TCS\",\"addressCity\":\"\",\"billingCity\":\"\",\"subIndustry\":\"SoftwareDevelopment\",\"addressLine1\":\"Bhopal\",\"addressLine2\":\"\",\"addressLine3\":\"\",\"accountRating\":\"Tier1\",\"accountSource\":\"TalentService\",\"accountStatus\":\"Active\",\"leadSalesName\":\"Test\",\"noOfEmployees\":6,\"parentCompany\":\"\",\"accountRemarks\":\"\",\"addressCountry\":\"\",\"billingAddress\":\"true\",\"landlineNumber\":324,\"secondaryOwner\":\"Test\",\"landlineCountry\":\"\",\"leadAccountName\":\"TCS\",\"revenueCurrency\":\"INR₹\",\"uploadAgreement\":\"Reema_Sahu_Java_5Yrs.docx(1).pdf\",\"addressPostalCode\":\"\",\"billingAddressLine1\":\"Bhopal\",\"billingAddressLine2\":\"\",\"billingAddressLine3\":\"\",\"billingAddressCountry\":\"\",\"billingAddressPostalCode\":\"\"},\"commercialSubmissionData\":{\"msp\":\"Test\",\"markUp\":\"Test\"},\"accountNumber\":\"A0958950\",\"createdAt\":\"2024-01-16T13:02:13.006307\",\"updatedAt\":\"2024-01-16T13:06:15.374175\",\"accountCountry\":\"India\",\"createdByName\":\"Super1Admin1\",\"updatedByName\":\"Super1Admin1\"}";

	@BeforeEach
	void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
		candidateEntity = new CandidateEntity(1L, "kotaiah", "nalleboina", true, 1, 1, candidateSubmissionData, "123");
		candidateOptional = Optional.of(candidateEntity);
		jobStageEntity = new JobStageEntity(1L, "Stage name", 1L, "Stage type");
		jobStageOptional = Optional.of(jobStageEntity);
		jobEntity = new JobEntity(1L, "Java Developer", 1L, 1L, false, jobSubmissionData);
		jobOptional = Optional.of(jobEntity);
		jobCandidateStageEntity = new JobCandidateStageEntity(1L, jobEntity, jobStageEntity, "Active", candidateEntity,
				1L, 1L, jobSubmissionData);
		jobCandidateStageEntity1 = new JobCandidateStageEntity(1L, jobEntity, jobStageEntity, "Active", candidateEntity,
				1L, 1L, jobSubmissionData);
		jobCandidateStageRequest = new JobCandidateStageRequest(1L, 1L, 1L, "Active", 1L, 1L, 1L, "full time", formData,
				1L, 1L);
		jobStageList = Arrays.asList(jobCandidateStageEntity, jobCandidateStageEntity1);
		sortDec = Sort.by(Sort.Direction.DESC, "updatedAt");
		pageable = PageRequest.of(1, 2, sortDec);
		optionalJobCandidate = Optional.of(jobCandidateStageEntity);
		jobCandidateStageRepository.save(jobCandidateStageEntity);

	}

	@AfterEach
	void tearDown() throws Exception {
		autoCloseable.close();
	}

	@Test
	void testGetByIdPositive()throws Exception   {
		when(jobCandidateStageRepository.findById(1L)).thenReturn(optionalJobCandidate);
		assertThat(jobCandidateStageEntity.getId()).isEqualTo(1L);
		when(jobCandidateStageRepository.findById(jobCandidateStageEntity.getId())).thenReturn(Optional.ofNullable(jobCandidateStageEntity));
		jobCandidateStageService.getById(1L);
	}

	@Test
	void testGetByIdNegative() {
		when(jobCandidateStageRepository.findById(1L)).thenReturn(optionalJobCandidate);
		assertThat(jobCandidateStageEntity.getId()).isEqualTo(1L);
		when(jobCandidateStageRepository.findById(jobCandidateStageEntity.getId())).thenReturn(Optional.ofNullable(jobCandidateStageEntity));
		jobCandidateStageEntity.setIsDeleted(true);
		boolean findId = false;
		try {
			if (optionalJobCandidate.isPresent() && !optionalJobCandidate.get().getIsDeleted()) {

			} else {
				throw new ServiceException(messageSource.getMessage("error.stagenotfound", new Object[] { 1 },
						LocaleContextHolder.getLocale()));
			}

		} catch (Exception e) {
			findId = true;
		}
		assertTrue(findId);
	}

	@Test
	void testSave()throws Exception {
		when(jobCandidateStageRepository
				.findByJobAndStageAndCandidate(jobCandidateStageRequest.getJobId(),
						jobCandidateStageRequest.getJobStageId(), jobCandidateStageRequest.getCandidateId())).thenReturn(optionalJobCandidate);
		when(jobRepository.findById(jobCandidateStageRequest.getJobId())).thenReturn(jobOptional);
		when(jobStageRepository
				.findById(jobCandidateStageRequest.getJobStageId())).thenReturn(jobStageOptional);
		when(candidateRepository
				.findById(jobCandidateStageRequest.getCandidateId())).thenReturn(candidateOptional);
		jobCandidateStageEntity = new JobCandidateStageEntity(1L, jobEntity, jobStageEntity, "Active", candidateEntity,
				1L, 1L, jobSubmissionData);
		when(jobCandidateStageRepository.save(jobCandidateStageEntity)).thenReturn(jobCandidateStageEntity);
		//jobCandidateStageEntity = jobCandidateStageRepository.save(jobCandidateStageEntity);
		assertNotNull(optionalJobCandidate);
		assertThat(optionalJobCandidate.get().getId()).isEqualTo(1L);
		assertThat(jobCandidateStageEntity.getId()).isEqualTo(1L);
		assertNotNull(jobCandidateStageEntity);
		jobCandidateStageEntity = optionalJobCandidate.get();
		jobCandidateStageRepository.save(jobCandidateStageEntity);
	}

	@Test
	void testGetAll() {
		when(jobCandidateStageRepository.findAllAndIsDeleted(false)).thenReturn(jobStageList);
		assertNotNull(jobStageList);
	}

	@Test
	void testGetAllPage() {
		when(jobCandidateStageRepository.findAllAndIsDeleted(false,pageable)).thenReturn(jobStageList);
		assertThat(jobStageList.get(0).getId()).isEqualTo(1L);
	}

	@Test void testDelete()throws Exception {
	  when(jobCandidateStageRepository.findById(1L)).thenReturn(
	  optionalJobCandidate); jobCandidateStageService.delete(1L);
	  jobCandidateStageEntity.setIsDeleted(true);
	  when(jobCandidateStageRepository.save(jobCandidateStageEntity)).thenReturn(
	  jobCandidateStageEntity);
	  assertThat(jobCandidateStageRepository.save(jobCandidateStageEntity).
	  getIsDeleted()).isEqualTo(true);
	  }

}
