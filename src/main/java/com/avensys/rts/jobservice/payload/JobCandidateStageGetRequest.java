package com.avensys.rts.jobservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JobCandidateStageGetRequest {
	private Long jobId;
	private Long jobStageId;
	private Long candidateId;
}
