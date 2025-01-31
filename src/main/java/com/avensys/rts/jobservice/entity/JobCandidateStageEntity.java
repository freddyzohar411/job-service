package com.avensys.rts.jobservice.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "job_candidate_stage")
@Table(name = "job_candidate_stage")
public class JobCandidateStageEntity extends BaseEntity {

	private static final long serialVersionUID = 4217358489248736598L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "job_id", referencedColumnName = "id")
	private JobEntity job;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "job_stage_id", referencedColumnName = "id")
	private JobStageEntity jobStage;

	@Column(name = "status")
	private String status;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "candidate_id", referencedColumnName = "id")
	private CandidateEntity candidate;

	@Column(name = "form_id")
	private Long formId;

	@Column(name = "form_submission_id")
	private Long formSubmissionId;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "submission_data", columnDefinition = "jsonb")
	private JsonNode submissionData;

	@Column(name = "action_form_submission_id")
	private Long actionFormSubmissionId;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "action_submission_data", columnDefinition = "jsonb")
	private JsonNode actionSubmissionData;


}
