package com.avensys.jobservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AccountInformation {

	@NotEmpty(message = " accountName sould not be empty")
	private String accountName;
	
	@NotEmpty(message = " accountContact should not be empty")
	private String accountContact;
	
	private String salesManager;

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountContact() {
		return accountContact;
	}

	public void setAccountContact(String accountContact) {
		this.accountContact = accountContact;
	}

	public String getSalesManager() {
		return salesManager;
	}

	public void setSalesManager(String salesManager) {
		this.salesManager = salesManager;
	}
	
}
