package com.avensys.rts.jobservice.payload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: Rahul Sahu This class is used to store the request parameters for the
 * account listing api
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobListingRequestDTO {
	private Integer page = 0;
	private Integer pageSize = 5;
	private String sortBy;
	private String sortDirection;
	private String searchTerm;
	private List<String> searchFields;
	private String jobType;
}
