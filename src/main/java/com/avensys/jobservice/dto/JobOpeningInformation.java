package com.avensys.jobservice.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class JobOpeningInformation {

	@NotEmpty (message = "jobTitle cannot be empty and can't extend more then 50 characters.")
	private String jobTitle;
	
	@NotEmpty(message = "dateOpen cannot be null.")
	private LocalDateTime dateOpen;
	
	private LocalDateTime targetClosingDate;
	
	private String clientJobID;
	
    @NotEmpty (message = "jobType cannot be empty and can't extend more then 20 characters.")
    private String jobType;
    
    private String duration;
    
    @NotEmpty (message = "primarySkills cannot be empty and can't extend more then 500 characters.")
    private String primarySkills;
    
    @NotEmpty (message = "secondarySkills cannot be emptyand can't extend more then 500 characters.")
    private String secondarySkills;
    
    @NotEmpty (message = " noOfHeadcounts not be null .")
    private String noOfHeadcounts;
    
    @NotEmpty (message = " workType cannot be empty and can't extend more then 20 characters.")
    private String workType;
    
    @NotEmpty (message = "jobDescription cannot be empty.")//can't extend more then 2500 characters.
    private String jobDescription;
    
    @NotEmpty (message = " visaStatus cannot be empty.")//can't extend more then 20 characters.
    private String visaStatus;
    
    @NotEmpty (message = " country cannot be empty")
    private String country;
    
    private String languages;
    
    private String requiredDocuments;
    
    private String workLocation;
    
    private String priority;
    
    private String qualification;
    
    @NotEmpty (message = "turnaroundTime cannot be null.")
    private String turnaroundTime;
    
    private String jobRatingSales;
    
    private String securityClearance;
    
	public String getJobTitle() {
		return jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
	public LocalDateTime getDateOpen() {
		return dateOpen;
	}
	public void setDateOpen(LocalDateTime dateOpen) {
		this.dateOpen = dateOpen;
	}
	public LocalDateTime getTargetClosingDate() {
		return targetClosingDate;
	}
	public void setTargetClosingDate(LocalDateTime targetClosingDate) {
		this.targetClosingDate = targetClosingDate;
	}
	public String getClientJobID() {
		return clientJobID;
	}
	public void setClientJobID(String clientJobID) {
		this.clientJobID = clientJobID;
	}
	public String getJobType() {
		return jobType;
	}
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getPrimarySkills() {
		return primarySkills;
	}
	public void setPrimarySkills(String primarySkills) {
		this.primarySkills = primarySkills;
	}
	public String getSecondarySkills() {
		return secondarySkills;
	}
	public void setSecondarySkills(String secondarySkills) {
		this.secondarySkills = secondarySkills;
	}
	public String getNoOfHeadcounts() {
		return noOfHeadcounts;
	}
	public void setNoOfHeadcounts(String noOfHeadcounts) {
		this.noOfHeadcounts = noOfHeadcounts;
	}
	public String getWorkType() {
		return workType;
	}
	public void setWorkType(String workType) {
		this.workType = workType;
	}
	public String getJobDescription() {
		return jobDescription;
	}
	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}
	public String getVisaStatus() {
		return visaStatus;
	}
	public void setVisaStatus(String visaStatus) {
		this.visaStatus = visaStatus;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getLanguages() {
		return languages;
	}
	public void setLanguages(String languages) {
		this.languages = languages;
	}
	public String getRequiredDocuments() {
		return requiredDocuments;
	}
	public void setRequiredDocuments(String requiredDocuments) {
		this.requiredDocuments = requiredDocuments;
	}
	public String getWorkLocation() {
		return workLocation;
	}
	public void setWorkLocation(String workLocation) {
		this.workLocation = workLocation;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getQualification() {
		return qualification;
	}
	public void setQualification(String qualification) {
		this.qualification = qualification;
	}
	public String getTurnaroundTime() {
		return turnaroundTime;
	}
	public void setTurnaroundTime(String turnaroundTime) {
		this.turnaroundTime = turnaroundTime;
	}
	public String getJobRatingSales() {
		return jobRatingSales;
	}
	public void setJobRatingSales(String jobRatingSales) {
		this.jobRatingSales = jobRatingSales;
	}
	public String getSecurityClearance() {
		return securityClearance;
	}
	public void setSecurityClearance(String securityClearance) {
		this.securityClearance = securityClearance;
	}
    
}
