package com.avensys.rts.jobservice.payload;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormSubmissionsRequestDTO {
	private Integer formId;
	private Long userId;
	private JsonNode submissionData;
	private Long entityId;
	private String entityType;
}
