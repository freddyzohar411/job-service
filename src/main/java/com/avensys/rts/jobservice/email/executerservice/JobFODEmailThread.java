package com.avensys.rts.jobservice.email.executerservice;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.avensys.rts.jobservice.apiclient.EmailAPIClient;
import com.avensys.rts.jobservice.payload.EmailMultiTemplateRequestDTO;

/**
 * @author Rahul sahu
 * @description Send email thread
 */
@Component
public class JobFODEmailThread implements Callable<Boolean> {

	private final Logger log = LoggerFactory.getLogger(JobFODEmailThread.class);

	private EmailAPIClient emailAPIClient;
	private EmailMultiTemplateRequestDTO dto;

	public JobFODEmailThread(EmailMultiTemplateRequestDTO dto, EmailAPIClient emailAPIClient) {
		this.dto = dto;
		this.emailAPIClient = emailAPIClient;
	}

	@Override
	public Boolean call() throws Exception {
		try {
			emailAPIClient.sendEmailServiceTemplate(dto);
			return true;
		} catch (Exception e) {
			log.error("Error:", e);
			return false;
		}
	}

}
