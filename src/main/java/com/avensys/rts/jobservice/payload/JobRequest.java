package com.avensys.rts.jobservice.payload;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobRequest {

	private Long id;
	private String title;
	private Long formId;
	private String formData;
	private Long createdBy;
	private Long updatedBy;
//	@NotNull(message = "File cannot be null")
//    @ValidPdfFile(message = "File must be a PDF file")
//    @FileSize(maxSize = 1, message = "File size must be less than 1MB")
	private MultipartFile uploadJobDocuments;
}
