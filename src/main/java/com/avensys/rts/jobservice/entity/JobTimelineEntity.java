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
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "job_timeline")
@Table(name = "job_timeline")
public class JobTimelineEntity extends BaseEntity {

	private static final long serialVersionUID = 4217358489248736598L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "job_id", referencedColumnName = "id")
	private JobEntity job;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "candidate_id", referencedColumnName = "id")
	private CandidateEntity candidate;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "timeline", columnDefinition = "jsonb")
	private JsonNode timeline;

	@Column(name = "stepName")
	private String stepName;

	@Column(name = "subStepName")
	private String subStepName;

	@Transient
	private String createdByName;

	@Transient
	private String updatedByName;

}
