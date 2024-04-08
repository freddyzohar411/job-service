package com.avensys.rts.jobservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.avensys.rts.jobservice.apiclient.FormSubmissionAPIClient;
import com.avensys.rts.jobservice.apiclient.UserAPIClient;
import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.exception.ServiceException;
import com.avensys.rts.jobservice.payload.FormSubmissionsRequestDTO;
import com.avensys.rts.jobservice.payload.JobRequest;
import com.avensys.rts.jobservice.repository.JobRepository;
import com.avensys.rts.jobservice.response.HttpResponse;
import com.avensys.rts.jobservice.response.JobListingDataDTO;
import com.avensys.rts.jobservice.search.job.JobSpecificationBuilder;
import com.avensys.rts.jobservice.util.UserUtil;
import com.fasterxml.jackson.databind.JsonNode;

@SpringBootTest
public class JobServiceTest {

	@Autowired
	private JobService jobService;

	@MockBean
	private MessageSource messageSource;

	@MockBean
	private JobRepository jobRepository;

	@MockBean
	private FormSubmissionAPIClient formSubmissionAPIClient;

	@MockBean
	private UserAPIClient userAPIClient;

	@MockBean
	private UserUtil userUtil;
	JobListingDataDTO jobListingDataDTO;

	JobEntity jobEntity;
	Pageable pageable = null;
	Page<JobEntity> jobPage;
	Sort sortDec = null;
	Optional<JobEntity> dbJob;

	FormSubmissionsRequestDTO formSubmissionsRequestDTO;

	@Mock
	AutoCloseable autoCloseable;
	Object data;
	@Mock
	JobSpecificationBuilder builder;
	Map<?, ?> audit;
	LocalDateTime timestamp = LocalDateTime.now();
	JobRequest jobRequest;
	JobRequest jobRequest1;
	JsonNode submissionData;
	JsonNode jobSubmissionData;
	HttpResponse formSubmissionResponse;
	LocalDateTime createdAt;
	LocalDateTime updatedAt;
	List<JobEntity> jobEntityList;
	String formData = "{\"id\":1,\"accountSubmissionData\":{\"msa\":\"yes\",\"revenue\":32432434,\"website\":\"www.tcs.com\",\"industry\":\"InformationTechnology\",\"salesName\":\"Test\",\"leadSource\":\"Test\",\"accountName\":\"TCS\",\"addressCity\":\"\",\"billingCity\":\"\",\"subIndustry\":\"SoftwareDevelopment\",\"addressLine1\":\"Bhopal\",\"addressLine2\":\"\",\"addressLine3\":\"\",\"accountRating\":\"Tier1\",\"accountSource\":\"TalentService\",\"accountStatus\":\"Active\",\"leadSalesName\":\"Test\",\"noOfEmployees\":6,\"parentCompany\":\"\",\"accountRemarks\":\"\",\"addressCountry\":\"\",\"billingAddress\":\"true\",\"landlineNumber\":324,\"secondaryOwner\":\"Test\",\"landlineCountry\":\"\",\"leadAccountName\":\"TCS\",\"revenueCurrency\":\"INR₹\",\"uploadAgreement\":\"Reema_Sahu_Java_5Yrs.docx(1).pdf\",\"addressPostalCode\":\"\",\"billingAddressLine1\":\"Bhopal\",\"billingAddressLine2\":\"\",\"billingAddressLine3\":\"\",\"billingAddressCountry\":\"\",\"billingAddressPostalCode\":\"\"},\"commercialSubmissionData\":{\"msp\":\"Test\",\"markUp\":\"Test\"},\"accountNumber\":\"A0958950\",\"createdAt\":\"2024-01-16T13:02:13.006307\",\"updatedAt\":\"2024-01-16T13:06:15.374175\",\"accountCountry\":\"India\",\"createdByName\":\"Super1Admin1\",\"updatedByName\":\"Super1Admin1\"}";

