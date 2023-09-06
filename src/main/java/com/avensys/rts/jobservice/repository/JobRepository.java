package com.avensys.rts.jobservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avensys.rts.jobservice.entity.JobEntity;

public interface JobRepository extends JpaRepository<JobEntity, Integer> {
      Optional<JobEntity> findByIdAndIsDeleted(int id, boolean isDeleted);

}
