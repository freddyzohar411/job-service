package com.avensys.rts.jobservice.response;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobTimelineTagDTO {
	private Timestamp date;
	private String status;
	private Long order;
}
