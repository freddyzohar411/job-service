package com.avensys.rts.jobservice.apiclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.avensys.rts.jobservice.interceptor.JwtTokenInterceptor;
import com.avensys.rts.jobservice.payload.FormSubmissionsRequestDTO;
import com.avensys.rts.jobservice.response.HttpResponse;

@FeignClient(name = "form-service", url = "http://localhost:9400", configuration = JwtTokenInterceptor.class)
public interface FormSubmissionAPIClient {

	@PostMapping("/form-submissions")
	HttpResponse addFormSubmission(@RequestBody FormSubmissionsRequestDTO formSubmissionsRequestDTO);

	@GetMapping("/form-submissions/{formSubmissionId}")
	HttpResponse getFormSubmission(@PathVariable Long formSubmissionId);

	@PutMapping("/form-submissions/{formSubmissionId}")
	HttpResponse updateFormSubmission(@PathVariable Long formSubmissionId,
			@RequestBody FormSubmissionsRequestDTO formSubmissionsRequestDTO);

	@DeleteMapping("/form-submissions/{formSubmissionId}")
	HttpResponse deleteFormSubmission(@PathVariable Long formSubmissionId);

}
