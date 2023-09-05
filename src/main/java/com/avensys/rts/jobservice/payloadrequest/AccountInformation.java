package com.avensys.rts.jobservice.payloadrequest;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountInformation {

	@NotNull(message = " accountId sould not be null")
	private Integer accountId;
	
}
