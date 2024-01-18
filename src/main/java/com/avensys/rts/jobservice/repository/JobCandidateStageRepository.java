package com.avensys.rts.jobservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.avensys.rts.jobservice.entity.JobCandidateStageEntity;

public interface JobCandidateStageRepository extends JpaRepository<JobCandidateStageEntity, Long> {

	@Query(value = "SELECT * FROM job_candidate_stage WHERE job_id = ?1", nativeQuery = true)
	public List<JobCandidateStageEntity> findByJob(Long jobId);

	@Query(value = "SELECT * FROM job_candidate_stage WHERE candidate_id = ?1", nativeQuery = true)
	public List<JobCandidateStageEntity> findByCandidate(Long candidateId);

	@Query(value = "SELECT * FROM job_candidate_stage WHERE job_id = ?1 and job_stage_id = ?2 and candidate_id = ?3", nativeQuery = true)
	public Optional<JobCandidateStageEntity> findByJobAndStageAndCandidate(Long jobId, Long jobStageId,
			Long candidateId);

	@Query(value = "SELECT * FROM job_candidate_stage WHERE job_id = ?1 and candidate_id = ?2", nativeQuery = true)
	public List<JobCandidateStageEntity> findByJobAndCandidate(Long jobId, Long candidateId);

	@Query(value = "SELECT * FROM job_candidate_stage stage WHERE stage.isDeleted = ?1", nativeQuery = true)
	public List<JobCandidateStageEntity> findAllAndIsDeleted(boolean isDeleted);

	@Query("select a from job_candidate_stage a where a.isDeleted = ?1")
	public List<JobCandidateStageEntity> findAllAndIsDeleted(boolean isDeleted, Pageable pageable);

}