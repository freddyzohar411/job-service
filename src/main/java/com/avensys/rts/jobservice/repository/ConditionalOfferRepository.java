package com.avensys.rts.jobservice.repository;

import com.avensys.rts.jobservice.entity.ConditionalOfferEntity;
import com.avensys.rts.jobservice.entity.TosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ConditionalOfferRepository extends JpaRepository<ConditionalOfferEntity, Long> {
	@Query(value = "SELECT * FROM conditional_offer WHERE job_id = ?1 AND candidate_id = ?2", nativeQuery = true)
	public Optional<ConditionalOfferEntity> findByJobAndCandidate(Long jobId, Long candidateId);

	@Query(value = "SELECT * FROM conditional_offer WHERE job_id = ?1 AND candidate_id = ?2 AND status = ?3", nativeQuery = true)
	public Optional<ConditionalOfferEntity> findByJobAndCandidateAndStatus(Long jobId, Long candidateId, String status);

}
