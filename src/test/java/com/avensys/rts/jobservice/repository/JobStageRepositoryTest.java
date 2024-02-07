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
import org.springframework.test.annotation.Rollback;

import com.avensys.rts.jobservice.entity.JobStageEntity;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JobStageRepositoryTest {

	@Mock
	JobStageRepository jobStageRepository;

	JobStageEntity jobStageEntity;
	Optional<JobStageEntity> optionalJob;
	@Mock
	List<JobStageEntity> jobStageList;
	@MockBean
	AutoCloseable autoCloseable;

	@BeforeEach
	void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
		jobStageEntity = new JobStageEntity(1L, "Stage name", 1L, "Stage type");
		jobStageRepository.save(jobStageEntity);
		optionalJob = Optional.of(jobStageEntity);
		jobStageList.add(jobStageEntity);
	}

	@AfterEach
	void tearDown() throws Exception {
		jobStageEntity = null;
		jobStageRepository.deleteAll();
	}

	@Test
	@Order(1)
	@Rollback(value = false)
	void testExistsByTitle() {
		mock(JobRepository.class);
		Boolean nameExits = true;
		when(jobStageRepository.existsByName("Stage name")).thenReturn(nameExits);
		assertThat(nameExits).isEqualTo(true);
	}

	@Test
	@Order(1)
	@Rollback(value = false)
	void testFindByName() {
		mock(JobRepository.class);
		when(jobStageRepository.findByName("Stage name")).thenReturn(optionalJob);
		assertNotNull(optionalJob);
	}

	@Test
	@Order(1)
	@Rollback(value = false)
	void testFindAllAndIsDeleted() {
		mock(JobRepository.class);
		when(jobStageRepository.findAllAndIsDeleted(false)).thenReturn(jobStageList);
		assertNotNull(jobStageList);
	}
}
