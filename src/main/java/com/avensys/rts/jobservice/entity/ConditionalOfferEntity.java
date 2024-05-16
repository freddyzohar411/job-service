package com.avensys.rts.jobservice.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "conditionalOffer")
@Table(name = "conditional_offer")
public class ConditionalOfferEntity extends BaseEntity {

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

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "candidate_id", referencedColumnName = "id", unique = false)
	private CandidateEntity candidate;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "job_id", referencedColumnName = "id", unique = false)
	private JobEntity jobEnity;
git
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "sales_user_id", referencedColumnName = "id", unique = false)
	private UserEntity seller;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "conditional_offer_submission_data", columnDefinition = "jsonb")
	private JsonNode conditionalOfferSubmissionData;

}
