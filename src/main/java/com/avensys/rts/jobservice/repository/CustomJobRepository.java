package com.avensys.rts.jobservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.logging.Filter;

import com.avensys.rts.jobservice.payload.FilterDTO;
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
	Page<JobEntity> findAllByOrderByStringWithUserIds(List<Long> userIds, Boolean isDeleted, Boolean isActive,
			Pageable pageable, String jobType, Long userId, List<FilterDTO> filters);

	Page<JobEntity> findAllByOrderByNumericWithUserIds(List<Long> userIds, Boolean isDeleted, Boolean isActive,
			Pageable pageable, String jobType, Long userId, List<FilterDTO> filters);

	Page<JobEntity> findAllByOrderByAndSearchStringWithUserIds(List<Long> userIds, Boolean isDeleted, Boolean isActive,
			Pageable pageable, List<String> searchFields, String searchTerm, String jobType, Long userId, List<FilterDTO> filters);

	Page<JobEntity> findAllByOrderByAndSearchNumericWithUserIds(List<Long> userIds, Boolean isDeleted, Boolean isActive,
			Pageable pageable, List<String> searchFields, String searchTerm, String jobType, Long userId, List<FilterDTO> filters);

	void insertVector(Long jobId, String columnName, List<Float> vector);

	void updateVector(Long jobId, String columnName, List<Float> vector);

	Optional<List<Float>> getEmbeddingsById(Long jobId, String columnName);

	List<JobEntity> findAllByEmbeddingIsNull();
}
