package com.avensys.rts.jobservice.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomFieldsResponseDTO {
	private Long id;
	private String name;
	private String type;
	private List<String> columnName;
	private Long createdBy;
	private Long updatedBy;
}
