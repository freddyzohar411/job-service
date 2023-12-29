package com.avensys.rts.jobservice.response;

import com.avensys.rts.jobservice.entity.JobEntity;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobListingDataDTO {
	private Long id;
	private String title;
	private JsonNode jobSubmissionData;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public JobListingDataDTO(JobEntity jobEntity) {
		this.id = jobEntity.getId();
		this.jobSubmissionData= jobEntity.getJobSubmissionData();
		this.createdAt = jobEntity.getCreatedAt();
		this.updatedAt = jobEntity.getUpdatedAt();

	}
	private String createdByName;
	private String updatedByName;
}
