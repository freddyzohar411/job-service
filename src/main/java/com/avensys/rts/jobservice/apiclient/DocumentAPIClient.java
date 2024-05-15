package com.avensys.rts.jobservice.apiclient;

import com.avensys.rts.jobservice.payload.DocumentKeyRequestDTO;
import com.avensys.rts.jobservice.payload.UpdateDocumentListKeyDTO;
import feign.Headers;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import feign.form.spring.SpringFormEncoder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.avensys.rts.jobservice.interceptor.JwtTokenInterceptor;
import com.avensys.rts.jobservice.payload.DocumentDeleteRequestDTO;
import com.avensys.rts.jobservice.payload.DocumentRequestDTO;
import com.avensys.rts.jobservice.response.HttpResponse;
import org.springframework.web.client.RestTemplate;

/**
 * author Rahul Sahu This class is an interface to interact with document
 * microservice
 */
@Configuration
@FeignClient(name = "document-service", url = "${api.document.url}", configuration = { JwtTokenInterceptor.class,
		DocumentAPIClient.MultipartSupportConfig.class })
public interface DocumentAPIClient {
	@PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	HttpResponse createDocument(@ModelAttribute DocumentRequestDTO documentRequest);

	@PutMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	HttpResponse updateDocument(@ModelAttribute DocumentRequestDTO documentRequest);

	@DeleteMapping("")
	HttpResponse deleteDocumentByEntityIdAndType(@RequestBody DocumentDeleteRequestDTO documentDeleteRequestDTO);

	@GetMapping("")
	HttpResponse getDocumentByEntityTypeAndId(@RequestParam String entityType, @RequestParam int entityId);

	@DeleteMapping("/entity/{entityType}/{entityId}")
	HttpResponse deleteDocumentsByEntityTypeAndEntityId(@PathVariable String entityType,
			@PathVariable Integer entityId);

	@PostMapping(value = "/update/list-with-keys", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Headers("Content-Type: multipart/form-data")
	HttpResponse updateDocumentListWithKeys(@RequestPart("entityType") String entityType,
			@RequestPart("entityId") int entityId,
			@RequestPart("documentKeyRequestDTO") DocumentKeyRequestDTO[] documentKeyRequestDTO);

	class MultipartSupportConfig {
		@Bean
		public Encoder feignFormEncoder() {
			return new SpringFormEncoder(new SpringEncoder(new ObjectFactory<HttpMessageConverters>() {
				@Override
				public HttpMessageConverters getObject() {
					return new HttpMessageConverters(new RestTemplate().getMessageConverters());
				}
			}));
		}
	}
}
