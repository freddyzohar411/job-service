package com.avensys.rts.jobservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JobTimelineRequest {

	private Long id;
	private Long jobId;
	private Long candidateId;
	private Long billRate;
	private Long expectedSalary;
	private Long createdBy;
	private Long updatedBy;

}
