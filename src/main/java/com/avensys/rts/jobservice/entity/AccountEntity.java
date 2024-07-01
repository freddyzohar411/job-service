package com.avensys.rts.jobservice.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Author: Koh He Xiang This is the entity class for the new account table in
 * the database that works with dynamic form
 */
@Entity(name = "account")
@Table(name = "account")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "name", length = 50, nullable = false)
	private String name;

	@Column(name = "mark_up")
	private String markUp;

	@Column(name = "msp")
	private String msp;

	@ManyToOne
	@JoinColumn(name = "parent_company", referencedColumnName = "id")
	private AccountEntity parentCompany;

	@Column(name = "is_deleted", columnDefinition = "boolean default false")
	private Boolean isDeleted;

	@Column(name = "is_draft")
	private Boolean isDraft = true;

	@Column(name = "is_active")
	private Boolean isActive = true;

	@Column(name = "account_number", length = 10)
	private String accountNumber;

	@Column(name = "created_by")
	private Integer createdBy;

	@CreationTimestamp
	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "form_id")
	private Integer formId;

	@Column(name = "form_submission_id")
	private Integer formSubmissionId;

	@Column(name = "commercial_form_id")
	private Integer commercialFormId;

	@Column(name = "commercial_form_submission_id")
	private Integer commercialFormSubmissionId;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "account_submission_data", columnDefinition = "jsonb")
	private JsonNode accountSubmissionData;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "commercial_submission_data", columnDefinition = "jsonb")
	private JsonNode commercialSubmissionData;

	// Added - 27102023
	@Column(name = "account_country")
	private String accountCountry;
}
