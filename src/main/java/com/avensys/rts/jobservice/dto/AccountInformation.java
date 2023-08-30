package com.avensys.rts.jobservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountInformation {

	@NotNull(message = " accountId sould not be null")
	private Integer accountId;
	
}
