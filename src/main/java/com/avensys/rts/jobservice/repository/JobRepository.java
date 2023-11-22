package com.avensys.rts.jobservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.avensys.rts.jobservice.entity.JobEntity;

public interface JobRepository extends JpaRepository<JobEntity, Long>, JpaSpecificationExecutor<JobEntity> {

	public Boolean existsByTitle(String title);

	public Optional<JobEntity> findByTitle(String title);

	public Optional<JobEntity> findByIdAndIsDeleted(Long id, boolean isDeleted);

	public List<JobEntity> findByEntityTypeAndEntityId(String entityType, Integer entityId);

	@Query("select a from job a where a.isDeleted = ?1")
	List<JobEntity> findAllAndIsDeleted(boolean isDeleted, Pageable pageable);

}