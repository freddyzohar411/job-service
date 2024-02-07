package com.avensys.rts.jobservice.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.avensys.rts.jobservice.entity.CandidateEntity;
import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.entity.JobTimelineEntity;
import com.fasterxml.jackson.databind.JsonNode;

public class JobTimelineRepositoryTest {

	@Mock
	JobTimelineRepository jobTimelineRepository;

	JobTimelineEntity jobTimelineEntity;
	JobEntity jobEntity;
	CandidateEntity candidateEntity;
	JsonNode timeline;
	Optional<JobTimelineEntity> optionalJob;

	@MockBean
	AutoCloseable autoCloseable;

	@BeforeEach
	void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
		jobTimelineEntity = new JobTimelineEntity(1L, jobEntity, candidateEntity, timeline, "123", "123");
		optionalJob = Optional.of(jobTimelineEntity);
	}

	@AfterEach
	void tearDown() throws Exception {
		jobTimelineEntity = null;
		jobTimelineRepository.deleteAll();
		autoCloseable.close();
	}

	@Test
	void testFindByJobAndCandidate() {

		when(jobTimelineRepository.findByJobAndCandidate(1L, 1L)).thenReturn(optionalJob);
		assertNotNull(optionalJob);
	}

}
