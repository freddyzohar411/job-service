package com.avensys.rts.jobservice.apiclient;

import com.avensys.rts.jobservice.payload.EmailMultiRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.avensys.rts.jobservice.interceptor.JwtTokenInterceptor;
import com.avensys.rts.jobservice.payload.EmailMultiTemplateRequestDTO;
import com.avensys.rts.jobservice.response.HttpResponse;

import java.net.MalformedURLException;

/**
 * @author Rahul Sahu This class is an interface to interact with document
 *         microservice
 */
@Configuration
@FeignClient(name = "ms-common-util", url = "${api.email.url}", configuration = JwtTokenInterceptor.class)
public interface EmailAPIClient {

	@PostMapping(value = "/sendingEmail-service/template")
	HttpResponse sendEmailServiceTemplate(@ModelAttribute EmailMultiTemplateRequestDTO emailMultiTemplateRequestDTO);

	@PostMapping(value="/sendingEmail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	HttpResponse sendEmail(@ModelAttribute EmailMultiRequestDTO emailMultiRequestDTO);

}
