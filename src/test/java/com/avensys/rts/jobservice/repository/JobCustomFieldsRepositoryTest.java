package com.avensys.rts.jobservice.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.avensys.rts.jobservice.entity.CustomFieldsEntity;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JobCustomFieldsRepositoryTest {

	@Autowired
	JobCustomFieldsRepository jobCustomFieldsRepository;

	CustomFieldsEntity customFieldsEntity;
	LocalDateTime createdAt;
	LocalDateTime updatedAt;

	@BeforeEach
	void setUp() {
		customFieldsEntity = new CustomFieldsEntity(1L, "JobCustomView", "Job", "ColumnNames", 1L, createdAt, 1L,
				updatedAt, false, false, true);
	}

	@AfterEach
	void tearDown() throws Exception {
		jobCustomFieldsRepository.deleteAll();
		customFieldsEntity = null;
	}

	@Test
	void testFindAllByUser() throws Exception {
		List<CustomFieldsEntity> CustomViewList = jobCustomFieldsRepository.findAllByUser(1L, "Job", false);
		assertNotNull(CustomViewList);
	}

	@Test
	void testFindById() {
		Optional<CustomFieldsEntity> OptionalcustomEntity = jobCustomFieldsRepository.findById(1L);
		assertNotNull(OptionalcustomEntity);
	}

	@Test
	void testFindByIdAndDeleted() {
		Optional<CustomFieldsEntity> OptionalcustomEntity = jobCustomFieldsRepository.findByIdAndDeleted(1L, false,
				true);
		assertNotNull(OptionalcustomEntity);
	}

}
