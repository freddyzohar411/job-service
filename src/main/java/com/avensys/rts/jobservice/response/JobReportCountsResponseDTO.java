package com.avensys.rts.jobservice.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JobReportCountsResponseDTO {
	private Long newRequirementsCount;
	private Long activeRequirementsCount;
	private Long noSubmissionsCount;
	private Long associatedCount;
	private Long submitToSalesCount;
	private Long submitToClientCount;
	private Long interviewScheduledCount;
	private Long interviewHappenedCount;
	private Long selectedCount;
	private Long rejectedCount;



}
