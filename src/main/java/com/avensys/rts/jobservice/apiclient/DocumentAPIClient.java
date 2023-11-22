package com.avensys.rts.jobservice.apiclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.avensys.rts.jobservice.payload.DocumentRequestDTO;
import com.avensys.rts.jobservice.response.HttpResponse;

/**
 * author Rahul Sahu This class is an interface to interact with document
 * microservice
 */
@Configuration
@FeignClient(name = "document-service", url = "http://localhost:8500")
public interface DocumentAPIClient {
	@PostMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	HttpResponse createDocument(@ModelAttribute DocumentRequestDTO documentRequest);

	@PutMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	HttpResponse updateDocument(@ModelAttribute DocumentRequestDTO documentRequest);

	@GetMapping("/documents")
	HttpResponse getDocumentByEntityTypeAndId(@RequestParam String entityType, @RequestParam int entityId);

}
