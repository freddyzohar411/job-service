package com.avensys.rts.jobservice.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class JobOpeningInformation {

	@NotEmpty (message = "jobTitle cannot be empty.")
	@Length (max =50)
	private String jobTitle;
	
	@NotEmpty(message = "dateOpen cannot be null.")
	private String dateOpen;
	
	private String targetClosingDate;
	
	private String clientJobID;
	
    @NotEmpty (message = "jobType cannot be empty .")
    @Length (max =20)
    private String jobType;
    
    private String duration;
    
    @NotEmpty (message = "primarySkills cannot be empty.")
    @Length (max =500)
    private String primarySkills;
    
    @NotEmpty (message = "secondarySkills cannot be empty.")
    @Length (max =500)
    private String secondarySkills;
    
    @NotEmpty (message = " noOfHeadcounts not be null .")
    private String noOfHeadcounts;
    
    @NotEmpty (message = " workType cannot be empty.")
    @Length (max = 20)
    private String workType;
    
    @NotEmpty (message = "jobDescription cannot be empty.")
    @Length (max =2500)
    private String jobDescription;
    
    @NotEmpty (message = " visaStatus cannot be empty.")
    @Length (max =20)
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
    
}
