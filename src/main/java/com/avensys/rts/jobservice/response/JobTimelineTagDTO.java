package com.avensys.rts.jobservice.response;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JobTimelineTagDTO {
	private Timestamp date;
	private String status;
	private Long order;
}
