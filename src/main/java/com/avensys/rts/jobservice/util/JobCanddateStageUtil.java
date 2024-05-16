package com.avensys.rts.jobservice.util;

import com.avensys.rts.jobservice.entity.CandidateEntity;
import com.avensys.rts.jobservice.entity.JobEntity;

/**
 * @author Rahul Sahu
 */
public class JobCanddateStageUtil {

	// Job stage ids
	public static Long ASSOCIATE = 2l;
	public static Long SUBMIT_TO_SALES = 3l;
	public static Long SUBMIT_TO_CLIENT = 4l;
	public static Long PROFILE_FEEDBACK_PENDING_ID = 5l;
	public static Long SKILLS_ASSESSMENT_ID = 6l;
	public static Long CODING_TEST_ID = 7l;
	public static Long TECHNICAL_INTERVIEW_ID = 8l;
	public static Long CULTURAL_FIT_TEST_ID = 9l;
	public static Long FIRST_INTERVIEW_SCHEDULED_ID = 10l;
	public static Long SECOND_INTERVIEW_SCHEDULED = 11l;
	public static Long THIRD_INTERVIEW_SCHEDULED = 12l;
	public static Long INTERVIEW_FEEDBACK_PENDING = 13l;
	public static Long CONDITIONAL_OFFER_SENT = 10l;
	public static Long CONDITIONAL_OFFER_ACCEPTED_OR_DECLINED = 11l;

	// Job stage Name
	public static String FIRST_INTERVIEW_SCHEDULED_NAME = "First Interview Scheduled";
	public static String SECOND_INTERVIEW_SCHEDULED_NAME = "Second Interview Scheduled";
	public static String THIRD_INTERVIEW_SCHEDULED_NAME = "Third Interview Scheduled";
	public static String CONDITIONAL_OFFER_SENT_NAME = "Conditional Offer Sent";
	public static String CONDITIONAL_OFFER_ACCEPTED_NAME = "Conditional Offer Accepted/Declined";

	public static String PREPARE_TERM_OF_SERVICE = "Prepare TOS";

	// Template category
	public static String JOB_TEMPLATE_CATEGORY = "Email Templates";

	// Template names
	public static String ASSOCIATE_TEMPLATE = "Associate";
	public static String SUBMIT_TO_SALES_TEMPLATE = "Submit to Sales";
	public static String SUBMIT_TO_CLIENT_TEMPLATE = "Submit to Client";
	public static String REJECT_TEMPLATE = "Reject";
	public static String WITHDRAWN_TEMPLATE = "Candidate Withdrawn";
	public static String INTERVIEW_SCHEDULED_TEMPLATE = "Interview Scheduled";
	public static String CONDITIONAL_OFFER_TEMPLATE = "Conditional Offer";
	public static String OFFER_ACCEPTED_TEMPLATE = "Offer Accepted";

	// Job status
	public static String COMPLETED = "COMPLETED";
	public static String REJECTED = "REJECTED";
	public static String WITHDRAWN = "WITHDRAWN";
	public static String IN_PROGRESS = "IN_PROGRESS";

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

	public static String validateValue(String value) {
		String output = "N/A";
		if (value != null && value.length() > 0) {
			output = value;
		}
		return output;
	}

}