	@BeforeEach
	void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
		jobListingDataDTO = new JobListingDataDTO(1L, "java developer", jobSubmissionData, createdAt, updatedAt,
				"kittu", "kittu");
		jobEntity = new JobEntity(1L, "Java Developer", 1L, 1L, false, submissionData);
		dbJob = Optional.of(jobEntity);
		jobRepository.save(jobEntity);
		formSubmissionResponse = new HttpResponse(201, false, "job saved successfully", data, audit, timestamp);
		formSubmissionsRequestDTO = new FormSubmissionsRequestDTO(1L, 1L, submissionData, 1L, "entityType");
		jobRequest = new JobRequest(1L, "Java Developer", 1L, 1L, formData, false, 1L, 1L,false);
		jobRequest1 = new JobRequest(2L, "Java Developer", 1L, 1L, formData, false, 1L, 1L,false);
		sortDec = Sort.by(Sort.Direction.DESC, "updatedAt");
		pageable = PageRequest.of(1, 2, sortDec);
		jobEntityList = Arrays.asList(jobEntity);
		jobPage = new PageImpl<JobEntity>(jobEntityList, pageable, 2);
	}

	@AfterEach
	void tearDown() throws Exception {
		autoCloseable.close();
	}

	@Test
	public void testMapRequestToEntity() throws Exception {
		JobService jobService = new JobService(jobRepository, formSubmissionAPIClient, userAPIClient);
		Method mapRequestToEntity = JobService.class.getDeclaredMethod("mapRequestToEntity", JobRequest.class);
		mapRequestToEntity.setAccessible(true);
		JobEntity jobEntityResult = (JobEntity) mapRequestToEntity.invoke(jobService, jobRequest);
		assertThat(jobEntityResult.getIsDeleted()).isEqualTo(jobEntity.getIsDeleted());
	}

	@Test
	void testSavePositive() throws Exception {
		JobService jobService = new JobService(jobRepository, formSubmissionAPIClient, userAPIClient);
		Method mapRequestToEntity = JobService.class.getDeclaredMethod("mapRequestToEntity", JobRequest.class);
		mapRequestToEntity.setAccessible(true);
		JobEntity jobEntityResult = (JobEntity) mapRequestToEntity.invoke(jobService, jobRequest);
		when(formSubmissionAPIClient.addFormSubmission(formSubmissionsRequestDTO)).thenReturn(formSubmissionResponse);
	}

	@Test
	void testSaveNegative() throws Exception {
		Boolean titleExists = true;
		when(jobRepository.existsByTitle(jobRequest.getTitle())).thenReturn(titleExists);
		// jobService.save(jobRequest);
		try {
			if (titleExists) {
				throw new ServiceException(
						messageSource.getMessage("error.jobtitletaken", null, LocaleContextHolder.getLocale()));
			}

		} catch (Exception e) {
			titleExists = false;
		}
		assertFalse(titleExists);
	}

	@Test
	void testUpdatePositive() throws Exception {

		when(jobRepository.findById(jobRequest.getId())).thenReturn(dbJob);
		jobService.update(jobRequest);
	}

	@Test
	void testUpdateNegative()throws Exception {
		when(jobRepository.findByTitle(jobRequest1.getTitle())).thenReturn(dbJob);
		Boolean titleExists = true;
		when(jobRepository.existsByTitle(jobRequest1.getTitle()))
				.thenReturn(titleExists);
		try {
			if (dbJob.isPresent() && dbJob.get().getId() != jobRequest1.getId()) {
				throw new ServiceException(
						messageSource.getMessage("error.jobtitletaken", null, LocaleContextHolder.getLocale()));
			}

		} catch (Exception e) {
			titleExists = false;
		}
		assertFalse(titleExists);
	}

	@Test
	void testGetById() throws Exception {
		jobRepository.save(jobEntity);
		assertThat(jobRequest.getId()).isEqualTo(1L);
		when(jobRepository.findById(jobRequest.getId())).thenReturn(dbJob);
		assertTrue(dbJob.isPresent());
		assertFalse(dbJob.get().getIsDeleted());
		jobService.getById(1L);
		assertThat(jobService.getById(1L)).isEqualTo(jobEntity);
		assertThat(dbJob.get().getIsDeleted()).isEqualTo(false);
	}

	@Test
	void testDelete() throws Exception {
		mock(JobEntity.class);
		mock(JobRequest.class);
		when(jobRepository.findById(jobEntity.getId())).thenReturn(dbJob);
		jobService.delete(jobRequest.getId());
		assertThat(jobEntity.getIsDeleted()).isEqualTo(true);
	}

	@Test
	void testGetAllJobs() throws Exception {
		when(jobRepository.findAllAndIsDeleted(false, pageable)).thenReturn(jobEntityList);
		jobService.getAllJobs(Integer.valueOf(1),Integer.valueOf(2),"title");
	}

	@Test
	void testSearch() throws Exception {

		when(jobRepository.findAll(builder.build(), pageable)).thenReturn(jobPage);
		assertThat(jobService.search("name", pageable)).isEqualTo(jobPage);

	}

	@Test
	void testGetJobDraft() throws Exception {

		when(jobRepository.findByUserAndDraftAndDeleted(1L, true, false, true)).thenReturn(dbJob);
		jobService.getJobDraft(1L);
	}

	/*
	 * @Test void testGetJobByIdData() throws Exception {
	 * when(jobRepository.findByIdAndDeleted(1L, false, true)).thenReturn(dbJob);
	 * jobService.getJobByIdData(1); }
	 */

}
