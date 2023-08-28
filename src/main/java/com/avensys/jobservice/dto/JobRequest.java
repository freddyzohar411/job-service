package com.avensys.jobservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobRequest {

    private AccountInformation accountInformation;
    private JobOpeningInformation jobOpeningInformation;
    private JobCommercials jobCommercials;
    private String jobRemarks;
	public AccountInformation getAccountInformation() {
		return accountInformation;
	}
	public void setAccountInformation(AccountInformation accountInformation) {
		this.accountInformation = accountInformation;
	}
	public JobOpeningInformation getJobOpeningInformation() {
		return jobOpeningInformation;
	}
	public void setJobOpeningInformation(JobOpeningInformation jobOpeningInformation) {
		this.jobOpeningInformation = jobOpeningInformation;
	}
	public JobCommercials getJobCommercials() {
		return jobCommercials;
	}
	public void setJobCommercials(JobCommercials jobCommercials) {
		this.jobCommercials = jobCommercials;
	}
	public String getJobRemarks() {
		return jobRemarks;
	}
	public void setJobRemarks(String jobRemarks) {
		this.jobRemarks = jobRemarks;
	}
    
    
}
