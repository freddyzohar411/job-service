package com.avensys.rts.jobservice.util;

import com.avensys.rts.jobservice.entity.CandidateEntity;
import com.avensys.rts.jobservice.entity.JobEntity;

public class JobCanddateStageUtil {

	public static Long ASSOCIATE = 2l;
	public static Long SUBMIT_TO_SALES = 3l;
	public static Long SUBMIT_TO_CLIENT = 4l;
	public static Long PROFILE_FEEDBACK_PENDING_ID = 5l;
	public static Long FIRST_INTERVIEW_SCHEDULED_ID = 6l;
	public static Long SECOND_INTERVIEW_SCHEDULED = 7l;
	public static Long THIRD_INTERVIEW_SCHEDULED = 8l;
	public static Long INTERVIEW_FEEDBACK_PENDING = 9l;
	public static Long CONDITIONAL_OFFER_SENT = 10l;
	public static Long CONDITIONAL_OFFER_ACCEPTED_OR_DECLINED = 11l;

	public static String COMPLETED = "COMPLETED";

	public static String JOB_ASSOCIATE_TEMPLATE = "Job Associate";
	public static String JOB_TEMPLATE_CATEGORY = "Email Templates";

	public static String getValue(Object input, String key) {
		String output = "N/A";
		if (input instanceof JobEntity && ((JobEntity) input).getJobSubmissionData().get(key).asText() != null) {
			output = ((JobEntity) input).getJobSubmissionData().get(key).asText();
		} else if (input instanceof CandidateEntity
				&& ((CandidateEntity) input).getCandidateSubmissionData().get(key).asText() != null) {
			output = ((CandidateEntity) input).getCandidateSubmissionData().get(key).asText();
		}
		return output;
	}

}
