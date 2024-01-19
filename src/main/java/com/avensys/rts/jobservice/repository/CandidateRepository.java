package com.avensys.rts.jobservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avensys.rts.jobservice.entity.CandidateEntity;

public interface CandidateRepository extends JpaRepository<CandidateEntity, Long> {

}
