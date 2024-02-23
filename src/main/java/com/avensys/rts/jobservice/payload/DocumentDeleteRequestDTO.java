package com.avensys.rts.jobservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * author: Rahul Sahu This is the DTO class for the document delete request
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDeleteRequestDTO {
	private String entityType;
	private Long entityId;
}
