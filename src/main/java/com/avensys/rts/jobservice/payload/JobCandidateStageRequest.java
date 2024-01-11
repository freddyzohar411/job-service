package com.avensys.rts.jobservice.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobCandidateStageRequest {

	private Long id;
	private Long jobId;
	private Long jobStageId;
	private String status;
	private Long candidateId;
	private Integer formId;
	private Integer formSubmissionId;
	private Long createdBy;
	private Long updatedBy;

}
