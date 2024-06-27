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

	// Add filter only in list of user Id - COUNTS
	@Query(value = "select count(id) from job where id not in (select distinct(fod.job_id) from job_recruiter_fod fod) and is_active = true and is_deleted = false and (created_by IN :userIds or CAST(NULLIF(job_submission_data->>'accountOwnerId', '') as INTEGER) in :userIds) and CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Active'", nativeQuery = true)
	Optional<Integer> getNewJobsCount(List<Long> userIds);

	@Query(value = "select count(id) from job where id not in (select distinct(fod.job_id) from job_recruiter_fod fod) and is_active = true and is_deleted = false and CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Active'", nativeQuery = true)
	Optional<Integer> getNewJobsCountAll();

	@Query(value = "select count(id) from job where is_active = true and is_deleted = false and (created_by IN :userIds or CAST(NULLIF(job_submission_data->>'accountOwnerId', '') as INTEGER) in :userIds or id in (select distinct(fod.job_id) from job_recruiter_fod fod where (fod.recruiter_id IN :userIds or fod.sales_id IN :userIds))) and CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Active'", nativeQuery = true)
	Optional<Integer> getActiveJobsCount(List<Long> userIds);

	@Query(value = "select count(id) from job where is_active = true and is_deleted = false and CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Active'", nativeQuery = true)
	Optional<Integer> getActiveJobsCountAll();

	@Query(value = "select count(id) from job where is_active = true and is_deleted = false and (created_by IN :userIds or CAST(NULLIF(job_submission_data->>'accountOwnerId', '') as INTEGER) in :userIds or id in (select distinct(fod.job_id) from job_recruiter_fod fod where (fod.recruiter_id IN :userIds or fod.sales_id IN :userIds))) and CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Inactive'", nativeQuery = true)
	Optional<Integer> getInactiveJobsCount(List<Long> userIds);

	@Query(value = "select count(id) from job where is_active = true and is_deleted = false and CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Inactive'", nativeQuery = true)
	Optional<Integer> getInactiveJobsCountAll();

	@Query(value = "select count(id) from job where is_active = true and is_deleted = false and (created_by IN :userIds or CAST(NULLIF(job_submission_data->>'accountOwnerId', '') as INTEGER) in :userIds or id in (select distinct(fod.job_id) from job_recruiter_fod fod where (fod.recruiter_id IN :userIds or fod.sales_id IN :userIds))) and CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Closed'", nativeQuery = true)
	Optional<Integer> getClosedJobsCount(List<Long> userIds);

	@Query(value = "select count(id) from job where is_active = true and is_deleted = false and CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Closed'", nativeQuery = true)
	Optional<Integer> getClosedJobsCountAll();

	@Query(value = "select count(id) from job where is_active = true and is_deleted = false and (created_by IN :userIds or CAST(NULLIF(job_submission_data->>'accountOwnerId', '') as INTEGER) in :userIds) and id in (select distinct(fod.job_id) from job_recruiter_fod fod where (fod.recruiter_id IN :userIds or fod.sales_id IN :userIds)) and CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Active'", nativeQuery = true)
	Optional<Integer> getAssignedJobsCount(List<Long> userIds);

	@Query(value = "select count(id) from job where is_active = true and is_deleted = false and  id in (select distinct(fod.job_id) from job_recruiter_fod fod) and CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Active'", nativeQuery = true)
	Optional<Integer> getAssignedJobsCountAll();

	@Query(value = "select count(id) from job where is_active = true and is_deleted = false and (created_by IN :userIds or CAST(NULLIF(job_submission_data->>'accountOwnerId', '') as INTEGER) in :userIds) and id in (select distinct(fod.job_id) from job_recruiter_fod fod where (fod.recruiter_id IN :userIds or fod.sales_id IN :userIds) and fod.created_at >= current_date) and CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Active'", nativeQuery = true)
	Optional<Integer> getFODCount(List<Long> userIds);

	@Query(value = "select count(id) from job where is_active = true and is_deleted = false and id in (select distinct(fod.job_id) from job_recruiter_fod fod where fod.created_at >= current_date) and CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Active'", nativeQuery = true)
	Optional<Integer> getFODCountAll();

	@Query(value = "select count(id) from job where is_active = true and is_deleted = false and (created_by IN :userIds or CAST(NULLIF(job_submission_data->>'accountOwnerId', '') as INTEGER) in :userIds or id in (select distinct(fod.job_id) from job_recruiter_fod fod where (fod.recruiter_id IN :userIds or fod.sales_id IN :userIds)))", nativeQuery = true)
	Optional<Integer> getAllJobsCount(List<Long> userIds);

	@Query(value = "select count(id) from job where is_active = true and is_deleted = false", nativeQuery = true)
	Optional<Integer> getAllJobsCountAll();

}