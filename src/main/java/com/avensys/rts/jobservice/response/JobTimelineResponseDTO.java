package com.avensys.rts.jobservice.response;

import java.util.List;

import com.avensys.rts.jobservice.entity.JobTimelineEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobTimelineResponseDTO {
	private Integer totalPages;
	private Long totalElements;
	private Integer page;
	private Integer pageSize;
	private List<JobTimelineEntity> jobs;
}
