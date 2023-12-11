package com.avensys.rts.jobservice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.avensys.rts.jobservice.entity.JobEntity;

public interface CustomJobRepository {
	Page<JobEntity> findAllByOrderBy(Long userId, Boolean isDeleted, Boolean isDraft, Boolean isActive,
			Pageable pageable);

	Page<JobEntity> findAllByOrderByString(Long userId, Boolean isDeleted, Boolean isDraft, Boolean isActive,
			Pageable pageable);

	Page<JobEntity> findAllByOrderByNumeric(Long userId, Boolean isDeleted, Boolean isDraft, Boolean isActive,
			Pageable pageable);

	Page<JobEntity> findAllByOrderByAndSearchString(Long userId, Boolean isDeleted, Boolean isDraft, Boolean isActive,
			Pageable pageable, List<String> searchFields, String searchTerm);

	Page<JobEntity> findAllByOrderByAndSearchNumeric(Long userId, Boolean isDeleted, Boolean isDraft, Boolean isActive,
			Pageable pageable, List<String> searchFields, String searchTerm);

	List<JobEntity> getAllAccountsNameWithSearch(String query, Long userId, Boolean isDeleted, Boolean isDraft);

	void updateDocumentEntityId(Long tempId, Long originalId, Long userId, String entityType);

	// Check only user id
	Page<JobEntity>findAllByOrderByStringWithUserIds(List<Long> userIds, Boolean
			isDeleted,Boolean isActive,Pageable pageable);

	Page<JobEntity>findAllByOrderByNumericWithUserIds(List<Long> userIds, Boolean
			isDeleted,Boolean isActive,Pageable pageable);

	Page<JobEntity>findAllByOrderByAndSearchStringWithUserIds(List<Long> userIds,
			Boolean isDeleted,Boolean isActive, Pageable pageable,List<String>
			searchFields, String searchTerm);

	Page<JobEntity>findAllByOrderByAndSearchNumericWithUserIds(List<Long> userIds,
			Boolean isDeleted, Boolean isActive,Pageable pageable,List<String>
			searchFields, String searchTerm);
}
