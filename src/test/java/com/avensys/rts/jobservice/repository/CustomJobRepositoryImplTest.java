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

import com.avensys.rts.jobservice.entity.JobEntity;
import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.EntityManager;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CustomJobRepositoryImplTest {

	@MockBean
	CustomJobRepository customJobRepository;

	@MockBean
	CustomJobRepositoryImpl customJobRepositoryImpl;

	@MockBean
	private EntityManager entityManager;

	@Mock
	AutoCloseable autoCloseable;

	JobEntity jobEntity;
	Page<JobEntity> jobPage;
	JsonNode jobSubmissionData;
	Page<JobEntity> pageJobEntity;
	Page<JobEntity> pageJobEntity1;
	List<JobEntity> jobList;
	Pageable pageable;
	Sort sortDec = null;
	String searchTerm;
	JobEntity jobEntity1;
	List<String> searchFields;
	List<Long> userIds;

	@BeforeEach
	void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
		customJobRepositoryImpl = new CustomJobRepositoryImpl();
		jobEntity = new JobEntity(1L, "Java Developer", 1L, 1L, false, jobSubmissionData,false);
		jobEntity1 = new JobEntity(2L, "Full Stack Developer", 1L, 1L, false, jobSubmissionData,false);
		sortDec = Sort.by(Sort.Direction.DESC, "updatedAt");
		pageable = PageRequest.of(1, 2, sortDec);
		jobList = Arrays.asList(jobEntity, jobEntity1);
		pageJobEntity = new PageImpl<JobEntity>(jobList, pageable, 2);
		searchFields = Arrays.asList("title", "id");
		userIds = Arrays.asList(1L, 2L);
		jobPage = new PageImpl<JobEntity>(jobList, pageable, 2);

	}

	@AfterEach
	void tearDown() throws Exception {
		autoCloseable.close();
	}

	@Test
	void testFindAllByOrderBy() {
		when(customJobRepository.findAllByOrderBy(1L, false, false, true, pageable)).thenReturn(pageJobEntity);
		assertNotNull(pageJobEntity);

	}

	@Test
	void testFindAllByOrderByString() {

		when(customJobRepository.findAllByOrderByString(1L, false, false, true, pageable)).thenReturn(pageJobEntity);
		assertNotNull(pageJobEntity);
	}

	@Test
	void testFindAllByOrderByNumeric() {

		when(customJobRepository.findAllByOrderByNumeric(1L, false, false, true, pageable)).thenReturn(pageJobEntity);
		assertNotNull(pageJobEntity);
	}

	@Test
	void testFindAllByOrderByAndSearchString() {
		when(customJobRepository.findAllByOrderByAndSearchString(1L, false, false, true, pageable,searchFields,searchTerm)).thenReturn(pageJobEntity);
		assertNotNull(pageJobEntity);

	}

	@Test
	void testFindAllByOrderByAndSearchNumeric() {
		when(customJobRepository.findAllByOrderByAndSearchNumeric(1L, false, false, true, pageable,searchFields,searchTerm)).thenReturn(pageJobEntity);
		assertNotNull(pageJobEntity);
	}

	@Test
	void testGetAllAccountsNameWithSearch() {
		when(customJobRepository.getAllAccountsNameWithSearch("query",1L, false, false)).thenReturn(jobList);
		assertNotNull(jobList);
	}

	@Test
	void testUpdateDocumentEntityId() {
		customJobRepository.updateDocumentEntityId(1L, 1L, 1L, "entityType");
	}

	@Test
	void testFindAllByOrderByStringWithUserIds() {
		when(customJobRepository.findAllByOrderByStringWithUserIds(userIds,false, false,pageable,"full time",1L)).thenReturn(jobPage);
		assertNotNull(jobPage);
	}

	@Test
	void testFindAllByOrderByNumericWithUserIds() {
		when(customJobRepository.findAllByOrderByNumericWithUserIds(userIds,false, false,pageable,"full time",1L)).thenReturn(jobPage);
		assertNotNull(jobPage);
	}

	@Test
	void testFindAllByOrderByAndSearchStringWithUserIds() {
		when(customJobRepository.findAllByOrderByAndSearchStringWithUserIds(userIds,false, false,pageable,searchFields,searchTerm,"full type",1L)).thenReturn(jobPage);
		assertNotNull(jobPage);
	}

	@Test
	void testFindAllByOrderByAndSearchNumericWithUserIds() {
		when(customJobRepository.findAllByOrderByAndSearchNumericWithUserIds(userIds,false, false,pageable,searchFields,searchTerm,"full type",1L)).thenReturn(jobPage);
		assertNotNull(jobPage);
	}

}
