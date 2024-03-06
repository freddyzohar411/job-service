package com.avensys.rts.jobservice.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.query.NativeQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.avensys.rts.jobservice.entity.JobEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class CustomJobRepositoryImpl implements CustomJobRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Page<JobEntity> findAllByOrderBy(Long userId, Boolean isDeleted, Boolean isDraft, Boolean isActive,
			Pageable pageable) {
		String sortBy = pageable.getSort().get().findFirst().get().getProperty();
		// Determine if sortBy is a regular column or a JSONB column
		String orderByClause = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			orderByClause = String.format("CAST(%s->>'%s' AS INTEGER)", jsonColumnName, jsonKey);
		}

		// Extract sort direction from pageable
		String sortDirection = pageable.getSort().isSorted()
				? pageable.getSort().get().findFirst().get().getDirection().name()
				: "ASC";

		// Build the complete query string
		String queryString = String.format(
				"SELECT * FROM job WHERE created_by = :userId AND is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive ORDER BY %s %s NULLS LAST",
				orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobEntity.class);
		query.setParameter("userId", userId);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		query.setParameter("isDraft", isDraft);
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = "SELECT COUNT(*) FROM job WHERE created_by = :userId AND is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive";

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("userId", userId);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
		countQuery.setParameter("isDraft", isDraft);
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<JobEntity> findAllByOrderByString(Long userId, Boolean isDeleted, Boolean isDraft, Boolean isActive,
			Pageable pageable) {
		String sortBy = pageable.getSort().get().findFirst().get().getProperty();
		// Determine if sortBy is a regular column or a JSONB column
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

		// Build the complete query string
		String queryString = String.format(
				"SELECT * FROM job WHERE created_by = :userId AND is_deleted = :isDeleted AND is_draft = :isDraft AND is_active = :isActive ORDER BY %s %s NULLS LAST",
				orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobEntity.class);
		query.setParameter("userId", userId);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		query.setParameter("isDraft", isDraft);
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = "SELECT COUNT(*) FROM job WHERE created_by = :userId AND is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive";

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("userId", userId);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
		countQuery.setParameter("isDraft", isDraft);
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<JobEntity> findAllByOrderByNumeric(Long userId, Boolean isDeleted, Boolean isDraft, Boolean isActive,
			Pageable pageable) {
		String sortBy = pageable.getSort().get().findFirst().get().getProperty();
		// Determine if sortBy is a regular column or a JSONB column
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

		// Build the complete query string
		String queryString = String.format(
				"SELECT * FROM job WHERE created_by = :userId AND is_deleted = :isDeleted AND is_draft = :isDraft AND is_active = :isActive ORDER BY %s %s NULLS LAST",
				orderByClause, sortDirection);

		// Log the generated SQL (for debugging)
		System.out.println(queryString);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobEntity.class);
		query.setParameter("userId", userId);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		query.setParameter("isDraft", isDraft);
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = "SELECT COUNT(*) FROM job WHERE created_by = :userId AND is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive";

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("userId", userId);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
		countQuery.setParameter("isDraft", isDraft);
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<JobEntity> findAllByOrderByAndSearchString(Long userId, Boolean isDeleted, Boolean isDraft,
			Boolean isActive, Pageable pageable, List<String> searchFields, String searchTerm) {

		// Determine if sortBy is a regular column or a JSONB column
		String sortBy = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		String orderByClause;
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			orderByClause = String.format("(%s->>'%s')", jsonColumnName, jsonKey);
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
//                searchConditions.append(String.format(" OR %s ILIKE :searchTerm ", field));
			}
		}

		// Remove the leading " OR " from the searchConditions
		if (searchConditions.length() > 0) {
			searchConditions.delete(0, 4);
		}

		// Build the complete query string
		String queryString = String.format(
				"SELECT * FROM job WHERE created_by = :userId AND is_deleted = :isDeleted AND is_draft = :isDraft AND is_active = :isActive AND (%s) ORDER BY %s %s NULLS LAST",
				searchConditions.toString(), orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobEntity.class);
		query.setParameter("userId", userId);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		query.setParameter("isDraft", isDraft);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM job WHERE created_by = :userId AND is_deleted = :isDeleted AND is_draft = :isDraft AND is_active = :isActive AND (%s)",
				searchConditions.toString());

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("userId", userId);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
		countQuery.setParameter("isDraft", isDraft);
		countQuery.setParameter("searchTerm", "%" + searchTerm + "%");
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<JobEntity> findAllByOrderByAndSearchNumeric(Long userId, Boolean isDeleted, Boolean isDraft,
			Boolean isActive, Pageable pageable, List<String> searchFields, String searchTerm) {

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
//                searchConditions.append(String.format(" OR %s ILIKE :searchTerm ", field));
			}
		}

		// Remove the leading " OR " from the searchConditions
		if (searchConditions.length() > 0) {
			searchConditions.delete(0, 4);
		}

		// Build the complete query string
		String queryString = String.format(
				"SELECT * FROM job WHERE created_by = :userId AND is_deleted = :isDeleted AND is_draft = :isDraft AND is_active = :isActive AND (%s) ORDER BY %s %s NULLS LAST",
				searchConditions.toString(), orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobEntity.class);
		query.setParameter("userId", userId);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		query.setParameter("isDraft", isDraft);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM job WHERE created_by = :userId AND is_draft = :isDraft AND is_deleted = :isDeleted AND is_active = :isActive AND (%s)",
				searchConditions.toString());

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("userId", userId);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
		countQuery.setParameter("isDraft", isDraft);
		countQuery.setParameter("searchTerm", "%" + searchTerm + "%");
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public List<JobEntity> getAllAccountsNameWithSearch(String query, Long userId, Boolean isDeleted, Boolean isDraft) {
		StringBuilder sql = new StringBuilder(
				"SELECT * FROM job WHERE created_by = :userId AND is_deleted = :isDeleted AND is_draft = :isDraft");

		Pattern pattern = Pattern.compile("([\\w.]+)([><]=?|!=|=)([\\w.]+)");
		Matcher matcher = pattern.matcher(query);

		int parameterPosition = 1;
		List<String> values = new ArrayList<>();

		while (matcher.find()) {
			String fieldName = matcher.group(1);
			String operator = matcher.group(2);
			String value = matcher.group(3);

			sql.append(" AND ");

			if (fieldName.contains(".")) {
				String[] parts = fieldName.split("\\.");
				String jsonColumnName = parts[0];
				String jsonKey = parts[1];
				switch (operator) {
				case "=" ->
					sql.append(String.format("(%s->>'%s') ILIKE :param%d", jsonColumnName, jsonKey, parameterPosition));
				case "!=" -> sql.append(
						String.format("(%s->>'%s') NOT ILIKE :param%d", jsonColumnName, jsonKey, parameterPosition));
				case ">" ->
					sql.append(String.format("CAST(NULLIF(%s->>'%s', '') AS INTEGER) > CAST(:param%d AS INTEGER)",
							jsonColumnName, jsonKey, parameterPosition));
				case "<" ->
					sql.append(String.format("CAST(NULLIF(%s->>'%s', '') AS INTEGER) < CAST(:param%d AS INTEGER)",
							jsonColumnName, jsonKey, parameterPosition));
				case ">=" ->
					sql.append(String.format("CAST(NULLIF(%s->>'%s', '') AS INTEGER) >= CAST(:param%d AS INTEGER)",
							jsonColumnName, jsonKey, parameterPosition));
				case "<=" ->
					sql.append(String.format("CAST(NULLIF(%s->>'%s', '') AS INTEGER) <= CAST(:param%d AS INTEGER)",
							jsonColumnName, jsonKey, parameterPosition));
				}
			} else {
				switch (operator) {
				case "=" -> sql.append(String.format("%s ILIKE :param%d", fieldName, parameterPosition));
				case "!=" -> sql.append(String.format("%s NOT ILIKE :param%d", fieldName, parameterPosition));
				case ">" -> sql.append(String.format("CAST(NULLIF(%s,'') AS INTEGER) > CAST(:param%d AS INTEGER)",
						fieldName, parameterPosition));
				case "<" -> sql.append(String.format("CAST(NULLIF(%s,'') AS INTEGER) < CAST(:param%d AS INTEGER)",
						fieldName, parameterPosition));
				case ">=" -> sql.append(String.format("CAST(NULLIF(%s,'') AS INTEGER) >= CAST(:param%d AS INTEGER)",
						fieldName, parameterPosition));
				case "<=" -> sql.append(String.format("CAST(NULLIF(%s,'') AS INTEGER) <= CAST(:param%d AS INTEGER)",
						fieldName, parameterPosition));
				}
//				sql.append(String.format("%s ILIKE ?", fieldName));
			}

			if (operator.equals("!=")) {
				values.add("%" + value.toLowerCase() + "%");
			} else if (operator.equals(">") || operator.equals("<") || operator.equals(">=") || operator.equals("<=")) {
				values.add(value);
				System.out.println("Value: " + value);
			} else {
				values.add("%" + value.toLowerCase() + "%");
			}

//			values.add("%" + value.toLowerCase() + "%");
			parameterPosition++;
		}

		System.out.println("SQL Query: " + sql);

		NativeQuery<JobEntity> nativeQuery = (NativeQuery<JobEntity>) entityManager.createNativeQuery(sql.toString(),
				JobEntity.class);

		// Set parameters for user isDeleted and isDraft
		nativeQuery.setParameter("userId", userId);
		nativeQuery.setParameter("isDeleted", isDeleted);
		nativeQuery.setParameter("isDraft", isDraft);

		// Batch parameter binding for dynamic search fields
		for (int i = 1; i <= values.size(); i++) {
			nativeQuery.setParameter("param" + i, values.get(i - 1));
		}

		return nativeQuery.getResultList();
	}

	@Override
	public void updateDocumentEntityId(Long tempId, Long originalId, Long userId, String entityType) {
		entityManager.createNativeQuery("update document set entity_id = " + originalId + " where entity_id = " + tempId
				+ " and created_by = " + userId + " and entity_type = '" + entityType + "'").executeUpdate();
	}

	// With user ids
	@Override
	public Page<JobEntity> findAllByOrderByStringWithUserIds(List<Long> userIds, Boolean isDeleted, Boolean isActive,
			Pageable pageable, String jobType, Long userId) {

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

		// User ID condition
		String userCondition = "";
		if (!userIds.isEmpty() && !jobType.equals("fod") && !jobType.equals("active_jobs")) {
			userCondition = " AND (created_by IN (:userIds) OR CAST(NULLIF(job_submission_data->>'accountOwnerId', '') as INTEGER) IN (:userIds))";
		}

		// Build the complete query string with user filter and excluding NULLs
		String queryString = String.format(
				"SELECT * FROM job WHERE {1} is_deleted = :isDeleted AND is_active = :isActive %s ORDER BY %s %s NULLS LAST",
				userCondition, orderByClause, sortDirection);

		if (jobType != null && jobType.length() > 0) {
			queryString = getQuery(queryString, jobType, userId, isActive, userIds);
			System.out.println("QueryString: " + queryString);
			isActive = getActive(jobType, isActive);
		} else {
			queryString = queryString.replace("{1}", "");
		}

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			query.setParameter("userIds", userIds);
		}
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM job WHERE {1} is_deleted = :isDeleted AND is_active = :isActive %s",
				userCondition);

		if (jobType != null && jobType.length() > 0) {
			countQueryString = getQuery(countQueryString, jobType, userId, isActive, userIds);
			isActive = getActive(jobType, isActive);
		} else {
			countQueryString = countQueryString.replace("{1}", "");
		}

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
	public Page<JobEntity> findAllByOrderByNumericWithUserIds(List<Long> userIds, Boolean isDeleted, Boolean isActive,
			Pageable pageable, String jobType, Long userId) {
		System.out.println("Here");
		System.out.println("JobType: " + jobType);

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

		// User ID condition
		String userCondition = "";
		if (!userIds.isEmpty() && !jobType.equals("fod") && !jobType.equals("active_jobs")) {
			userCondition = " AND (created_by IN (:userIds) OR CAST(NULLIF(job_submission_data->>'accountOwnerId', '') as INTEGER) IN (:userIds))";
		}

		// Build the complete query string with user filter and excluding NULLs
		String queryString = String.format(
				"SELECT * FROM job WHERE {1} is_deleted = :isDeleted AND is_active = :isActive %s ORDER BY %s %s NULLS LAST",
				userCondition, orderByClause, sortDirection);

		if (jobType != null && jobType.length() > 0) {
			queryString = getQuery(queryString, jobType, userId, isActive, userIds);
			System.out.println("QueryStringNum: " + queryString);
			isActive = getActive(jobType, isActive);
		} else {
			queryString = queryString.replace("{1}", "");
		}

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			query.setParameter("userIds", userIds);
		}
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM job WHERE {1} is_deleted = :isDeleted AND is_active = :isActive %s",
				userCondition);

		if (jobType != null && jobType.length() > 0) {
			countQueryString = getQuery(countQueryString, jobType, userId, isActive, userIds);
			isActive = getActive(jobType, isActive);
		} else {
			countQueryString = countQueryString.replace("{1}", "");
		}

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
	public Page<JobEntity> findAllByOrderByAndSearchStringWithUserIds(List<Long> userIds, Boolean isDeleted,
			Boolean isActive, Pageable pageable, List<String> searchFields, String searchTerm, String jobType,
			Long userId) {
		// Determine if sortBy is a regular column or a JSONB column
		String sortBy = pageable.getSort().isSorted() ? pageable.getSort().get().findFirst().get().getProperty()
				: "updated_at";
		String orderByClause;
		if (sortBy.contains(".")) { // assuming sortBy is in the format "jsonColumn.jsonKey"
			String[] parts = sortBy.split("\\.");
			String jsonColumnName = parts[0];
			String jsonKey = parts[1];
			orderByClause = String.format("(%s->>'%s')", jsonColumnName, jsonKey);
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

		// User ID condition
		String userCondition = "";
		if (!userIds.isEmpty() && !jobType.equals("fod") && !jobType.equals("active_jobs")) {
			userCondition = " AND (created_by IN (:userIds) OR CAST(NULLIF(job_submission_data->>'accountOwnerId', '') as INTEGER) IN (:userIds))";
		}

		// Build the complete query string
		String queryString = String.format(
				"SELECT * FROM job WHERE {1} is_deleted = :isDeleted AND is_active = :isActive %s AND (%s) ORDER BY %s %s NULLS LAST",
				userCondition, searchConditions.toString(), orderByClause, sortDirection);

		if (jobType != null && jobType.length() > 0) {
			queryString = getQuery(queryString, jobType, userId, isActive, userIds);
			isActive = getActive(jobType, isActive);
		} else {
			queryString = queryString.replace("{1}", "");
		}

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		if (!userIds.isEmpty() && !jobType.equals("fod")) {
			query.setParameter("userIds", userIds);
		}
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM job WHERE {1} is_deleted = :isDeleted AND is_active = :isActive %s AND (%s)",
				userCondition, searchConditions.toString());

		if (jobType != null && jobType.length() > 0) {
			countQueryString = getQuery(countQueryString, jobType, userId, isActive, userIds);
			isActive = getActive(jobType, isActive);
		} else {
			countQueryString = countQueryString.replace("{1}", "");
		}

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			countQuery.setParameter("userIds", userIds);
		}
		countQuery.setParameter("searchTerm", "%" + searchTerm + "%");
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<JobEntity> findAllByOrderByAndSearchNumericWithUserIds(List<Long> userIds, Boolean isDeleted,
			Boolean isActive, Pageable pageable, List<String> searchFields, String searchTerm, String jobType,
			Long userId) {
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

		// User ID condition
		String userCondition = "";
		if (!userIds.isEmpty() && !jobType.equals("fod") && !jobType.equals("active_jobs")) {
			userCondition = " AND (created_by IN (:userIds) OR CAST(NULLIF(job_submission_data->>'accountOwnerId', '') as INTEGER) IN (:userIds))";
		}

		// Build the complete query string
		String queryString = String.format(
				"SELECT * FROM job WHERE {1} is_deleted = :isDeleted AND is_active = :isActive %s AND (%s) ORDER BY %s %s NULLS LAST",
				userCondition, searchConditions.toString(), orderByClause, sortDirection);

		if (jobType != null && jobType.length() > 0) {
			queryString = getQuery(queryString, jobType, userId, isActive, userIds);
			isActive = getActive(jobType, isActive);
		} else {
			queryString = queryString.replace("{1}", "");
		}

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			query.setParameter("userIds", userIds);
		}
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM job WHERE {1} is_deleted = :isDeleted AND is_active = :isActive %s AND (%s)",
				userCondition, searchConditions.toString());

		if (jobType != null && jobType.length() > 0) {
			countQueryString = getQuery(countQueryString, jobType, userId, isActive, userIds);
			isActive = getActive(jobType, isActive);
		} else {
			countQueryString = countQueryString.replace("{1}", "");
		}

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
		if (!userIds.isEmpty()) {
			countQuery.setParameter("userIds", userIds);
		}
		countQuery.setParameter("searchTerm", "%" + searchTerm + "%");
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	private Boolean getActive(String jobType, Boolean isActive) {
		Boolean active = isActive;
		switch (jobType) {
		case "active_jobs": {
			active = true;
			break;
		}
		case "inactive_jobs": {
			active = false;
			break;
		}
		default:
			active = isActive;
			break;
		}
		return active;
	}

	private String getQuery(String queryString, String jobType, Long userId, Boolean isActive, List<Long> userIds) {
		if (jobType != null && jobType.length() > 0 && !userIds.isEmpty()) {
			switch (jobType) {
			case "new_job": {
				queryString = queryString.replace("{1}",
						"id not in (select distinct(fod.job_id) from job_recruiter_fod fod) AND ");
				break;
			}
			case "active_jobs": {
				queryString = queryString.replace("{1}",
						"id in (select distinct(job_id) from job_recruiter_fod where (recruiter_id in (:userIds) "
								+ "or sales_id in (:userIds))) AND ");
				break;
			}
			case "inactive_jobs": {
				isActive = false;
				queryString = queryString.replace("{1}",
						"id in (select distinct(fod.job_id) from job_recruiter_fod fod) AND ");
				break;
			}
			case "closed_jobs": {
				queryString = queryString.replace("{1}",
						"id in (select distinct(fod.job_id) from job_recruiter_fod fod where fod.status = 'CLOSED') AND ");
				break;
			}
			case "fod": {
				queryString = queryString.replace("{1}",
						"id in (select distinct(job_id) from job_recruiter_fod where (recruiter_id in (:userIds) "
								+ "or sales_id in (:userIds)) and created_at >= current_date) AND ");
				break;
			}
			case "assigned_jobs": {
				queryString = queryString.replace("{1}",
						"id in (select distinct(job_id) from job_recruiter_fod where status != 'CLOSED' and (recruiter_id in (:userIds) "
								+ "or sales_id in (:userIds))) AND ");
				break;
			}
			default:
				queryString = queryString.replace("{1}", "");
				break;
			}
		} else if (jobType != null && jobType.length() > 0 && userIds.isEmpty()) {
			switch (jobType) {
			case "new_job": {
				queryString = queryString.replace("{1}",
						"id not in (select distinct(fod.job_id) from job_recruiter_fod fod) AND ");
				break;
			}
			case "active_jobs": {
				queryString = queryString.replace("{1}", "id in (select distinct(job_id) from job_recruiter_fod) AND ");
				break;
			}
			case "inactive_jobs": {
				isActive = false;
				queryString = queryString.replace("{1}",
						"id in (select distinct(fod.job_id) from job_recruiter_fod fod) AND ");
				break;
			}
			case "closed_jobs": {
				queryString = queryString.replace("{1}",
						"id in (select distinct(fod.job_id) from job_recruiter_fod fod where fod.status = 'CLOSED') AND ");
				break;
			}
			case "fod": {
				queryString = queryString.replace("{1}",
						"id in (select distinct(job_id) from job_recruiter_fod where created_at >= current_date) AND ");
				break;
			}
			case "assigned_jobs": {
				queryString = queryString.replace("{1}",
						"id in (select distinct(job_id) from job_recruiter_fod where status != 'CLOSED' and (recruiter_id in (:userIds) "
								+ "or sales_id in (:userIds))) AND ");
				break;
			}
			default:
				queryString = queryString.replace("{1}", "");
				break;
			}
		} else {
			queryString = queryString.replace("{1}", "");
		}
		return queryString;
	}

}