package com.avensys.rts.jobservice.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.avensys.rts.jobservice.apiclient.UserAPIClient;
import com.avensys.rts.jobservice.entity.JobTimelineEntity;
import com.avensys.rts.jobservice.payload.JobListingRequestDTO;
import com.avensys.rts.jobservice.repository.JobTimelineRepository;
import com.avensys.rts.jobservice.response.JobTimelineResponseDTO;
import com.avensys.rts.jobservice.util.UserUtil;

@SpringBootTest
public class JobTimelineServiceTest {

	@Autowired
	JobTimelineService JobTimelineService;

	@MockBean
	JobTimelineRepository jobTimelineRepository;

	@MockBean
	UserUtil userUtil;

	@MockBean
	UserAPIClient userAPIClient;

	@Mock
	MessageSource messageSource;

	@Mock
	AutoCloseable autoCloseable;

	Pageable pageable = null;
	Sort sortDec = null;
	JobTimelineEntity jobTimelineEntity;
	Page<JobTimelineEntity> jobEntitiesPage;
	JobListingRequestDTO jobListingRequestDTO;
	JobTimelineResponseDTO jobTimelineResponseDTO;

	@BeforeEach
	void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
		jobTimelineEntity = new JobTimelineEntity();
		// jobListingRequestDTO = new JobListingRequestDTO(1, 10, "updatedAt",
		// "DEFAULT_DIRECTION", "name", searchFields,
		// "part-time");
		// jobTimelineResponseDTO = new JobTimelineResponseDTO(1, 1L, 1, 5, jobs);
		sortDec = Sort.by(Sort.Direction.DESC, "updatedAt");
		pageable = PageRequest.of(1, 2, sortDec);

	}

	@AfterEach
	void tearDown() throws Exception {
		autoCloseable.close();
	}

	@Test
	void testGetJobTimelineCount() {

	}

	@Test
	void testPageJobListingToJobListingResponseDTO() {

	}

	@Test
	void testGetJobTimelineListingPageWithSearch() {

	}

	@Test
	void testGetJobTimelineListingPage() {
		// when(jobTimelineRepository.findAllByOrderByNumericWithUserIds(userUtil.getUsersIdUnderManager(),
		// false, true, pageRequest, userId)).thenReturn(userGroupPage);
		// when(JobTimelineService.getJobTimelineListingPage(1, 2, "updatedAt",
		// "DEFAULT_DIRECTION",1L)).thenReturn(userGroupPage);
		// assertNotNull(userGroupPage);
	}

}
