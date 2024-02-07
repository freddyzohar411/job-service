package com.avensys.rts.jobservice.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;

import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.entity.JobRecruiterFODEntity;
import com.avensys.rts.jobservice.entity.UserEntity;
import com.avensys.rts.jobservice.entity.UserGroupEntity;
import com.avensys.rts.jobservice.payload.JobRecruiterFODRequest;
import com.avensys.rts.jobservice.repository.JobRecruiterFODRepository;
import com.avensys.rts.jobservice.repository.JobRepository;
import com.avensys.rts.jobservice.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;

@SpringBootTest
public class JobRecruiterFODServiceTest {

	@Autowired
	JobRecruiterFODService jobRecruiterFODService;

	@MockBean
	JobRecruiterFODRepository jobRecruiterFODRepository;

	@Mock
	MessageSource messageSource;

	@MockBean
	private JobRepository jobRepository;

	@MockBean
	private UserRepository userRepository;

	@Mock
	AutoCloseable autoCloseable;

	JobRecruiterFODRequest jobRecruiterFODRequest;
	JobRecruiterFODEntity jobRecruiterFODEntity;
	Optional<JobRecruiterFODEntity> jobFODOptional;
	private JobEntity job;
	UserEntity recruiter;
	UserEntity seller;
	JobEntity jobEntity;
	JsonNode jobSubmissionData;
	Optional<JobEntity> jobOptional;
	UserEntity userEntity;
	Set<UserGroupEntity> groupEntities;
	Optional<UserEntity> recruiterOptional;

	@BeforeEach
	void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
		userEntity = new UserEntity(1L, "339f35a7-0d3d-431e-9a63-d90d4c342e4a", "Kotaiah", "Nalleboina",
				"kittu1@aven-sys.com", "kittu1@aven-sys.com",
				"$2a$10$pxSQVx/EqvfrehZDdN6Q3.Qg3Agm2S/d60xYqy0rFpuNSgt1DcpvO", "+91-9381515362", "852", false, true,
				groupEntities);
		recruiterOptional = Optional.of(userEntity);
		jobEntity = new JobEntity(1L, "Java Developer", 1L, 1L, false, jobSubmissionData);
		jobOptional = Optional.of(jobEntity);
		jobRecruiterFODEntity = new JobRecruiterFODEntity(1L, job, recruiter, seller, "Status");
		jobFODOptional = Optional.of(jobRecruiterFODEntity);
		jobRecruiterFODRequest = new JobRecruiterFODRequest(1L, 1L, 1L, 1L, 1L);
	}

	@AfterEach
	void tearDown() throws Exception {
		autoCloseable.close();
	}

	@Test
	void testSave()throws Exception {
		when( jobRecruiterFODRepository
				.findByJob(jobRecruiterFODRequest.getJobId())).thenReturn(jobFODOptional);
		assertNotNull(jobOptional);
		when(jobRepository.findById(jobRecruiterFODRequest.getJobId())).thenReturn(jobOptional);
		when(userRepository.findById(jobRecruiterFODRequest.getRecruiterId())).thenReturn(recruiterOptional);
		when( userRepository.findById(jobRecruiterFODRequest.getSellerId())).thenReturn(recruiterOptional);
		jobRecruiterFODService.save(jobRecruiterFODRequest);
	}

}
