package com.avensys.rts.jobservice.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.avensys.rts.jobservice.entity.JobEntity;
import com.fasterxml.jackson.databind.JsonNode;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JobRepositoryTest {

	@Mock
	private JobRepository jobRepository;

	JobEntity jobEntity;
	JobEntity jobEntity1;
	JsonNode jobSubmissionData;
	Optional<JobEntity> optionalJob;
	Pageable pageable;
	@Mock
	List<JobEntity> jobList;
	@Mock
	List<Long> createdByList;

	@MockBean
	AutoCloseable autoCloseable;

	@BeforeEach
	void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);

		jobEntity = new JobEntity(1L, "Java Developer", 1L, 1L, false, jobSubmissionData);
		jobEntity1 = new JobEntity(2L, "Full Stack Developer", 1L, 1L, false, jobSubmissionData);
		jobList.add(jobEntity);
		jobList.add(jobEntity1);
		createdByList.add(1L);
		createdByList.add(2L);
		jobRepository.save(jobEntity);
		optionalJob = Optional.of(jobEntity);

	}

	@AfterEach
	void tearDown() throws Exception {
		jobEntity = null;
		jobRepository.deleteAll();
	}

	@Test
	@Order(1)
	@Rollback(value = false)
	void testExistsByTitle() {
		mock(JobRepository.class);
		Boolean nameExits = true;
		when(jobRepository.existsByTitle("Java Developer")).thenReturn(nameExits);
		assertThat(nameExits).isEqualTo(true);
	}

	@Test
	@Order(2)
	@Rollback(value = false)
	void testFindByTitle() {
		mock(JobRepository.class);
		when(jobRepository.findByTitle("Java Developer")).thenReturn(optionalJob);
		assertNotNull(optionalJob);
	}

	@Test
	@Order(3)
	@Rollback(value = false)
	void testFindByIdAndIsDeleted() {
		mock(JobRepository.class);
		when(jobRepository.findByIdAndIsDeleted(1L, true)).thenReturn(optionalJob);
		assertNotNull(optionalJob);
	}

	@Test
	@Order(4)
	@Rollback(value = false)
	void testFindAllAndIsDeleted() {
		mock(JobRepository.class);
		when(jobRepository.findAllAndIsDeleted(false, pageable)).thenReturn(jobList);
		assertNotNull(jobList);
	}

	@Test
	@Order(5)
	@Rollback(value = false)
	void testFindAllByUserAndDeleted() {
		mock(JobRepository.class);
		when(jobRepository.findAllByUserAndDeleted(1L, false, true)).thenReturn(jobList);
		assertNotNull(jobList);
	}

	@Test
	@Order(6)
	@Rollback(value = false)
	void testFindAllByUserIdsAndDeleted() {
		mock(JobRepository.class);
		when(jobRepository.findAllByUserIdsAndDeleted(createdByList, false, true)).thenReturn(jobList);
		assertNotNull(jobList);
	}

	@Test
	@Order(7)
	@Rollback(value = false)
	void testFindByUserAndDraftAndDeleted() {
		mock(JobRepository.class);
		when(jobRepository.findByUserAndDraftAndDeleted(1L, false, false, true)).thenReturn(optionalJob);
		assertNotNull(optionalJob);
	}

	@Test
	@Order(8)
	@Rollback(value = false)
	void testFindByIdAndDeleted() {
		mock(JobRepository.class);
		when(jobRepository.findByIdAndDeleted(1L, false, true)).thenReturn(optionalJob);
		assertNotNull(optionalJob);
	}

}
