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

@FeignClient(name = "form-service", url = "${api.form-submission.url}", configuration = JwtTokenInterceptor.class)
public interface FormSubmissionAPIClient {

	@PostMapping("")
	HttpResponse addFormSubmission(@RequestBody FormSubmissionsRequestDTO formSubmissionsRequestDTO);

	@GetMapping("/{formSubmissionId}")
	HttpResponse getFormSubmission(@PathVariable int formSubmissionId);

	@PutMapping("/{formSubmissionId}")
	HttpResponse updateFormSubmission(@PathVariable int formSubmissionId,
			@RequestBody FormSubmissionsRequestDTO formSubmissionsRequestDTO);

	@DeleteMapping("/{formSubmissionId}")
	HttpResponse deleteFormSubmission(@PathVariable int formSubmissionId);

}
