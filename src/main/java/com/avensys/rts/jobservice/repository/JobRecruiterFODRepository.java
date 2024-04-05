package com.avensys.rts.jobservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.avensys.rts.jobservice.entity.JobRecruiterFODEntity;

public interface JobRecruiterFODRepository extends JpaRepository<JobRecruiterFODEntity, Long> {

	@Query(value = "SELECT * FROM job_recruiter_fod WHERE job_id = ?1", nativeQuery = true)
	public Optional<JobRecruiterFODEntity> findByJob(Long jobId);

	@Query(value = "SELECT * FROM job_recruiter_fod WHERE recruiter_id = ?1", nativeQuery = true)
	public Optional<JobRecruiterFODEntity> findByRecruiter(Long recruiterId);

	@Query(value = "SELECT * FROM job_recruiter_fod WHERE seller_id = ?1", nativeQuery = true)
	public Optional<JobRecruiterFODEntity> findBySeller(Long sellerId);

	@Query(value = "SELECT * FROM job_recruiter_fod WHERE job_id = ?1 AND recruiter_id = ?2", nativeQuery = true)
	public Optional<JobRecruiterFODEntity> findByJobAndRecruiter(Long jobId, Long recruiterId);

	public void deleteByJobId(Long jobId);

	public List<JobRecruiterFODEntity> getAllByJobId(Long jobId);

}