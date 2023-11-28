package com.avensys.rts.jobservice.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author: Rahul Sahu This is the DTO class for the document delete request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDeleteRequestDTO {
	private String entityType;
	private Long entityId;
}
