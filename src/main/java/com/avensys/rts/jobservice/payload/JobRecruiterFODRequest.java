package com.avensys.rts.jobservice.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobRecruiterFODRequest {

	private Long[] jobId;
	private Long[] recruiterId;
	private Long sellerId;
	private Long createdBy;
	private Long updatedBy;
}
