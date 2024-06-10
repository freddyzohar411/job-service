package com.avensys.rts.jobservice.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.entity.JobRecruiterFODEntity;
import com.avensys.rts.jobservice.entity.UserEntity;
import com.avensys.rts.jobservice.entity.UserGroupEntity;
import com.fasterxml.jackson.databind.JsonNode;

public class JobRecruiterFODRepositoryTest {

	@Mock
	JobRecruiterFODRepository jobRecruiterFODRepository;
	@MockBean
	JobRecruiterFODEntity jobRecruiterFODEntity;

	private JobEntity jobEntity;
	UserEntity userEntityRecruiter;
	UserEntity userEntitySeller;
	JsonNode jobSubmissionData;
	Set<UserGroupEntity> groupEntities;
	Optional<JobRecruiterFODEntity> optionalJob;
	@MockBean
	AutoCloseable autoCloseable;

	@BeforeEach
	void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
		userEntityRecruiter = new UserEntity(1L, "339f35a7-0d3d-431e-9a63-d90d4c342e4a", "Kotaiah", "Nalleboina",
				"kittu1@aven-sys.com", "kittu1@aven-sys.com",
				"$2a$10$pxSQVx/EqvfrehZDdN6Q3.Qg3Agm2S/d60xYqy0rFpuNSgt1DcpvO", "+91-9381515362", "852", false, true,
				groupEntities);
		userEntitySeller = new UserEntity(2L, "339f35a7-0d3d-431e-9a63-d90d4c342e4a", "Kotaiah", "Nalleboina",
				"kittu1@aven-sys.com", "kittu1@aven-sys.com",
				"$2a$10$pxSQVx/EqvfrehZDdN6Q3.Qg3Agm2S/d60xYqy0rFpuNSgt1DcpvO", "+91-9381515362", "852", false, true,
				groupEntities);
		jobEntity = new JobEntity(1L, "Java Developer", 1L, 1L, false, jobSubmissionData,false);
		jobRecruiterFODEntity = new JobRecruiterFODEntity(1L, jobEntity, userEntityRecruiter, userEntitySeller,
				"Active");
		optionalJob = Optional.of(jobRecruiterFODEntity);
	}

	@AfterEach
	void tearDown() throws Exception {
		jobRecruiterFODEntity = null;
		jobRecruiterFODRepository.deleteAll();
	}

	@Test
	void testFindByJob() throws Exception {
		jobRecruiterFODRepository.save(jobRecruiterFODEntity);
		when(jobRecruiterFODRepository.findByJob(1L)).thenReturn(optionalJob);
		assertNotNull(optionalJob);
	}

	@Test
	void testFindByRecruiter() throws Exception {
		jobRecruiterFODRepository.save(jobRecruiterFODEntity);
		when(jobRecruiterFODRepository.findByRecruiter(1L)).thenReturn(optionalJob);
		assertNotNull(optionalJob);
	}

	@Test
	void testFindBySeller() throws Exception {
		jobRecruiterFODRepository.save(jobRecruiterFODEntity);
		when(jobRecruiterFODRepository.findBySeller(2L)).thenReturn(optionalJob);
		assertNotNull(optionalJob);
	}

	@Test
	void testFindByJobAndRecruiter() throws Exception {
		jobRecruiterFODRepository.save(jobRecruiterFODEntity);
		when(jobRecruiterFODRepository.findByJobAndRecruiter(1L, 1L)).thenReturn(optionalJob);
		assertNotNull(optionalJob);
	}

}
