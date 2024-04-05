package com.avensys.rts.jobservice.email.executerservice;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Rahul sahu
 * @description Send bulk email executer service
 */
@Component
public class JobFODMailService {

	private final Logger log = LoggerFactory.getLogger(JobFODEmailThread.class);

	public void sendEmails(Set<JobFODEmailThread> emailTasks) {
		if (emailTasks.size() > 0) {
			ExecutorService executorService = null;
			try {
				executorService = Executors.newSingleThreadExecutor();
				executorService.invokeAll(emailTasks);
			} catch (Exception e) {
				log.error("Error:", e);
			} finally {
				if (executorService != null) {
					executorService.shutdown();
				}
			}
		}
	}
}
