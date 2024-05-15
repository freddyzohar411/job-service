package com.avensys.rts.jobservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TosRequestDTO {
	
	private Long id;
	private Long candidateId;
	private Long JobId;
	private String Status;
	private Long SalesUserId;
	private Long formId;
	private Long createdBy;
	private Long updatedBy;

}
