package com.avensys.rts.jobservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.avensys.rts.jobservice.entity.JobStageEntity;

public interface JobStageRepository extends CrudRepository<JobStageEntity, Long> {

	public Boolean existsByName(String name);

	public Optional<JobStageEntity> findByName(String name);

	@Query(value = "SELECT stage FROM job_stage stage WHERE stage.isDeleted = ?1")
	List<JobStageEntity> findAllAndIsDeleted(boolean isDeleted);

}