package com.avensys.rts.jobservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JobRequest {

	private Long id;
	private String title;
	private Long formId;
	private Long tempDocId;
	private String formData;
	private Boolean isDraft;
	private Long createdBy;
	private Long updatedBy;
}
