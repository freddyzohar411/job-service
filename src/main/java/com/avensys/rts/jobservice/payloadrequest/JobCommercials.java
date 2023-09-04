package com.avensys.rts.jobservice.payloadrequest;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class JobCommercials {

	@NotEmpty(message = "salaryBudgetLocal cannot be null")
	private String salaryBudgetLocal;
	
	private String localCurrency;
	
	@NotEmpty(message = "budgetType cannot be empty")
	@Length (max =20)
	private String budgetType;
	
	private int salaryBudgetSGD;
	
	private String expectedMarginCurrency;
	
	private int expectedMarginMin;
	
	private int expectedMarginMax;
	
	private int expectedMarginSGD;
	
	private Screening screening;
	
}
