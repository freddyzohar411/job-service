package com.avensys.rts.jobservice.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.avensys.rts.jobservice.entity.CandidateEntity;
import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.entity.JobTimelineEntity;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.EntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CustomJobTimelineRepositoryImplTest {

	@MockBean
	private EntityManager entityManager;

	@MockBean
	CustomJobTimelineRepository customJobTimelineRepository;

	@MockBean
	CustomJobTimelineRepositoryImplTest customJobTimelineRepositoryImplTest;

	@Mock
	AutoCloseable autoCloseable;

	JobTimelineEntity jobTimelineEntity;
	JobTimelineEntity jobTimelineEntity1;
	JobEntity jobEntity;
	JsonNode timeline;
	CandidateEntity candidateEntity;
	List<JobTimelineEntity> jobTimelineList;
	Pageable pageable;
	Sort sortDec = null;
	JsonNode jobSubmissionData;
	JsonNode candidateSubmissionData;
	List<String> searchFields;
	String searchTerm;
	List<Long> userIds;
	Page<JobTimelineEntity> pageJobTimeline;

	@BeforeEach
	void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
		jobEntity = new JobEntity(1L, "Java Developer", 1L, 1L, false, jobSubmissionData,false);
		candidateEntity = new CandidateEntity(1L, "kotaiah", "nalleboina", true, 1, 1, candidateSubmissionData, "123",
				false);
		jobTimelineEntity = new JobTimelineEntity(1L, jobEntity, candidateEntity, timeline, "stepName", "subStepName",0L,0L,"123","123");
		jobTimelineEntity1 = new JobTimelineEntity(1L, jobEntity, candidateEntity, timeline, "stepName", "subStepName",0L,0L,"123","123");
		jobTimelineList = Arrays.asList(jobTimelineEntity, jobTimelineEntity1);
		sortDec = Sort.by(Sort.Direction.DESC, "updatedAt");
		userIds = Arrays.asList(1L, 2L);
		pageable = PageRequest.of(1, 2, sortDec);

		pageJobTimeline = new PageImpl<JobTimelineEntity>(jobTimelineList, pageable, 2);
	}

	@AfterEach
	void tearDown() throws Exception {
		autoCloseable.close();
	}

	@Test
	void testFindAllByOrderByStringWithUserIds() {
		when(customJobTimelineRepository.findAllByOrderByStringWithUserIds(userIds,false, false,pageable,1L,1L,1)).thenReturn(pageJobTimeline);
		assertNotNull(pageJobTimeline);
	}

	@Test
	void testFindAllByOrderByNumericWithUserIds() {
		when(customJobTimelineRepository.findAllByOrderByNumericWithUserIds(userIds,false, false,pageable,1L,1L,1)).thenReturn(pageJobTimeline);
		assertNotNull(pageJobTimeline);
	}

	@Test
	void testFindAllByOrderByAndSearchStringWithUserIds() {
		when(customJobTimelineRepository.findAllByOrderByAndSearchStringWithUserIds(userIds,false, false,pageable,searchFields,searchTerm,1L,1L,1)).thenReturn(pageJobTimeline);
		assertNotNull(pageJobTimeline);
	}

	@Test
	void testFindAllByOrderByAndSearchNumericWithUserIds() {
		when(customJobTimelineRepository.findAllByOrderByAndSearchNumericWithUserIds(userIds,false, false,pageable,searchFields,searchTerm,1L,1L,1)).thenReturn(pageJobTimeline);
		assertNotNull(pageJobTimeline);
	}
}
