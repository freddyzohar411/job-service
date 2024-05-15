package com.avensys.rts.jobservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.avensys.rts.jobservice.entity.JobRecruiterFODEntity;
import com.avensys.rts.jobservice.entity.TosEntity;

public interface TosRepository extends JpaRepository<TosEntity, Long> {

	@Query(value = "SELECT * FROM tos WHERE job_id = ?1", nativeQuery = true)
	public Optional<TosEntity> findByJob(Long jobId);

	@Query(value = "SELECT * FROM tos WHERE candidate_id = ?1", nativeQuery = true)
	public Optional<TosEntity> findByCandidate(Long candidateId);

	@Query(value = "SELECT * FROM tos WHERE sales_user_id = ?1", nativeQuery = true)
	public Optional<TosEntity> findBySalesUserId(Long candidateId);

	public Optional<TosEntity> findById(Long id);

}
