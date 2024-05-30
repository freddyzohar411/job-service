package com.avensys.rts.jobservice.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.avensys.rts.jobservice.entity.CandidateEntity;
import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.entity.JobTimelineEntity;
import com.fasterxml.jackson.databind.JsonNode;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class JobTimelineRepositoryTest {

	@Autowired
	// @Mock
	JobTimelineRepository jobTimelineRepository;

	JobTimelineEntity jobTimelineEntity;
	JobEntity jobEntity;
	CandidateEntity candidateEntity;
	JsonNode timeline;
	Optional<JobTimelineEntity> optionalJob;

	@Mock
	AutoCloseable autoCloseable;

	@BeforeEach
	void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
		jobTimelineEntity = new JobTimelineEntity(1L, jobEntity, candidateEntity, timeline, "123", "123", null, null);
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

		optionalJob = jobTimelineRepository.findByJobAndCandidate(1L, 1L);
		// when(jobTimelineRepository.findByJobAndCandidate(1L,
		// 1L)).thenReturn(optionalJob);
		assertNotNull(optionalJob);
	}

}
