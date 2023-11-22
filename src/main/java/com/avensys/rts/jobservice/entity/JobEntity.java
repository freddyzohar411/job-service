package com.avensys.rts.jobservice.entity;

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
@Entity(name = "job")
@Table(name = "job")
public class JobEntity extends BaseEntity {

	private static final long serialVersionUID = 4217358489248736598L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "title", length = 5)
	private String title;

	@Column(name = "entity_id")
	private Long entityId;

	@Column(name = "entity_type", length = 30)
	private String entityType;

	@Column(name = "form_id")
	private Long formId;

	@Column(name = "form_submission_id")
	private Long formSubmissionId;

}
