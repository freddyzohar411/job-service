package com.avensys.jobservice.entity;

import java.sql.Blob;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job")
public class JobEntity {

	@SequenceGenerator(name = "job_sequence", sequenceName = "job_sequence", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_sequence")
	private Integer id;
	
	@Column(name = "account_id")
	private Integer accountId;
	
	@Column (name = "contact_id")
	private Integer contactId;
	
	@Column(name = "title")
	private String title;
	
	@CreationTimestamp
	@Column (name = "open_date")
	private LocalDateTime openDate;
	
	@Column (name = "close_date")
	private LocalDateTime closeDate;
	
	@Column (name = "client_job_id")
	private String clientJobId;
	
	@Column (name = "job_type")
	private String jobType;
	
	@Column (name = "duration_id")
	private Integer durationId;
	
	@Column (name = "primary_skills")
	private String primarySkills;
	
	@Column (name = "secondary_skills")
	private String secondarySkills;
	
	@Column (name = " no_of_headcount")
	private Integer noOfHeadcount;
	
	@Column (name = "work_type")
	private String workType;
	
	@Column (name = "job_description")
	private String jobDescription;
	
	@Column (name = "visa_status")
	private String visaStatus;
	
	@Column (name = "country_name")
	private String countryName;
	
	@Column(name = "languages")
	private String languages;
	
	@Column (name = "required_document")
	private String requiredDocument;
	
	@Column (name = "work_location")
	private String workLocation;
	
	@Column(name = "priotity")
	private Integer priotity;
	
	@Column(name = "qualitifcation")
	private String qualitifcation;
	
	@Column (name = "security_clearance")
	private Integer securityClearance;
	
	@Column (name = "job_ratings")
	private String jobRatings;
	
	@Column (name = "turn_around_time_day")
	private String turnAroundTimeDay;
	
	@Column (name = "turn_around_time_unit")
	private String turnAroundTimeUnit;
	
	@Column (name = "is_screen_required")
	private boolean isScreenRequired;
	
	@Column (name = "salary_budget")
	private Integer salaryBudget;
	
	@Column (name = "salary_budget_range")
	private String salaryBudgetRange;
	
	@Column(name = "currency")
	private String currency;
	
	@Column (name = "budget_type")
	private String budgetType;
	
	@Column (name = "exp_margin_min")
	private Integer expMarginMin;
	
	@Column (name = "exp_margin_max")
	private Integer expMarginMax;
	
	@Column (name = "exp_margin_currency")
	private String expMarginCurrency;
	
	@Column (name = "exp_margin_sgd")
	private Integer expMarginSgd;
	
	@Column (name = "tech_screen_template")
	private Integer techScreenTemplate;
	
	@Column (name = "rec_scren_template")
	private Integer recScrenTemplate;
	
	@Column(name = "remarks")
	private String remarks;
	
	@Column (name = "is_post_job")
	private boolean isPostJob;
	
	@Column (name = "is_deleted")
	private boolean isDeleted;
	
	@Column (name = "is_hold")
	private boolean isHold;
	
	@Column (name = "job_status")
	private String jobStatus;
	
	@Column (name = "job_overview")
	private Blob jobOverview;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public Integer getContactId() {
		return contactId;
	}

	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public LocalDateTime getOpenDate() {
		return openDate;
	}

	public void setOpenDate(LocalDateTime openDate) {
		this.openDate = openDate;
	}

	public LocalDateTime getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(LocalDateTime closeDate) {
		this.closeDate = closeDate;
	}

	public String getClientJobId() {
		return clientJobId;
	}

	public void setClientJobId(String clientJobId) {
		this.clientJobId = clientJobId;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public Integer getDurationId() {
		return durationId;
	}

	public void setDurationId(Integer durationId) {
		this.durationId = durationId;
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

	public Integer getNoOfHeadcount() {
		return noOfHeadcount;
	}

	public void setNoOfHeadcount(Integer noOfHeadcount) {
		this.noOfHeadcount = noOfHeadcount;
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

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getLanguages() {
		return languages;
	}

	public void setLanguages(String languages) {
		this.languages = languages;
	}

	public String getRequiredDocument() {
		return requiredDocument;
	}

	public void setRequiredDocument(String requiredDocument) {
		this.requiredDocument = requiredDocument;
	}

	public String getWorkLocation() {
		return workLocation;
	}

	public void setWorkLocation(String workLocation) {
		this.workLocation = workLocation;
	}

	public Integer getPriotity() {
		return priotity;
	}

	public void setPriotity(Integer priotity) {
		this.priotity = priotity;
	}

	public String getQualitifcation() {
		return qualitifcation;
	}

	public void setQualitifcation(String qualitifcation) {
		this.qualitifcation = qualitifcation;
	}

	public Integer getSecurityClearance() {
		return securityClearance;
	}

	public void setSecurityClearance(Integer securityClearance) {
		this.securityClearance = securityClearance;
	}

	public String getJobRatings() {
		return jobRatings;
	}

	public void setJobRatings(String jobRatings) {
		this.jobRatings = jobRatings;
	}

	public String getTurnAroundTimeDay() {
		return turnAroundTimeDay;
	}

	public void setTurnAroundTimeDay(String turnAroundTimeDay) {
		this.turnAroundTimeDay = turnAroundTimeDay;
	}

	public String getTurnAroundTimeUnit() {
		return turnAroundTimeUnit;
	}

	public void setTurnAroundTimeUnit(String turnAroundTimeUnit) {
		this.turnAroundTimeUnit = turnAroundTimeUnit;
	}

	public boolean isScreenRequired() {
		return isScreenRequired;
	}

	public void setScreenRequired(boolean isScreenRequired) {
		this.isScreenRequired = isScreenRequired;
	}

	public Integer getSalaryBudget() {
		return salaryBudget;
	}

	public void setSalaryBudget(Integer salaryBudget) {
		this.salaryBudget = salaryBudget;
	}

	public String getSalaryBudgetRange() {
		return salaryBudgetRange;
	}

	public void setSalaryBudgetRange(String salaryBudgetRange) {
		this.salaryBudgetRange = salaryBudgetRange;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getBudgetType() {
		return budgetType;
	}

	public void setBudgetType(String budgetType) {
		this.budgetType = budgetType;
	}

	public Integer getExpMarginMin() {
		return expMarginMin;
	}

	public void setExpMarginMin(Integer expMarginMin) {
		this.expMarginMin = expMarginMin;
	}

	public Integer getExpMarginMax() {
		return expMarginMax;
	}

	public void setExpMarginMax(Integer expMarginMax) {
		this.expMarginMax = expMarginMax;
	}

	public String getExpMarginCurrency() {
		return expMarginCurrency;
	}

	public void setExpMarginCurrency(String expMarginCurrency) {
		this.expMarginCurrency = expMarginCurrency;
	}

	public Integer getExpMarginSgd() {
		return expMarginSgd;
	}

	public void setExpMarginSgd(Integer expMarginSgd) {
		this.expMarginSgd = expMarginSgd;
	}

	public Integer getTechScreenTemplate() {
		return techScreenTemplate;
	}

	public void setTechScreenTemplate(Integer techScreenTemplate) {
		this.techScreenTemplate = techScreenTemplate;
	}

	public Integer getRecScrenTemplate() {
		return recScrenTemplate;
	}

	public void setRecScrenTemplate(Integer recScrenTemplate) {
		this.recScrenTemplate = recScrenTemplate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public boolean isPostJob() {
		return isPostJob;
	}

	public void setPostJob(boolean isPostJob) {
		this.isPostJob = isPostJob;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public boolean isHold() {
		return isHold;
	}

	public void setHold(boolean isHold) {
		this.isHold = isHold;
	}

	public String getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	public Blob getJobOverview() {
		return jobOverview;
	}

	public void setJobOverview(Blob jobOverview) {
		this.jobOverview = jobOverview;
	}


}
