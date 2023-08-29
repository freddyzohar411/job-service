package com.avensys.jobservice.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class JobCommercials {

	@NotEmpty(message = "salaryBudgetLocal cannot be empty")
	private String salaryBudgetLocal;
	
	private String localCurrency;
	
	@NotEmpty(message = "budgetType cannot be empty")
	@Length (max =20)
	private String budgetType;
	
	private String salaryBudgetSGD;
	
	private String expectedMargin;
	
	private String expectedMarginSGD;
	
	private Screening screening;
	
}
