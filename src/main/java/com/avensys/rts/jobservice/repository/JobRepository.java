package com.avensys.rts.jobservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.avensys.rts.jobservice.entity.JobEntity;

public interface JobRepository
		extends JpaRepository<JobEntity, Long>, JpaSpecificationExecutor<JobEntity>, CustomJobRepository {

	public Boolean existsByTitle(String title);

	public Optional<JobEntity> findByTitle(String title);

	// new
	public Optional<JobEntity> findById(Long id);

	public Optional<JobEntity> findByIdAndIsDeleted(Long id, boolean isDeleted);

	@Query("select a from job a where a.isDeleted = ?1")
	public List<JobEntity> findAllAndIsDeleted(boolean isDeleted, Pageable pageable);

	@Query(value = "SELECT a FROM job a WHERE a.createdBy = ?1 AND a.isDeleted = ?2 AND a.isActive = ?3")
	List<JobEntity> findAllByUserAndDeleted(Long createdBy, boolean isDeleted, boolean isActive);

	@Query(value = "SELECT a FROM job a WHERE a.createdBy IN (?1) AND a.isDeleted = ?2 AND a.isActive = ?3")
	List<JobEntity> findAllByUserIdsAndDeleted(List<Long> createdByList, boolean isDeleted, boolean isActive);

	@Query(value = "SELECT a FROM job a WHERE a.createdBy = ?1 AND a.isDraft = ?2 AND a.isDeleted = ?3 AND a.isActive = ?4")
	Optional<JobEntity> findByUserAndDraftAndDeleted(Long userId, boolean draft, boolean deleted, boolean isActive);

	@Query(value = "SELECT a FROM job a WHERE a.id = ?1 AND a.isDeleted = ?2 AND a.isActive = ?3")
	Optional<JobEntity> findByIdAndDeleted(Long id, boolean isDeleted, boolean isActive);

	@Query(value = "SELECT a FROM job a WHERE a.isDraft= ?1 AND a.isDeleted = ?2 AND a.isActive = ?3")
	List<JobEntity> findAllByIsDraftAndIsDeletedAndIsActive(boolean isDraft, boolean isDeleted, boolean isActive);

	@Query(value = "SELECT j FROM job j WHERE j.id IN (?1) AND j.isDraft = ?2 AND j.isDeleted = ?3 AND j.isActive = ?4")
	List<JobEntity> findAllByIdsAndDraftAndDeleted(List<Long> jobIds, boolean draft, boolean isDeleted,
			boolean isActive);

	@Query(value = "select STRING_AGG(CONCAT(u.first_name,' ',u.last_name), ', ') from users u inner join job_recruiter_fod jrf on jrf.recruiter_id = u.id where jrf.job_id = ?1", nativeQuery = true)
	String getRecruiters(Long jobId);

}