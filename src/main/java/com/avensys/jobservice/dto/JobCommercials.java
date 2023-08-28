package com.avensys.jobservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class JobCommercials {

	@NotEmpty(message = "salaryBudgetLocal cannot be empty")
	private String salaryBudgetLocal;
	
	private String localCurrency;
	
	@NotEmpty(message = "budgetType cannot be empty") // can't extend more then 20 characters.
	private String budgetType;
	
	private String salaryBudgetSGD;
	
	private String expectedMargin;
	
	private String expectedMarginSGD;
	
	private Screening screening;

	public String getSalaryBudgetLocal() {
		return salaryBudgetLocal;
	}

	public void setSalaryBudgetLocal(String salaryBudgetLocal) {
		this.salaryBudgetLocal = salaryBudgetLocal;
	}

	public String getLocalCurrency() {
		return localCurrency;
	}

	public void setLocalCurrency(String localCurrency) {
		this.localCurrency = localCurrency;
	}

	public String getBudgetType() {
		return budgetType;
	}

	public void setBudgetType(String budgetType) {
		this.budgetType = budgetType;
	}

	public String getSalaryBudgetSGD() {
		return salaryBudgetSGD;
	}

	public void setSalaryBudgetSGD(String salaryBudgetSGD) {
		this.salaryBudgetSGD = salaryBudgetSGD;
	}

	public String getExpectedMargin() {
		return expectedMargin;
	}

	public void setExpectedMargin(String expectedMargin) {
		this.expectedMargin = expectedMargin;
	}

	public String getExpectedMarginSGD() {
		return expectedMarginSGD;
	}

	public void setExpectedMarginSGD(String expectedMarginSGD) {
		this.expectedMarginSGD = expectedMarginSGD;
	}

	public Screening getScreening() {
		return screening;
	}

	public void setScreening(Screening screening) {
		this.screening = screening;
	}
	
}
