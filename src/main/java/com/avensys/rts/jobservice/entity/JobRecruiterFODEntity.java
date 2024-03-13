package com.avensys.rts.jobservice.entity;

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
@Entity
@Table(name = "job_recruiter_fod")
public class JobRecruiterFODEntity extends BaseEntity {

	private static final long serialVersionUID = 4217358489248736598L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "job_id", referencedColumnName = "id")
	private JobEntity job;

	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "recruiter_id", referencedColumnName = "id", unique = false)
	private UserEntity recruiter;

	@OneToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "sales_id", referencedColumnName = "id", unique = false)
	private UserEntity seller;

	@Column(name = "status")
	private String status;
}
