package com.avensys.rts.jobservice.payload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Author: Rahul Sahu This class is used to store the request parameters for the
 * account listing api
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JobListingRequestDTO {
	private Integer page = 0;
	private Integer pageSize = 5;
	private String sortBy;
	private String sortDirection;
	private String searchTerm;
	private Long userId;
	private String jobType;
	private Boolean getAll = false;
	private String email;
	private List<String> searchFields;
	private Long jobId;
	private Boolean allActive = false;
	private Integer stageType = null;
	private Boolean isDownload = false;
	private List<FilterDTO> filters;
}
