package com.avensys.jobservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avensys.jobservice.entity.JobEntity;

public interface JobRepository extends JpaRepository<JobEntity, Integer>{

}
