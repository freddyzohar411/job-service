package com.avensys.rts.jobservice.payload;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Author: Rahul Sahu This class is used to store the form submission request
 * parameters for account service
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FormSubmissionsRequestDTO {
	private Long formId;
	private Long userId;
	private JsonNode submissionData;
	private Long entityId;
	private String entityType;
}
