package com.avensys.rts.jobservice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.avensys.rts.jobservice.entity.JobTimelineEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class CustomJobTimelineRepositoryImpl implements CustomJobTimelineRepository {

	@PersistenceContext
	private EntityManager entityManager;

	// With user ids
	@Override
	public Page<JobTimelineEntity> findAllByOrderByStringWithUserIds(List<Long> userIds, Boolean isDeleted,
			Boolean isActive, Pageable pageable, Long userId, Long jobId) {

		// Determine if sortBy is a regular column or a JSONB column
		String sortBy = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		String orderByClause = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			orderByClause = String.format("(%s->>'%s')", jsonColumnName, jsonKey);
		}

		// Extract sort direction from pageable
		String sortDirection = pageable.getSort().isSorted()
				? pageable.getSort().get().findFirst().get().getDirection().name()
				: "ASC";

		// Build the complete query string with user filter and excluding NULLs
		String queryString = String.format(
				"SELECT * FROM job_timeline WHERE is_deleted = :isDeleted AND is_active = :isActive AND created_by IN (:userIds) AND job_id = :jobId ORDER BY %s %s NULLS LAST",
				orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobTimelineEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		query.setParameter("userIds", userIds);
		query.setParameter("jobId", jobId);
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobTimelineEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = "SELECT COUNT(*) FROM job_timeline WHERE is_deleted = :isDeleted AND is_active = :isActive AND created_by IN (:userIds) AND job_id = :jobId";

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
		countQuery.setParameter("userIds", userIds);
		countQuery.setParameter("jobId", jobId);
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<JobTimelineEntity> findAllByOrderByNumericWithUserIds(List<Long> userIds, Boolean isDeleted,
			Boolean isActive, Pageable pageable, Long userId, Long jobId) {
		// Determine if sortBy is a regular column or a JSONB column
		String sortBy = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		String orderByClause = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			orderByClause = String.format("CAST(NULLIF(%s->>'%s', '') AS INTEGER)", jsonColumnName, jsonKey);
		}

		// Extract sort direction from pageable
		String sortDirection = pageable.getSort().isSorted()
				? pageable.getSort().get().findFirst().get().getDirection().name()
				: "ASC";

		// Build the complete query string with user filter and excluding NULLs
		String queryString = String.format(
				"SELECT * FROM job_timeline WHERE is_deleted = :isDeleted AND is_active = :isActive AND created_by IN (:userIds) AND job_id = :jobId ORDER BY %s %s NULLS LAST",
				orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobTimelineEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		query.setParameter("userIds", userIds);
		query.setParameter("jobId", jobId);
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobTimelineEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = "SELECT COUNT(*) FROM job_timeline WHERE is_deleted = :isDeleted AND is_active = :isActive AND created_by IN (:userIds) AND job_id = :jobId";

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
		countQuery.setParameter("userIds", userIds);
		countQuery.setParameter("jobId", jobId);
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<JobTimelineEntity> findAllByOrderByAndSearchStringWithUserIds(List<Long> userIds, Boolean isDeleted,
			Boolean isActive, Pageable pageable, List<String> searchFields, String searchTerm, Long userId,
			Long jobId) {
		// Determine if sortBy is a regular column or a JSONB column
		String sortBy = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "job_timeline.updated_at";
		String orderByClause = sortBy;

		// Extract sort direction from pageable
		String sortDirection = pageable.getSort().isSorted()
				? pageable.getSort().get().findFirst().get().getDirection().name()
				: "ASC";

		// Build the dynamic search conditions based on searchFields
		StringBuilder searchConditions = new StringBuilder();
		for (int i = 0; i < searchFields.size(); i++) {
			String field = searchFields.get(i);
			if (field.contains(".")) { // assuming field is in the format "jsonColumn.jsonKey"
				String[] parts = field.split("\\.");
				String jsonColumnName = parts[0];
				String jsonKey = parts[1];
				searchConditions.append(String.format(" OR %s.%s ILIKE :searchTerm ", jsonColumnName, jsonKey));
			} else {
				searchConditions.append(String.format(" OR CAST(%s AS TEXT) ILIKE :searchTerm ", field));
			}
		}

		// Remove the leading " OR " from the searchConditions
		if (searchConditions.length() > 0) {
			searchConditions.delete(0, 4);
		}

		// Build the complete query string
		String queryString = String.format(
				"SELECT job_timeline.* FROM job_timeline inner join candidate on job_timeline.candidate_id = candidate.id inner join users on job_timeline.created_by = users.id WHERE job_timeline.is_deleted = :isDeleted AND job_timeline.is_active = :isActive AND job_timeline.created_by IN (:userIds) AND job_timeline.job_id = :jobId AND (%s) ORDER BY %s %s NULLS LAST",
				searchConditions.toString(), orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobTimelineEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		query.setParameter("userIds", userIds);
		query.setParameter("jobId", jobId);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobTimelineEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM job_timeline inner join candidate on job_timeline.candidate_id = candidate.id inner join users on job_timeline.created_by = users.id WHERE job_timeline.is_deleted = :isDeleted AND job_timeline.is_active = :isActive AND job_timeline.created_by IN (:userIds) AND job_timeline.job_id = :jobId AND (%s)",
				searchConditions.toString());

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
		countQuery.setParameter("userIds", userIds);
		countQuery.setParameter("jobId", jobId);
		countQuery.setParameter("searchTerm", "%" + searchTerm + "%");
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<JobTimelineEntity> findAllByOrderByAndSearchNumericWithUserIds(List<Long> userIds, Boolean isDeleted,
			Boolean isActive, Pageable pageable, List<String> searchFields, String searchTerm, Long userId,
			Long jobId) {
		// Determine if sortBy is a regular column or a JSONB column
		String sortBy = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		String orderByClause;
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			orderByClause = String.format("CAST(NULLIF(%s->>'%s', '') AS INTEGER)", jsonColumnName, jsonKey);
		} else {
			orderByClause = sortBy;
		}

		// Extract sort direction from pageable
		String sortDirection = pageable.getSort().isSorted()
				? pageable.getSort().get().findFirst().get().getDirection().name()
				: "ASC";

		// Build the dynamic search conditions based on searchFields
		StringBuilder searchConditions = new StringBuilder();
		for (int i = 0; i < searchFields.size(); i++) {
			String field = searchFields.get(i);
			if (field.contains(".")) { // assuming field is in the format "jsonColumn.jsonKey"
				String[] parts = field.split("\\.");
				String jsonColumnName = parts[0];
				String jsonKey = parts[1];
				searchConditions.append(String.format(" OR (%s->>'%s') ILIKE :searchTerm ", jsonColumnName, jsonKey));
			} else {
				searchConditions.append(String.format(" OR CAST(%s AS TEXT) ILIKE :searchTerm ", field));
			}
		}

		// Remove the leading " OR " from the searchConditions
		if (searchConditions.length() > 0) {
			searchConditions.delete(0, 4);
		}

		// Build the complete query string
		String queryString = String.format(
				"SELECT * FROM job_timeline inner join candidate on job_timeline.candidate_id = candidate.id WHERE job_timeline.is_deleted = :isDeleted AND job_timeline.is_active = :isActive AND job_timeline.created_by IN (:userIds) AND job_timeline.job_id = :jobId AND (%s) ORDER BY %s %s NULLS LAST",
				searchConditions.toString(), orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobTimelineEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		query.setParameter("userIds", userIds);
		query.setParameter("jobId", jobId);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobTimelineEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM job_timeline inner join candidate on job_timeline.candidate_id = candidate.id WHERE job_timeline.is_deleted = :isDeleted AND job_timeline.is_active = :isActive AND job_timeline.created_by IN (:userIds) AND job_timeline.job_id = :jobId AND (%s)",
				searchConditions.toString());

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
		countQuery.setParameter("userIds", userIds);
		countQuery.setParameter("jobId", jobId);
		countQuery.setParameter("searchTerm", "%" + searchTerm + "%");
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

}