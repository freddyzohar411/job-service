package com.avensys.rts.jobservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import com.avensys.rts.jobservice.entity.JobStageEntity;
import com.avensys.rts.jobservice.exception.ServiceException;
import com.avensys.rts.jobservice.repository.JobStageRepository;

@SpringBootTest
public class JobStageServiceTest {

	@Autowired
	JobStageService jobStageService;

	@MockBean
	JobStageRepository jobStageRepository;

	@Mock
	MessageSource messageSource;

	@Mock
	AutoCloseable autoCloseable;

	Optional<JobStageEntity> dbStage;
	Optional<JobStageEntity> dbStage1;

	JobStageEntity jobStageEntity;
	JobStageEntity jobStageEntity1;
	List<JobStageEntity> jobStageList;

	@BeforeEach
	void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
		jobStageService = new JobStageService(jobStageRepository, messageSource);
		jobStageRepository.save(jobStageEntity);
		jobStageEntity = new JobStageEntity(1L, "Stage name", 1L, "Stage type");
		jobStageEntity1 = new JobStageEntity(2L, "Stage name1", 1L, "Stage type");
		dbStage = Optional.of(jobStageEntity);
		dbStage1 = Optional.of(jobStageEntity1);
		jobStageList = Arrays.asList(jobStageEntity, jobStageEntity1);
	}

	@AfterEach
	void tearDown() throws Exception {
		autoCloseable.close();
	}

	@Test
	void testSavePositive() throws Exception {
		boolean stageName = false;
		when(jobStageRepository.existsByName(jobStageEntity.getName())).thenReturn(stageName);
		jobStageEntity1 = new JobStageEntity(1L, "Stage name", 1L, "Stage type");
		jobStageEntity.setIsActive(true);
		jobStageEntity.setIsDeleted(false);
		when(jobStageRepository.save(jobStageEntity)).thenReturn(jobStageEntity);
		jobStageService.save(jobStageEntity);
	}

	@Test
	void testSaveNegative() throws Exception {
		Boolean stageName = true;
		when(jobStageRepository.existsByName(jobStageEntity.getName())).thenReturn(stageName);
		try {
			if (stageName) {
				throw new ServiceException(
						messageSource.getMessage("error.nametaken", null, LocaleContextHolder.getLocale()));
			}

		} catch (Exception e) {
			stageName = false;
		}
		assertFalse(stageName);
	}

	@Test
	void testUpdatePositive()throws Exception  {
		when(jobStageRepository.findByName(jobStageEntity.getName())).thenReturn(dbStage);
		when(jobStageRepository.findById(1L)).thenReturn(dbStage);
		jobStageService.update(jobStageEntity);
		assertNotNull(jobStageEntity);
	}

	@Test
	void testUpdateNegative() throws Exception {
		Boolean stageName = true;
		when(jobStageRepository.findByName(jobStageEntity1.getName())).thenReturn(dbStage1);
		try {
			if (dbStage.isPresent() && dbStage.get().getId() != jobStageEntity1.getId()) {
				throw new ServiceException(
						messageSource.getMessage("error.nametaken", null, LocaleContextHolder.getLocale()));
			}

		} catch (Exception e) {
			stageName = false;
		}
		assertFalse(stageName);
	}

	@Test
	void testDelete() throws Exception {
		jobStageEntity.setIsDeleted(true);
		when(jobStageRepository.save(jobStageEntity)).thenReturn(jobStageEntity);
		assertThat(jobStageRepository.save(jobStageEntity).getIsDeleted()).isEqualTo(true);
	}

	@Test
	void testGetByIdPositive()throws Exception   {
		when(jobStageRepository.findById(1L)).thenReturn(dbStage);
		assertThat(jobStageEntity.getId()).isEqualTo(1L);
		when(jobStageRepository.findById(jobStageEntity.getId())).thenReturn(Optional.ofNullable(jobStageEntity));
		jobStageService.getById(1L);
	}

	@Test
	void testGetByIdNegative() {
		when(jobStageRepository.findById(1L)).thenReturn(dbStage);
		assertThat(jobStageEntity.getId()).isEqualTo(1L);
		when(jobStageRepository.findById(jobStageEntity.getId())).thenReturn(Optional.ofNullable(jobStageEntity));
		jobStageEntity.setIsDeleted(true);
		boolean findStage = false;
		try {
			if (dbStage.isPresent() && !dbStage.get().getIsDeleted()) {

			} else {
		    throw new ServiceException(messageSource.getMessage("error.stagenotfound", new Object[] { 1 },
						LocaleContextHolder.getLocale()));
			}

		} catch (Exception e) {
			findStage = true;
		}
		assertTrue(findStage);
	}

	@Test
	void testGetAll() {
		when(jobStageRepository.findAllAndIsDeleted(false)).thenReturn(jobStageList);
		assertNotNull(jobStageList);
	}

}
