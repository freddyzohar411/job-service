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
			Boolean isActive, Pageable pageable, Long userId, Long jobId, Integer stageType) {

		// Determine if sortBy is a regular column or a JSONB column
		String sortBy = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "job_timeline.updated_at";
		String orderByClause = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "job_timeline.updated_at";
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			orderByClause = String.format("%s.%s", jsonColumnName, jsonKey);
		}

		// Extract sort direction from pageable
		String sortDirection = pageable.getSort().isSorted()
				? pageable.getSort().get().findFirst().get().getDirection().name()
				: "ASC";

		// User Condition
		String userCondition = "";
		if (!userIds.isEmpty()) {
			userCondition = "AND job_timeline.created_by IN (:userIds)";
		}

		// Stage Type Condition
		String stageTypeCondition = "";
		if (stageType != null) {
			stageTypeCondition = "and jcs.job_stage_id = (select id from job_stage js where js.stage_order = :stageTypeId)";
		}

		// Build the complete query string with user filter and excluding NULLs
		String queryString = String.format(
				"SELECT job_timeline.* FROM job_timeline inner join candidate on job_timeline.candidate_id = candidate.id inner join users on job_timeline.created_by = users.id inner join job_candidate_stage jcs on job_timeline.job_id = jcs.job_id and job_timeline.candidate_id = jcs.candidate_id and jcs.job_id = :jobId WHERE job_timeline.is_deleted = :isDeleted AND job_timeline.is_active = :isActive %s AND job_timeline.job_id = :jobId %s group by candidate.first_name,job_timeline.id,jcs.job_id,jcs.candidate_id ORDER BY %s %s NULLS LAST",
				userCondition, stageTypeCondition, orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobTimelineEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			query.setParameter("userIds", userIds);
		}
		if (stageType != null) {
			query.setParameter("stageTypeId", stageType);
		}
		query.setParameter("jobId", jobId);
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobTimelineEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM job_timeline inner join candidate on job_timeline.candidate_id = candidate.id inner join users on job_timeline.created_by = users.id WHERE job_timeline.is_deleted = :isDeleted AND job_timeline.is_active = :isActive %s AND job_timeline.job_id = :jobId",
				userCondition);

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			countQuery.setParameter("userIds", userIds);
		}
		countQuery.setParameter("jobId", jobId);
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<JobTimelineEntity> findAllByOrderByNumericWithUserIds(List<Long> userIds, Boolean isDeleted,
			Boolean isActive, Pageable pageable, Long userId, Long jobId, Integer stageType) {
		// Determine if sortBy is a regular column or a JSONB column
		String sortBy = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "job_timeline.updated_at";
		String orderByClause = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "job_timeline.updated_at";
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			orderByClause = String.format("CAST(NULLIF(%s.%s, '') AS INTEGER)", jsonColumnName, jsonKey);
		}

		// User Condition
		String userCondition = "";
		if (!userIds.isEmpty()) {
			userCondition = "AND job_timeline.created_by IN (:userIds)";
		}

		// Stage Type Condition
		String stageTypeCondition = "";
		if (stageType != null) {
			stageTypeCondition = "and jcs.job_stage_id = (select id from job_stage js where js.stage_order = :stageTypeId)";
		}

		// Extract sort direction from pageable
		String sortDirection = pageable.getSort().isSorted()
				? pageable.getSort().get().findFirst().get().getDirection().name()
				: "ASC";

		// Build the complete query string with user filter and excluding NULLs
		String queryString = String.format(
				"SELECT job_timeline.* FROM job_timeline inner join candidate on job_timeline.candidate_id = candidate.id inner join users on job_timeline.created_by = users.id inner join job_candidate_stage jcs on job_timeline.job_id = jcs.job_id and job_timeline.candidate_id = jcs.candidate_id and jcs.job_id = :jobId WHERE job_timeline.is_deleted = :isDeleted AND job_timeline.is_active = :isActive %s AND job_timeline.job_id = :jobId %s group by job_timeline.id,jcs.job_id,jcs.candidate_id ORDER BY %s %s NULLS LAST",
				userCondition, stageTypeCondition, orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobTimelineEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			query.setParameter("userIds", userIds);
		}
		if (stageType != null) {
			query.setParameter("stageTypeId", stageType);
		}
		query.setParameter("jobId", jobId);
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobTimelineEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM job_timeline inner join candidate on job_timeline.candidate_id = candidate.id inner join users on job_timeline.created_by = users.id WHERE job_timeline.is_deleted = :isDeleted AND job_timeline.is_active = :isActive %s AND job_timeline.job_id = :jobId",
				userCondition);

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			countQuery.setParameter("userIds", userIds);
		}
		countQuery.setParameter("jobId", jobId);
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<JobTimelineEntity> findAllByOrderByAndSearchStringWithUserIds(List<Long> userIds, Boolean isDeleted,
			Boolean isActive, Pageable pageable, List<String> searchFields, String searchTerm, Long userId, Long jobId,
			Integer stageType) {
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

		// User Condition
		String userCondition = "";
		if (!userIds.isEmpty()) {
			userCondition = "AND job_timeline.created_by IN (:userIds)";
		}

		// Stage Type Condition
		String stageTypeCondition = "";
		if (stageType != null) {
			stageTypeCondition = "and jcs.job_stage_id = (select id from job_stage js where js.stage_order = :stageTypeId)";
		}

		// Build the complete query string
		String queryString = String.format(
				"SELECT job_timeline.* FROM job_timeline inner join candidate on job_timeline.candidate_id = candidate.id inner join users on job_timeline.created_by = users.id inner join job_candidate_stage jcs on job_timeline.job_id = jcs.job_id and job_timeline.candidate_id = jcs.candidate_id and jcs.job_id = :jobId WHERE job_timeline.is_deleted = :isDeleted AND job_timeline.is_active = :isActive %s AND job_timeline.job_id = :jobId %s AND (%s) group by job_timeline.id,jcs.job_id,jcs.candidate_id ORDER BY %s %s NULLS LAST",
				userCondition, stageTypeCondition, searchConditions.toString(), orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobTimelineEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			query.setParameter("userIds", userIds);
		}
		if (stageType != null) {
			query.setParameter("stageTypeId", stageType);
		}
		query.setParameter("jobId", jobId);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobTimelineEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM job_timeline inner join candidate on job_timeline.candidate_id = candidate.id inner join users on job_timeline.created_by = users.id WHERE job_timeline.is_deleted = :isDeleted AND job_timeline.is_active = :isActive %s AND job_timeline.job_id = :jobId AND (%s)",
				userCondition, searchConditions.toString());

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			countQuery.setParameter("userIds", userIds);
		}
		countQuery.setParameter("jobId", jobId);
		countQuery.setParameter("searchTerm", "%" + searchTerm + "%");
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<JobTimelineEntity> findAllByOrderByAndSearchNumericWithUserIds(List<Long> userIds, Boolean isDeleted,
			Boolean isActive, Pageable pageable, List<String> searchFields, String searchTerm, Long userId, Long jobId,
			Integer stageType) {
		// Determine if sortBy is a regular column or a JSONB column
		String sortBy = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "job_timeline.updated_at";
		String orderByClause;
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			orderByClause = String.format("CAST(NULLIF(%s.%s, '') AS INTEGER)", jsonColumnName, jsonKey);
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

		// User Condition
		String userCondition = "";
		if (!userIds.isEmpty()) {
			userCondition = "AND job_timeline.created_by IN (:userIds)";
		}

		// Stage Type Condition
		String stageTypeCondition = "";
		if (stageType != null) {
			stageTypeCondition = "and jcs.job_stage_id = (select id from job_stage js where js.stage_order = :stageTypeId)";
		}

		// Build the complete query string
		String queryString = String.format(
				"SELECT job_timeline.* FROM job_timeline inner join candidate on job_timeline.candidate_id = candidate.id inner join users on job_timeline.created_by = users.id inner join job_candidate_stage jcs on job_timeline.job_id = jcs.job_id and job_timeline.candidate_id = jcs.candidate_id and jcs.job_id = :jobId WHERE job_timeline.is_deleted = :isDeleted AND job_timeline.is_active = :isActive %s AND job_timeline.job_id = :jobId %s AND (%s) group by job_timeline.id,jcs.job_id,jcs.candidate_id ORDER BY %s %s NULLS LAST",
				userCondition, stageTypeCondition, searchConditions.toString(), orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobTimelineEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			query.setParameter("userIds", userIds);
		}
		if (stageType != null) {
			query.setParameter("stageTypeId", stageType);
		}
		query.setParameter("jobId", jobId);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobTimelineEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM job_timeline inner join candidate on job_timeline.candidate_id = candidate.id WHERE job_timeline.is_deleted = :isDeleted AND job_timeline.is_active = :isActive %s AND job_timeline.job_id = :jobId AND (%s)",
				userCondition, searchConditions.toString());

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			countQuery.setParameter("userIds", userIds);
		}
		countQuery.setParameter("jobId", jobId);
		countQuery.setParameter("searchTerm", "%" + searchTerm + "%");
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<JobTimelineEntity> findAllByOrderByStringWithUserIdsReport(List<Long> userIds, Boolean isDeleted,
			Boolean isActive, Pageable pageable, Long userId, String jobStatus) {

		String jobStatusFilterQuery = "";
		if (jobStatus != null && !jobStatus.isEmpty()) {
			jobStatusFilterQuery = String.format("AND job.job_submission_data->>'jobStatus' = '%s'", jobStatus);
		}

		// Determine if sortBy is a regular column or a JSONB column
		String sortBy = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "job_timeline.updated_at";
		String orderByClause = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "job_timeline.updated_at";
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			orderByClause = String.format("%s.%s", jsonColumnName, jsonKey);
		}

		// Extract sort direction from pageable
		String sortDirection = pageable.getSort().isSorted()
				? pageable.getSort().get().findFirst().get().getDirection().name()
				: "ASC";

		// User Condition
		String userCondition = "";
		if (!userIds.isEmpty()) {
			userCondition = "AND job_timeline.created_by IN (:userIds)";
		}

		// Build the complete query string with user filter and excluding NULLs
		String queryString = String.format(
				"SELECT job_timeline.* FROM job_timeline "
						+ "INNER JOIN candidate ON job_timeline.candidate_id = candidate.id "
						+ "INNER JOIN users ON job_timeline.created_by = users.id "
						+ "INNER JOIN job ON job_timeline.job_id = job.id "
						+ "WHERE job_timeline.is_deleted = :isDeleted " + "AND job_timeline.is_active = :isActive %s %s "
						+ "GROUP BY candidate.first_name, job_timeline.id, job.id " + "ORDER BY %s %s NULLS LAST",
				userCondition, jobStatusFilterQuery, orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobTimelineEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			query.setParameter("userIds", userIds);
		}
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobTimelineEntity> resultList = query.getResultList();

		// Build the count query string
		// Now use this variable in your formatted string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM job_timeline "
						+ "INNER JOIN candidate ON job_timeline.candidate_id = candidate.id "
						+ "INNER JOIN users ON job_timeline.created_by = users.id "
						+ "INNER JOIN job ON job_timeline.job_id = job.id "
						+ "WHERE job_timeline.is_deleted = :isDeleted "
						+ "AND job_timeline.is_active = :isActive %s %s ",
				userCondition, jobStatusFilterQuery);

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			countQuery.setParameter("userIds", userIds);
		}
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<JobTimelineEntity> findAllByOrderByNumericWithUserIdsReport(List<Long> userIds, Boolean isDeleted,
			Boolean isActive, Pageable pageable, Long userId, String jobStatus) {

		String jobStatusFilterQuery = "";
		if (jobStatus != null && !jobStatus.isEmpty()) {
			jobStatusFilterQuery = String.format("AND job.job_submission_data->>'jobStatus' = '%s'", jobStatus);
		}

		// Determine if sortBy is a regular column or a JSONB column
		String sortBy = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "job_timeline.updated_at";
		String orderByClause = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "job_timeline.updated_at";
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			orderByClause = String.format("CAST(NULLIF(%s.%s, '') AS INTEGER)", jsonColumnName, jsonKey);
		}

		// User Condition
		String userCondition = "";
		if (!userIds.isEmpty()) {
			userCondition = "AND job_timeline.created_by IN (:userIds)";
		}

		// Extract sort direction from pageable
		String sortDirection = pageable.getSort().isSorted()
				? pageable.getSort().get().findFirst().get().getDirection().name()
				: "ASC";

		// Build the complete query string with user filter and excluding NULLs
		String queryString = String.format(
				"SELECT job_timeline.* FROM job_timeline "
						+ "INNER JOIN candidate ON job_timeline.candidate_id = candidate.id "
						+ "INNER JOIN users ON job_timeline.created_by = users.id "
						+ "INNER JOIN job ON job_timeline.job_id = job.id "
						+ "WHERE job_timeline.is_deleted = :isDeleted " + "AND job_timeline.is_active = :isActive %s %s "
						+ "GROUP BY candidate.first_name, job_timeline.id, job.id " + "ORDER BY %s %s NULLS LAST",
				userCondition, jobStatusFilterQuery, orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobTimelineEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			query.setParameter("userIds", userIds);
		}
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobTimelineEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM job_timeline "
						+ "INNER JOIN candidate ON job_timeline.candidate_id = candidate.id "
						+ "INNER JOIN users ON job_timeline.created_by = users.id "
						+ "INNER JOIN job ON job_timeline.job_id = job.id "
						+ "WHERE job_timeline.is_deleted = :isDeleted "
						+ "AND job_timeline.is_active = :isActive %s %s ",
				userCondition, jobStatusFilterQuery);

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			countQuery.setParameter("userIds", userIds);
		}
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

}