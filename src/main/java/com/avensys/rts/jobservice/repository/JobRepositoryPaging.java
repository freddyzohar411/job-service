package com.avensys.rts.jobservice.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.avensys.rts.jobservice.entity.JobEntity;

@Repository
public interface JobRepositoryPaging extends PagingAndSortingRepository<JobEntity ,Integer> {

	@Query("select a from job a where a.isDeleted = ?1")
	List<JobEntity> findAllAndIsDeleted(boolean isDeleted, Pageable pageable);

}
