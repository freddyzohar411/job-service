package com.avensys.rts.jobservice.apiclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.avensys.rts.jobservice.interceptor.JwtTokenInterceptor;
import com.avensys.rts.jobservice.payload.EmbeddingRequestDTO;
import com.avensys.rts.jobservice.response.HttpResponse;

/**
 * @author Rahul Sahu
 * @description This class is an interface to interact with document
 *              microservice
 */
@Configuration
@FeignClient(name = "embedding-service", url = "${api.embeddings.url}", configuration = JwtTokenInterceptor.class)
public interface EmbeddingAPIClient {

	@PostMapping("/get/single")
	HttpResponse getEmbeddingSingle(@RequestBody EmbeddingRequestDTO embeddingRequestDTO);

	@PostMapping("/get/single/py")
	HttpResponse getEmbeddingSinglePy(@RequestBody EmbeddingRequestDTO embeddingRequestDTO);

}
