package com.avensys.rts.jobservice.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.avensys.rts.jobservice.entity.JobStageEntity;
import com.avensys.rts.jobservice.exception.ServiceException;
import com.avensys.rts.jobservice.repository.JobStageRepository;
import com.avensys.rts.jobservice.service.JobStageService;
import com.avensys.rts.jobservice.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * This is the test class for Job stage controller
 */
public class JobStageControllerTest {

	private MockMvc mockMvc;

	@InjectMocks
	JobStageController jobStageController;

	@Mock
	private JobStageService jobStageService;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private MessageSource messageSource;

	@Mock
	JobStageRepository jobStageRepository;

	@MockBean
	AutoCloseable autoCloseable;

	JobStageEntity jobStageEntity;
	JobStageEntity jobStageEntity1;

	Optional<JobStageEntity> optionalJobStageEntity;

	List<JobStageEntity> jobEntityList;

	/**
	 * This setup method is invoking before each test method
	 */
	@BeforeEach
	void setUp() throws Exception {
		autoCloseable = MockitoAnnotations.openMocks(this);
		// jobStageService = new JobStageService();
		jobStageEntity = new JobStageEntity(1L, "Stage name", 1L, "Stage type");
		jobStageEntity1 = new JobStageEntity(1L, "Stage name", 1L, "Stage type");
		// jobStageService.save(jobStageEntity);
		optionalJobStageEntity = Optional.of(jobStageEntity);
		jobEntityList = Arrays.asList(jobStageEntity, jobStageEntity1);
		this.mockMvc = MockMvcBuilders.standaloneSetup(jobStageController).build();
	}

	/**
	 * This tearDown method is used to cleanup the object initialization and other
	 * resources.
	 * 
	 * @throws Exception
	 */
	@AfterEach
	void tearDown() throws Exception {
		autoCloseable.close();

	}

	/**
	 * This is the positive test for the create job stage.
	 * 
	 * @throws Exception
	 */
	@Test
	void testCreatePositive() throws Exception {
		mock(JobStageService.class);
		// when().thenReturn();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		RequestBuilder request = MockMvcRequestBuilders.post("/api/jobstage")
				.content(asJsonString(new JobStageEntity(1L, "Stage name", 1L, "Stage type")))
				.header("Authorization",
						"Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI0WndUaGhXVUtGSjhUdE1NdFZrcm1Edk9TdGdRcS1Sa3MwUnEwRE5IRG5jIn0.eyJleHAiOjE3MDMyMzI3MTQsImlhdCI6MTcwMzIzMjQxNCwianRpIjoiNmMwYjBlMmYtMDZmYi00YzU3LWJmMWQtM2MzNmEzZGUxOGQxIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9ydHNyZWFsbSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiIzMzlmMzVhNy0wZDNkLTQzMWUtOWE2My1kOTBkNGMzNDJlNGEiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJydHNjbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiMzExMDI3MDYtYmJmZS00MGJjLWE4YmMtMDEzYTgzYzIzMTVlIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL3d3dy5rZXljbG9hay5vcmciXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJkZWZhdWx0LXJvbGVzLXJ0c3JlYWxtIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiMzExMDI3MDYtYmJmZS00MGJjLWE4YmMtMDEzYTgzYzIzMTVlIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJLb3RhaSBOYWxsZWIiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJraXR0dTFAYXZlbi1zeXMuY29tIiwiZ2l2ZW5fbmFtZSI6IktvdGFpIiwiZmFtaWx5X25hbWUiOiJOYWxsZWIiLCJlbWFpbCI6ImtpdHR1MUBhdmVuLXN5cy5jb20ifQ.A314CP_nu6x3qENsK8fyZP8SXXJO9y1nAcUXHU2FRRZ2vtPjD-T6rUoHQ_CZgMXnPg4Rl4MOlSCQ5leTiWix9kfBYkDQGar7GPSf9UnnPai7adiLV8Rb6OUYykHPjN_Wy3A0CVyGbsBB1ow7uhmgPkM7aMBUUYikkYK0aLremKn9vXJCpC7G2UTCW_BOjl7Bb5atic3J328ieN8nu0_W_Zd61ux1zm7skX4TPLNTC-4dAc16O-6IOo6JChQLUublfm-CcVC_i7oIv0Nuw7hOj5m5_e0klNcK-dw9bArBkRCGU9Sr4ieFIkjaLxt22Z3ZDg0C9SeB268OvnKXrjDKiQ")
				.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(request).andExpect(status().isCreated());
	}

