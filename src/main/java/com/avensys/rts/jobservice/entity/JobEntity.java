package com.avensys.rts.jobservice.entity;

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


}
