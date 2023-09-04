package com.avensys.rts.jobservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.avensys.rts.jobservice.entity.JobEntity;

public interface JobRepository extends JpaRepository<JobEntity, Integer> {
	  @Query(value = "SELECT a FROM job a WHERE a.id = ?1 AND a.isDeleted = ?2")
      Optional<JobEntity> findByIdAndDeleted(int id, boolean isDeleted);

}
