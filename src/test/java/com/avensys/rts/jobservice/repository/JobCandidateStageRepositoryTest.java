package com.avensys.rts.jobservice.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;

import com.avensys.rts.jobservice.entity.CandidateEntity;
import com.avensys.rts.jobservice.entity.JobCandidateStageEntity;
import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.entity.JobStageEntity;
import com.fasterxml.jackson.databind.JsonNode;

public class JobCandidateStageRepositoryTest {

	@Mock
	JobCandidateStageRepository jobCandidateStageRepository;

	JobCandidateStageEntity jobCandidateStageEntity;
	JobCandidateStageEntity jobCandidateStageEntity1;
	JsonNode submissionData;
	CandidateEntity candidateEntity;
	JobStageEntity jobStageEntity;
	JobEntity jobEntity;
	List<JobCandidateStageEntity> jobList;
	Pageable pageable;
	Optional<JobCandidateStageEntity> optionalJob;
	@MockBean
	AutoCloseable autoCloseable;

	@BeforeEach
	void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
		jobCandidateStageEntity = new JobCandidateStageEntity(1L, jobEntity, jobStageEntity, "Active", candidateEntity,
				1L, 1L, submissionData, null, submissionData);
		jobCandidateStageEntity1 = new JobCandidateStageEntity(1L, jobEntity, jobStageEntity, "Active", candidateEntity,
				1L, 1L, submissionData, null, submissionData);
		jobList = Arrays.asList(jobCandidateStageEntity, jobCandidateStageEntity1);
		optionalJob = Optional.of(jobCandidateStageEntity);
		optionalJob = Optional.of(jobCandidateStageEntity1);
	}

	@AfterEach
	void tearDown() throws Exception {
		jobCandidateStageEntity = null;
		jobCandidateStageRepository.deleteAll();
		autoCloseable.close();
	}

	@Test
	void testFindByJob() {

		jobCandidateStageRepository.save(jobCandidateStageEntity);
		jobCandidateStageRepository.save(jobCandidateStageEntity1);
		when(jobCandidateStageRepository.findByJob(1L)).thenReturn(jobList);
		assertNotNull(jobList);
	}

	@Test
	void testFindByCandidate() {

		jobCandidateStageRepository.save(jobCandidateStageEntity);
		jobCandidateStageRepository.save(jobCandidateStageEntity1);
		when(jobCandidateStageRepository.findByCandidate(1L)).thenReturn(jobList);
		assertNotNull(jobList);
	}

	@Test
	void testFindByJobAndStageAndCandidate() {
		jobCandidateStageRepository.save(jobCandidateStageEntity);
		jobCandidateStageRepository.save(jobCandidateStageEntity1);
		when(jobCandidateStageRepository.findByJobAndStageAndCandidate(1L, 1L, 1L)).thenReturn(optionalJob);
		assertNotNull(optionalJob);
	}

	@Test
	void testFindByJobAndCandidate() {
		jobCandidateStageRepository.save(jobCandidateStageEntity);
		jobCandidateStageRepository.save(jobCandidateStageEntity1);
		when(jobCandidateStageRepository.findByJobAndCandidate(1L, 1L)).thenReturn(jobList);
		assertNotNull(jobList);
	}

	@Test
	void testFindAllAndIsDeleted() {
		jobCandidateStageRepository.save(jobCandidateStageEntity);
		jobCandidateStageRepository.save(jobCandidateStageEntity1);
		when(jobCandidateStageRepository.findAllAndIsDeleted(false)).thenReturn(jobList);
		assertNotNull(jobList);
	}

	@Test
	void testFindAllAndIsDeleted1() {
		jobCandidateStageRepository.save(jobCandidateStageEntity);
		jobCandidateStageRepository.save(jobCandidateStageEntity1);
		when(jobCandidateStageRepository.findAllAndIsDeleted(false, pageable)).thenReturn(jobList);
		assertNotNull(jobList);
	}

}
