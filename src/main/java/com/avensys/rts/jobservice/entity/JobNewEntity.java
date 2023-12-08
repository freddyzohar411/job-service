package com.avensys.rts.jobservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobNewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

//    @Column(name = "account_id")
//    private Integer accountId;

    @Column(name = "account_name")
    private String accountName;

//    @Column(name = "contact_id")
//    private Integer contactId;

    @Column(name = "account_contact")
    private String accountContact;

    @Column(name = "sales_manager")
    private String salesManager;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "open_date", nullable = false)
    private LocalDate openDate;

    @Column(name = "close_date")
    private LocalDate closeDate;

    @Column(name = "client_job_id", length = 20)
    private String clientJobId;

    @Column(name = "job_type", length = 20, nullable = false)
    private String jobType;

    @Column(name = "duration_id")
    private Integer durationId;

    @Column(name = "primary_skills", length = 500, nullable = false)
    private String primarySkills;

    @Column(name = "secondary_skills", length = 500, nullable = false)
    private String secondarySkills;

    @Column(name = " no_of_headcount", nullable = false)
    private Integer noOfHeadcount;

    @Column(name = "work_type", length = 20, nullable = false)
    private String workType;

    @Column(name = "job_description", length = 2500, nullable = false)
    private String jobDescription;

    @Column(name = "visa_status", length = 20, nullable = false)
    private String visaStatus;

    @Column(name = "country_name", length = 20, nullable = false)
    private String countryName;

    @Column(name = "languages",length = 100)
    private String languages;

    @Column(name = "required_document", length = 50)
    private String requiredDocument;

    @Column(name = "work_location", length = 500)
    private String workLocation;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "qualification",length = 50)
    private String qualification;

    @Column(name = "security_clearance")
    private Integer securityClearance;

    @Column(name = "job_ratings",length = 10)
    private String jobRatings;

    @Column(name = "turn_around_time_day", nullable = false)
    private Integer turnAroundTimeDay;

    @Column(name = "turn_around_time_unit",length = 10, nullable = false)
    private String turnAroundTimeUnit;

    @Column(name = "is_screen_required")
    private boolean isScreenRequired;

    @Column(name = "salary_budget")
    private Integer salaryBudget;

    @Column(name = "salary_budget_range",length = 20, nullable = false)
    private String salaryBudgetRange;

    @Column(name = "currency",length = 10)
    private String currency;

    @Column(name = "budget_type",length = 20, nullable = false)
    private String budgetType;

    @Column(name = "exp_margin_min")
    private Integer expMarginMin;

    @Column(name = "exp_margin_max")
    private Integer expMarginMax;

    @Column(name = "exp_margin_currency",length = 5)
    private String expMarginCurrency;

    @Column(name = "salary_budget_sgd")
    private Integer salaryBudgetSgd;

    @Column(name = "exp_margin_sgd")
    private Integer expMarginSgd;

    @Column(name = "tech_screen_template")
    private Integer techScreenTemplate;

    @Column(name = "rec_scren_template")
    private Integer recScrenTemplate;

    @Column(name = "remarks",length = 1500)
    private String remarks;

    @Column(name = "is_post_job")
    private boolean isPostJob;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "is_hold")
    private boolean isHold;

    @Column(name = "job_status",length = 10)
    private String jobStatus;

//	@Column(name = "job_overview")
//	private Blob jobOverview;

    @Column (name = "created_by")
    private Integer createdBy;

    @CreationTimestamp
    @Column (name = "created_at")
    private LocalDateTime createdAt;

    @Column (name = "updated_by")
    private Integer updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
