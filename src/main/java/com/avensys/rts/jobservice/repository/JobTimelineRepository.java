package com.avensys.rts.jobservice.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.avensys.rts.jobservice.entity.JobTimelineEntity;

public interface JobTimelineRepository extends JpaRepository<JobTimelineEntity, Long>,
		JpaSpecificationExecutor<JobTimelineEntity>, CustomJobTimelineRepository {

	@Query(value = "SELECT * FROM job_timeline WHERE job_id = ?1 and candidate_id = ?2", nativeQuery = true)
	public Optional<JobTimelineEntity> findByJobAndCandidate(Long jobId, Long candidateId);

	@Query(value = "select js.name,count(jcs.job_stage_id) from job_candidate_stage jcs "
			+ "inner join job_stage js on jcs.job_stage_id = js.id  where jcs.job_id = ?1 "
			+ "and jcs.status = 'COMPLETED' and js.stage_order in (1,2,3,4,16,17) group by js.name,jcs.job_id,jcs.job_stage_id", nativeQuery = true)
	public List<Map<String, Long>> findJobTimelineCountAdmin(Long jobId);

	@Query(value = "select js.name,count(jcs.job_stage_id) from job_candidate_stage jcs "
			+ "inner join job_stage js on jcs.job_stage_id = js.id  where jcs.job_id = ?1 "
			+ "and jcs.status = 'COMPLETED' and js.stage_order in (1,2,3,4,16,17) and jcs.created_by IN (?2) group by js.name,jcs.job_id,jcs.job_stage_id", nativeQuery = true)
	public List<Map<String, Long>> findJobTimelineCount(Long jobId, List<Long> userIds);

	@Query(value = "select 'Scheduled' as name,count(jcs.job_stage_id) from job_candidate_stage jcs inner join job_stage js on jcs.job_stage_id = js.id where jcs.job_id = ?1 and js.stage_order in(10,11,12)", nativeQuery = true)
	public Map<String, Long> findInterviewScheduledCount(Long jobId);

	@Query(value = "select 'Completed' as name,count(jcs.job_stage_id) from job_candidate_stage jcs inner join job_stage js on jcs.job_stage_id = js.id where jcs.job_id = ?1 and js.stage_order in(10,11,12) and jcs.status in ('COMPLETED', 'REJECTED')", nativeQuery = true)
	public Map<String, Long> findInterviewHappenedCount(Long jobId);

	@Query(value = "select 'Cancelled/Backout' as name,count(jcs.job_stage_id) from job_candidate_stage jcs inner join job_stage js on jcs.job_stage_id = js.id where jcs.job_id = ?1 and js.stage_order in(10,11,12) and jcs.status = 'WITHDRAWN'", nativeQuery = true)
	public Map<String, Long> findInterviewCancelledCount(Long jobId);

	@Query(value = "select 'Pending Feedback' as name,count(jcs.job_stage_id) from job_candidate_stage jcs inner join job_stage js on jcs.job_stage_id = js.id where jcs.job_id = ?1 and js.stage_order = 13", nativeQuery = true)
	public Map<String, Long> findInterviewFeedbackPendingCount(Long jobId);

	@Modifying
	@Query(value = "delete from job_timeline  where job_id = ?1 and candidate_id = ?2", nativeQuery = true)
	public Integer deleteByJobAndCandidate(Long jobId, Long candidateId);

	@Query(value = "select 'Scheduled' as name,count(jcs.job_stage_id) from job_candidate_stage jcs inner join job_stage js on jcs.job_stage_id = js.id where js.stage_order in(10,11,12)", nativeQuery = true)
	public Map<String, Long> findInterviewScheduledCountAll();

	@Query(value = "select 'Completed' as name,count(jcs.job_stage_id) from job_candidate_stage jcs inner join job_stage js on jcs.job_stage_id = js.id where js.stage_order in(10,11,12) and jcs.status in ('COMPLETED', 'REJECTED')", nativeQuery = true)
	public Map<String, Long> findInterviewHappenedCountAll();

}
