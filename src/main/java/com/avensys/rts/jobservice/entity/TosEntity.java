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
@Entity(name = "tos")
@Table(name = "tos")
public class TosEntity extends BaseEntity{
	
	private static final long serialVersionUID = 4217358489248736598L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "form_id")
	private Long formId;

	@Column(name = "form_submission_id")
	private Long formSubmissionId;
	
	@Column(name = "status")
	private String Status;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "candidate_id", referencedColumnName = "id", unique = false)
	private CandidateEntity candidate;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "job_id", referencedColumnName = "id", unique = false)
	private JobEntity jobEnity;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "sales_user_id", referencedColumnName = "id", unique = false)
	private UserEntity seller;
	
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "tos_submission_data", columnDefinition = "jsonb")
	private JsonNode tosSubmissionData;

	
	
	

}
