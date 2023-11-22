package com.avensys.rts.jobservice.payload;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author: Rahul Sahu This is the DTO class for the document request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentRequestDTO {

	private String type;
	private String title;
	private String description;
	private Long entityId;
	private String entityType;
	private MultipartFile file;

}