package com.avensys.rts.jobservice.response;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormSubmissionsResponseDTO {
	private Long id;
	private String formId;
	private Long userId;
	private JsonNode submissionData;
	private Long entityId;
	private String entityType;
}
