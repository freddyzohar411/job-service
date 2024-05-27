package com.avensys.rts.jobservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JobCandidateStageWithFilesRequest {
	private Long id;
	private Long jobId;
	private Long jobStageId;
	private String status;
	private String stepName;
	private String subStepName;
	private Long candidateId;
	private Long formId;
	private Long formSubmissionId;
	private String jobType;
	private String formData;
	private Long createdBy;
	private Long updatedBy;
	FileDataDTO[] files;
}
