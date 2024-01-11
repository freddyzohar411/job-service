package com.avensys.rts.jobservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.avensys.rts.jobservice.entity.JobCandidateStageEntity;

public interface JobCandidateStageRepository extends JpaRepository<JobCandidateStageEntity, Long> {

	@Query(value = "SELECT * FROM job_recruiter_fod WHERE job_id = ?1", nativeQuery = true)
	public List<JobCandidateStageEntity> findByJob(Long jobId);

	@Query(value = "SELECT * FROM job_recruiter_fod WHERE candidate_id = ?1", nativeQuery = true)
	public List<JobCandidateStageEntity> findByCandidate(Long candidateId);

	@Query(value = "SELECT * FROM job_recruiter_fod WHERE job_id = ?1 and job_stage_id = ?2 and candidate_id = ?3", nativeQuery = true)
	public List<JobCandidateStageEntity> findByJobAndStageAndCandidate(Long candidateId);

}