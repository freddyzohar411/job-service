package com.avensys.rts.jobservice.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.avensys.rts.jobservice.entity.CandidateEntity;
import com.avensys.rts.jobservice.entity.JobCandidateStageEntity;
import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.entity.JobStageEntity;
import com.avensys.rts.jobservice.payload.JobCandidateStageRequest;
import com.avensys.rts.jobservice.service.JobCandidateStageService;
import com.avensys.rts.jobservice.util.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * This is the test class for Job candidate stage controller
 */
public class JobCandidateStageControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@InjectMocks
	JobCandidateStageController jobCandidateStageController;

	@Mock
	private JobCandidateStageService jobCandidateStageService;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private MessageSource messageSource;

	@MockBean
	AutoCloseable autoCloseable;

	List<JobCandidateStageEntity> jobCandidateEntityList;
	private JobEntity jobEntity;
	private JobStageEntity jobStageEntity;
	private CandidateEntity candidateEntity;
	JsonNode jobSubmissionData;
	JobCandidateStageRequest jobCandidateStageRequest;
	JobCandidateStageRequest jobCandidateStageRequest1;
	JobCandidateStageRequest[] jobCandidateStageRequests;
	JobCandidateStageEntity jobCandidateStageEntity;
	JobCandidateStageEntity jobCandidateStageEntity1;

	/**
	 * This setup method is invoking before each test method
	 */
	@BeforeEach
	void setUp() {
		autoCloseable = MockitoAnnotations.openMocks(this);
		jobEntity = new JobEntity(1L, "Java Developer", 1L, 1L, false, jobSubmissionData);
		jobCandidateStageEntity = new JobCandidateStageEntity(1L, jobEntity, jobStageEntity, "Active", candidateEntity,
				1L, 1L, jobSubmissionData, null, jobSubmissionData);
		jobCandidateStageEntity1 = new JobCandidateStageEntity(2L, jobEntity, jobStageEntity, "Active", candidateEntity,
				1L, 1L, jobSubmissionData, null, jobSubmissionData);
		jobCandidateStageRequest = new JobCandidateStageRequest(1L, 1L, 1L, "Active", null, null, 1L, 1L, 1L,
				"full time", "form data", 1L, 1L);
		jobCandidateStageRequest1 = new JobCandidateStageRequest(2L, 1L, 1L, "Active", null, null, 1L, 1L, 1L,
				"part time", "form data", 1L, 1L);
		jobCandidateStageRequests = new JobCandidateStageRequest[2];
		jobCandidateStageRequests[0] = jobCandidateStageRequest;
		jobCandidateStageRequests[1] = jobCandidateStageRequest1;
		jobCandidateEntityList = Arrays.asList(jobCandidateStageEntity, jobCandidateStageEntity1);
		this.mockMvc = MockMvcBuilders.standaloneSetup(jobCandidateStageController).build();
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
	 * This is the positive test for the tag a job.
	 * 
	 * @throws Exception
	 */
	@Test
	void testCreatePositive() throws Exception {
		mock(JobCandidateStageService.class);
		mock(JobCandidateStageController.class);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		RequestBuilder request = MockMvcRequestBuilders.post("/api/jobcandidatestage")
				.content(asJsonString(jobCandidateStageRequest))
				.header("Authorization",
						"Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI0WndUaGhXVUtGSjhUdE1NdFZrcm1Edk9TdGdRcS1Sa3MwUnEwRE5IRG5jIn0.eyJleHAiOjE3MDMyMzI3MTQsImlhdCI6MTcwMzIzMjQxNCwianRpIjoiNmMwYjBlMmYtMDZmYi00YzU3LWJmMWQtM2MzNmEzZGUxOGQxIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9ydHNyZWFsbSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiIzMzlmMzVhNy0wZDNkLTQzMWUtOWE2My1kOTBkNGMzNDJlNGEiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJydHNjbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiMzExMDI3MDYtYmJmZS00MGJjLWE4YmMtMDEzYTgzYzIzMTVlIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL3d3dy5rZXljbG9hay5vcmciXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJkZWZhdWx0LXJvbGVzLXJ0c3JlYWxtIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiMzExMDI3MDYtYmJmZS00MGJjLWE4YmMtMDEzYTgzYzIzMTVlIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJLb3RhaSBOYWxsZWIiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJraXR0dTFAYXZlbi1zeXMuY29tIiwiZ2l2ZW5fbmFtZSI6IktvdGFpIiwiZmFtaWx5X25hbWUiOiJOYWxsZWIiLCJlbWFpbCI6ImtpdHR1MUBhdmVuLXN5cy5jb20ifQ.A314CP_nu6x3qENsK8fyZP8SXXJO9y1nAcUXHU2FRRZ2vtPjD-T6rUoHQ_CZgMXnPg4Rl4MOlSCQ5leTiWix9kfBYkDQGar7GPSf9UnnPai7adiLV8Rb6OUYykHPjN_Wy3A0CVyGbsBB1ow7uhmgPkM7aMBUUYikkYK0aLremKn9vXJCpC7G2UTCW_BOjl7Bb5atic3J328ieN8nu0_W_Zd61ux1zm7skX4TPLNTC-4dAc16O-6IOo6JChQLUublfm-CcVC_i7oIv0Nuw7hOj5m5_e0klNcK-dw9bArBkRCGU9Sr4ieFIkjaLxt22Z3ZDg0C9SeB268OvnKXrjDKiQ")
				.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(request).andExpect(status().isCreated());

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
	 * This is the negative test for the tag a job.
	 * 
	 * @throws Exception
	 */
	@Test
	void testCreateNegative() throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		RequestBuilder request = MockMvcRequestBuilders.post("/api/jobcandidatestage")
				.content(asJsonString(jobCandidateStageRequest)).contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(request).andExpect(status().isBadRequest());
	}

	/**
	 * This is the positive test for the tag all jobs.
	 * 
	 * @throws Exception
	 */
	@Test
	void testCreateAllPositive() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		RequestBuilder request = MockMvcRequestBuilders.post("/api/jobcandidatestage/createAll")
				.content(asJsonString(jobCandidateStageRequests))
				.header("Authorization",
						"Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI0WndUaGhXVUtGSjhUdE1NdFZrcm1Edk9TdGdRcS1Sa3MwUnEwRE5IRG5jIn0.eyJleHAiOjE3MDMyMzI3MTQsImlhdCI6MTcwMzIzMjQxNCwianRpIjoiNmMwYjBlMmYtMDZmYi00YzU3LWJmMWQtM2MzNmEzZGUxOGQxIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9ydHNyZWFsbSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiIzMzlmMzVhNy0wZDNkLTQzMWUtOWE2My1kOTBkNGMzNDJlNGEiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJydHNjbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiMzExMDI3MDYtYmJmZS00MGJjLWE4YmMtMDEzYTgzYzIzMTVlIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwczovL3d3dy5rZXljbG9hay5vcmciXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJkZWZhdWx0LXJvbGVzLXJ0c3JlYWxtIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiMzExMDI3MDYtYmJmZS00MGJjLWE4YmMtMDEzYTgzYzIzMTVlIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJLb3RhaSBOYWxsZWIiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJraXR0dTFAYXZlbi1zeXMuY29tIiwiZ2l2ZW5fbmFtZSI6IktvdGFpIiwiZmFtaWx5X25hbWUiOiJOYWxsZWIiLCJlbWFpbCI6ImtpdHR1MUBhdmVuLXN5cy5jb20ifQ.A314CP_nu6x3qENsK8fyZP8SXXJO9y1nAcUXHU2FRRZ2vtPjD-T6rUoHQ_CZgMXnPg4Rl4MOlSCQ5leTiWix9kfBYkDQGar7GPSf9UnnPai7adiLV8Rb6OUYykHPjN_Wy3A0CVyGbsBB1ow7uhmgPkM7aMBUUYikkYK0aLremKn9vXJCpC7G2UTCW_BOjl7Bb5atic3J328ieN8nu0_W_Zd61ux1zm7skX4TPLNTC-4dAc16O-6IOo6JChQLUublfm-CcVC_i7oIv0Nuw7hOj5m5_e0klNcK-dw9bArBkRCGU9Sr4ieFIkjaLxt22Z3ZDg0C9SeB268OvnKXrjDKiQ")
				.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(request).andExpect(status().isCreated());
	}

	/**
	 * This is the negative test for the tag all jobs.
	 * 
	 * @throws Exception
	 */
	@Test
	void testCreateAllNegative() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		RequestBuilder request = MockMvcRequestBuilders.post("/api/jobcandidatestage/createAll")
				.content(asJsonString(jobCandidateStageRequests)).contentType(MediaType.APPLICATION_JSON_VALUE)
				.accept(MediaType.APPLICATION_JSON);
		mockMvc.perform(request).andExpect(status().isBadRequest());
	}

	/**
	 * This is the test for the delete a job.
	 * 
	 * @throws Exception
	 */
	@Test
	void testDeleteJob() throws Exception {
		jobCandidateStageService.delete(1L);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/jobcandidatestage/{id}", 1L)).andExpect(status().isOk())
				.andReturn();
	}

	/**
	 *  This is the test for the get all tag jobs.
	 * @throws Exception
	 */
	@Test
	void testGetAll() throws Exception{
		when(jobCandidateStageService.getAll(1,10,"Super Admin")).thenReturn(jobCandidateEntityList);
		RequestBuilder request = MockMvcRequestBuilders.get("/api/jobcandidatestage")
				.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON);;
		mockMvc.perform(request).andExpect(status().isOk()).andReturn();

	}

	/**
	 *  This is the test for the find a job.
	 * @throws Exception
	 */
	@Test
	void testFind()throws Exception  {
		when(jobCandidateStageService.getById(1L)).thenReturn(jobCandidateStageEntity);
		mockMvc.perform(MockMvcRequestBuilders.get("/api/jobcandidatestage/{id}", 1L)).andExpect(status().isOk()).andReturn();
	}

}
