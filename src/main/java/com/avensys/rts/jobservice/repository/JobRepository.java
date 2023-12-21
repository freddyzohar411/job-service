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

	public Optional<JobEntity> findByIdAndIsDeleted(Long id, boolean isDeleted);

	@Query("select a from job a where a.isDeleted = ?1")
	public List<JobEntity> findAllAndIsDeleted(boolean isDeleted, Pageable pageable);

	@Query(value = "SELECT a FROM job a WHERE a.createdBy = ?1 AND a.isDeleted = ?2 AND a.isActive = ?3")
	List<JobEntity> findAllByUserAndDeleted(Long createdBy, boolean isDeleted, boolean isActive);

	@Query(value = "SELECT a FROM job a WHERE a.createdBy = ?1 AND a.isDraft = ?2 AND a.isDeleted = ?3 AND a.isActive = ?4")
	Optional<JobEntity> findByUserAndDraftAndDeleted(Long userId, boolean draft, boolean deleted, boolean isActive);

}