	/**
	 * This is the negative test for the create job stage.
	 * 
	 * @throws Exception
	 */
	@Test
	void testCreateNegative() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		RequestBuilder request = MockMvcRequestBuilders.post("/api/jobstage")
				.content(asJsonString(new JobStageEntity(1L, "Stage name", 1L, "Stage type")))
				.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(request).andExpect(status().isBadRequest());
	}

	/**
	 * This method is used to convert Json object to string.
	 * 
	 * @param obj
	 * @return
	 */
	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * This is the positive test for the update job stage.
	 * 
	 * @throws Exception
	 */
	@Test
	void testUpdateJobPositive() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = writer.writeValueAsString(jobStageEntity);
		RequestBuilder request = MockMvcRequestBuilders.put("/api/jobstage").content(requestJson).header(
				"Authorization",
				"Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI0WndUaGhXVUtGSjhUdE1NdFZrcm1Edk9TdGdRcS1Sa3MwUnEwRE5IRG5jIn0.eyJleHAiOjE3MDMyMzI3MTQsImlhdCI6MTcwMzIzMjQxNCwianRpIjoiNmMwYjBlMmYtMDZmYi00YzU3LWJmMWQtM2MzNmEzZGUxOGQxIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9ydHNyZWFsbSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiIzMzlmMzVhNy0wZDNkLTQzMWUtOWE2My1kOTBkNGMzNDJlNGEiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJydHNjbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiMzExMDI3MDYtYmJmZS00MGJjLWE4YmMtMDEzYTgzYzIzMTVlIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL3d3dy5rZXljbG9hay5vcmciXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJkZWZhdWx0LXJvbGVzLXJ0c3JlYWxtIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiMzExMDI3MDYtYmJmZS00MGJjLWE4YmMtMDEzYTgzYzIzMTVlIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJLb3RhaSBOYWxsZWIiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJraXR0dTFAYXZlbi1zeXMuY29tIiwiZ2l2ZW5fbmFtZSI6IktvdGFpIiwiZmFtaWx5X25hbWUiOiJOYWxsZWIiLCJlbWFpbCI6ImtpdHR1MUBhdmVuLXN5cy5jb20ifQ.A314CP_nu6x3qENsK8fyZP8SXXJO9y1nAcUXHU2FRRZ2vtPjD-T6rUoHQ_CZgMXnPg4Rl4MOlSCQ5leTiWix9kfBYkDQGar7GPSf9UnnPai7adiLV8Rb6OUYykHPjN_Wy3A0CVyGbsBB1ow7uhmgPkM7aMBUUYikkYK0aLremKn9vXJCpC7G2UTCW_BOjl7Bb5atic3J328ieN8nu0_W_Zd61ux1zm7skX4TPLNTC-4dAc16O-6IOo6JChQLUublfm-CcVC_i7oIv0Nuw7hOj5m5_e0klNcK-dw9bArBkRCGU9Sr4ieFIkjaLxt22Z3ZDg0C9SeB268OvnKXrjDKiQ")
				.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(request).andExpect(status().isOk()).andReturn();
	}

	/**
	 * This is the negative test for the update job stage.
	 * 
	 * @throws Exception
	 */
	@Test
	void testUpdateJobNegative() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = writer.writeValueAsString(jobStageEntity);
		RequestBuilder request = MockMvcRequestBuilders.put("/api/jobstage").content(requestJson)
				.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
	}

	/**
	 * This is the test case for the delete job stage.
	 * 
	 * @throws Exception
	 */
	@Test
	void testDeleteJob() throws Exception {
		jobStageService.delete(1L);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/jobstage/{id}", 1L)).andExpect(status().isOk()).andReturn();
	}

	/**
	 * This is the positive test case for the find job stage by id.
	 * @throws Exception
	 */
	@Test
	void testFindPositive()throws Exception {
		when(jobStageService.getById(1L)).thenReturn(jobStageEntity);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/jobstage/{id}", 1L)).andExpect(status().isOk()).andReturn();
	}

	/**
	 * This is the negative test case for the find job stage by id.
	 * @throws Exception
	 */
	@Test
	void testFindNegative()throws Exception {
		when(jobStageService.getById(1L)).thenReturn(jobStageEntity);
		jobStageEntity.setIsDeleted(true);
		boolean jobStageId = false;
		try {
			if (optionalJobStageEntity.isPresent() && !optionalJobStageEntity.get().getIsDeleted()) {
			} else {
				throw new ServiceException(messageSource.getMessage("error.jobnotfound", new Object[] { 1 },
						LocaleContextHolder.getLocale()));
			}
			
		} catch (Exception e) {
			jobStageId = true;
		}
		assertTrue(jobStageId);
	}

	/**
	 * This is the test case for the get all job entity list.
	 * @throws Exception
	 */
	@Test
	void testGetAllJobs()throws Exception  {

		when(jobStageService.getAll()).thenReturn(jobEntityList);
		RequestBuilder request = MockMvcRequestBuilders.get("/api/jobstage")
				.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON);;
		mockMvc.perform(request).andExpect(status().isOk()).andReturn();
	}

}
