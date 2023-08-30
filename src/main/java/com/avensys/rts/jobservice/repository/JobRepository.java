package com.avensys.rts.jobservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avensys.rts.jobservice.entity.JobEntity;

public interface JobRepository extends JpaRepository<JobEntity, Integer>{

}
