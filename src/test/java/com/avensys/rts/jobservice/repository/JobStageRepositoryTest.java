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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;

import com.avensys.rts.jobservice.entity.JobStageEntity;
@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JobStageRepositoryTest {

	//@Mock
	@Autowired
	JobStageRepository jobStageRepository;

	JobStageEntity jobStageEntity;
	JobStageEntity jobStageEntity1;
	Optional<JobStageEntity> optionalJob;
	Optional<JobStageEntity> optionalJob1;
	@Mock
	List<JobStageEntity> jobStageList;
	List<JobStageEntity> jobStageList1;
	@Mock
	AutoCloseable autoCloseable;

	@BeforeEach
	void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
		jobStageEntity = new JobStageEntity(1L, "Stage name", 1L, "Stage type");
		jobStageEntity1 = new JobStageEntity(2L, "Stage name1", 1L, "Stage type");
		jobStageRepository.save(jobStageEntity);
		jobStageRepository.save(jobStageEntity1);
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
		//mock(JobRepository.class);
		Boolean nameExits ;
		nameExits = jobStageRepository.existsByName("Stage name");
		//when(jobStageRepository.existsByName("Stage name")).thenReturn(nameExits);
		assertThat(nameExits).isEqualTo(true);
	}

	@Test
	@Order(1)
	@Rollback(value = false)
	void testFindByName() {
		//mock(JobRepository.class);
		optionalJob1 = jobStageRepository.findByName("Stage name");
		//when(jobStageRepository.findByName("Stage name")).thenReturn(optionalJob);
		assertNotNull(optionalJob1);
		//assertThat(optionalJob1.get().)
	}

	@Test
	@Order(1)
	@Rollback(value = false)
	void testFindAllAndIsDeleted() {
		//mock(JobRepository.class);
		//jobStageList1 = jobStageRepository.findAllAndIsDeleted(false);
		//when(jobStageRepository.findAllAndIsDeleted(false)).thenReturn(jobStageList);
		assertNotNull(jobStageList);
	}
}
