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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "candidate")
@Table(name = "candidate")
public class CandidateEntity extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1510395795076592541L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "firstName", length = 50, nullable = false)
	private String firstName;

	@Column(name = "lastName", length = 50, nullable = false)
	private String lastName;

	@Column(name = "is_draft")
	private boolean isDraft = true;

	@Column(name = "form_id")
	private Integer formId;

	@Column(name = "form_submission_id")
	private Integer formSubmissionId;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "candidate_submission_data")
	private JsonNode candidateSubmissionData;

	@Column(name = "created_by_user_groups_id", columnDefinition = "TEXT")
	private String createdByUserGroupsId;

}
