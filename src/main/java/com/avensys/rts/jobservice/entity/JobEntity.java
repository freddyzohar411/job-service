package com.avensys.rts.jobservice.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "job")
@Table(name = "job")
public class JobEntity extends BaseEntity {

	private static final long serialVersionUID = 4217358489248736598L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "title")
	private String title;

	@Column(name = "form_id")
	private Long formId;

	@Column(name = "form_submission_id")
	private Long formSubmissionId;

	@Column(name = "is_draft")
	private Boolean isDraft = false;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "job_submission_data", columnDefinition = "jsonb")
	private JsonNode jobSubmissionData;

	@Column(name = "is_email_sent")
	private Boolean isEmailSent = false;

	@Transient
	@Column(name = "createdByName")
	private String createdByName;

	@Transient
	@Column(name = "updatedByName")
	private String updatedByName;

}
