package com.avensys.rts.jobservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.avensys.rts.jobservice.entity.JobCandidateStageEntity;

public interface JobCandidateStageRepository extends JpaRepository<JobCandidateStageEntity, Long> {

	@Query(value = "SELECT * FROM job_candidate_stage WHERE job_id = ?1", nativeQuery = true)
	public List<JobCandidateStageEntity> findByJob(Long jobId);

	@Query(value = "SELECT * FROM job_candidate_stage WHERE candidate_id = ?1", nativeQuery = true)
	public List<JobCandidateStageEntity> findByCandidate(Long candidateId);

	@Query(value = "SELECT * FROM job_candidate_stage WHERE job_id = ?1 and job_stage_id = (SELECT js.id from job_stage js where js.stage_order = ?2) and candidate_id = ?3", nativeQuery = true)
	public Optional<JobCandidateStageEntity> findByJobAndStageAndCandidate(Long jobId, Long jobStageId,
			Long candidateId);

	@Query(value = "SELECT * FROM job_candidate_stage WHERE job_id = ?1 and candidate_id = ?2", nativeQuery = true)
	public List<JobCandidateStageEntity> findByJobAndCandidate(Long jobId, Long candidateId);

	@Query(value = "SELECT * FROM job_candidate_stage stage WHERE stage.isDeleted = ?1", nativeQuery = true)
	public List<JobCandidateStageEntity> findAllAndIsDeleted(boolean isDeleted);

	@Query("select a from job_candidate_stage a where a.isDeleted = ?1")
	public List<JobCandidateStageEntity> findAllAndIsDeleted(boolean isDeleted, Pageable pageable);

	@Modifying
	@Query(value = "delete from job_candidate_stage  where job_id = ?1 and candidate_id = ?2", nativeQuery = true)
	public Integer deleteByJobAndCandidate(Long jobId, Long candidateId);

	@Query(value = "SELECT COUNT(jcs.*) " + "FROM job_candidate_stage jcs " + "JOIN job j ON jcs.job_id = j.id "
			+ "WHERE j.job_submission_data->>'jobStatus' = 'Active' "
			+ "AND jcs.job_stage_id = (SELECT js.id FROM job_stage js WHERE js.stage_order = ?1) AND jcs.status != 'SKIPPED'", nativeQuery = true)
	Long findActiveJobsByStageName(Long stageOrder);

	@Query(value = "SELECT COUNT(jcs.*) " + "FROM job_candidate_stage jcs " + "JOIN job j ON jcs.job_id = j.id "
			+ "WHERE j.job_submission_data->>'jobStatus' = 'Active' "
			+ "AND jcs.job_stage_id = (SELECT js.id FROM job_stage js WHERE js.stage_order = ?1)"
			+ "AND jcs.status = ?2", nativeQuery = true)
	Long findActiveJobsByStageNameAndStatus(Long stageOrder, String status);

	@Query(value = "SELECT COUNT(jcs.*) " + "FROM job_candidate_stage jcs " + "JOIN job j ON jcs.job_id = j.id "
			+ "WHERE j.job_submission_data->>'jobStatus' = 'Active' " + "AND jcs.status = ?1", nativeQuery = true)
	Long findActiveJobByStatus(String status);

	@Query(value = "SELECT COUNT(*) "
			+ "FROM ("
			+ "  SELECT jcs.job_id, jcs.candidate_id "
			+ "  FROM job_candidate_stage jcs "
			+ "  JOIN job j ON jcs.job_id = j.id "
			+ "  WHERE j.job_submission_data->>'jobStatus' = 'Active' "
			+ "  GROUP BY jcs.job_id, jcs.candidate_id "
			+ "  HAVING COUNT(*) = 1 "
			+ "  AND SUM(CASE WHEN jcs.job_stage_id = 1 AND jcs.status = 'COMPLETED' THEN 1 ELSE 0 END) = 1"
			+ ") AS matched_records", nativeQuery = true)
	Long countNoSubmissions();

}