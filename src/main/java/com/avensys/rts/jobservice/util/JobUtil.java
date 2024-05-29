package com.avensys.rts.jobservice.util;

import com.avensys.rts.jobservice.entity.CandidateEntity;
import com.avensys.rts.jobservice.entity.JobEntity;

/**
 * @author Rahul Sahu
 */
public class JobUtil {

	// Template category
	public static String EMAIL_TEMPLATE = "Email Templates";

	// Template names
	public static String NEW_JOB_NOTIFICATION = "New Job Notification";

	public static String getValue(Object input, String key) {
		String output = "N/A";
		try {
			if (input instanceof JobEntity && ((JobEntity) input).getJobSubmissionData().get(key).asText() != null) {
				output = ((JobEntity) input).getJobSubmissionData().get(key).asText();
			} else if (input instanceof CandidateEntity
					&& ((CandidateEntity) input).getCandidateSubmissionData().get(key).asText() != null) {
				output = ((CandidateEntity) input).getCandidateSubmissionData().get(key).asText();
			}
		} catch (Exception e) {
			output = "N/A";
		}
		return output;
	}
}
