package com.avensys.rts.jobservice.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.hibernate.query.NativeQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.payload.FilterDTO;
import com.avensys.rts.jobservice.util.QueryUtil;

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
			} else {
				values.add("%" + value.toLowerCase() + "%");
			}
			parameterPosition++;
		}

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
			Pageable pageable, String jobType, Long userId, List<FilterDTO> filters) {

		String filterQuery = "";
		if (filters != null) {
			if (!filters.isEmpty()) {
				filterQuery = " AND (" + QueryUtil.buildQueryFromFilters(filters) + ")";
			}
		}
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
				"SELECT * FROM job WHERE {1} is_deleted = :isDeleted AND is_active = :isActive %s ORDER BY %s %s NULLS LAST",
				filterQuery, orderByClause, sortDirection);

		if (jobType != null && jobType.length() > 0) {
			queryString = getQuery(queryString, jobType, userId, isActive, userIds);
		} else {
			queryString = queryString.replace("{1}", "");
		}

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);

		if (!userIds.isEmpty() && !jobType.equals("active_jobs")) {
			query.setParameter("userIds", userIds);
		}
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM job WHERE {1} is_deleted = :isDeleted AND is_active = :isActive %s", filterQuery);

		if (jobType != null && jobType.length() > 0) {
			countQueryString = getQuery(countQueryString, jobType, userId, isActive, userIds);
		} else {
			countQueryString = countQueryString.replace("{1}", "");
		}

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
		if (!userIds.isEmpty() && !jobType.equals("active_jobs")) {
			countQuery.setParameter("userIds", userIds);
		}
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<JobEntity> findAllByOrderByNumericWithUserIds(List<Long> userIds, Boolean isDeleted, Boolean isActive,
			Pageable pageable, String jobType, Long userId, List<FilterDTO> filters) {

		String filterQuery = "";
		if (filters != null) {
			if (!filters.isEmpty()) {
				filterQuery = " AND (" + QueryUtil.buildQueryFromFilters(filters) + ")";
			}
		}

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
				"SELECT * FROM job WHERE {1} is_deleted = :isDeleted AND is_active = :isActive %s ORDER BY %s %s NULLS LAST",
				filterQuery, orderByClause, sortDirection);

		if (jobType != null && jobType.length() > 0) {
			queryString = getQuery(queryString, jobType, userId, isActive, userIds);
		} else {
			queryString = queryString.replace("{1}", "");
		}

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		if (!userIds.isEmpty() && !jobType.equals("active_jobs")) {
			query.setParameter("userIds", userIds);
		}
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM job WHERE {1} is_deleted = :isDeleted AND is_active = :isActive %s", filterQuery);

		if (jobType != null && jobType.length() > 0) {
			countQueryString = getQuery(countQueryString, jobType, userId, isActive, userIds);
		} else {
			countQueryString = countQueryString.replace("{1}", "");
		}

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
		if (!userIds.isEmpty() && !jobType.equals("active_jobs")) {
			countQuery.setParameter("userIds", userIds);
		}
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public Page<JobEntity> findAllByOrderByAndSearchStringWithUserIds(List<Long> userIds, Boolean isDeleted,
			Boolean isActive, Pageable pageable, List<String> searchFields, String searchTerm, String jobType,
			Long userId, List<FilterDTO> filters) {
		String filterQuery = "";
		if (filters != null) {
			if (!filters.isEmpty()) {
				filterQuery = " AND (" + QueryUtil.buildQueryFromFilters(filters) + ")";
			}
		}
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

		// Build the complete query string
		String queryString = String.format(
				"SELECT * FROM job WHERE {1} is_deleted = :isDeleted AND is_active = :isActive AND (%s) %s ORDER BY %s %s NULLS LAST",
				searchConditions.toString(), filterQuery, orderByClause, sortDirection);

		if (jobType != null && jobType.length() > 0) {
			queryString = getQuery(queryString, jobType, userId, isActive, userIds);
		} else {
			queryString = queryString.replace("{1}", "");
		}

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		if (!userIds.isEmpty() && !jobType.equals("active_jobs")) {
			query.setParameter("userIds", userIds);
		}
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM job WHERE {1} is_deleted = :isDeleted AND is_active = :isActive AND (%s) %s",
				searchConditions.toString(), filterQuery);

		if (jobType != null && jobType.length() > 0) {
			countQueryString = getQuery(countQueryString, jobType, userId, isActive, userIds);
		} else {
			countQueryString = countQueryString.replace("{1}", "");
		}

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
		if (!userIds.isEmpty() && !jobType.equals("active_jobs")) {
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
			Long userId, List<FilterDTO> filters) {
		String filterQuery = "";
		if (filters != null) {
			if (!filters.isEmpty()) {
				filterQuery = " AND (" + QueryUtil.buildQueryFromFilters(filters) + ")";
			}
		}
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
				"SELECT * FROM job WHERE {1} is_deleted = :isDeleted AND is_active = :isActive AND (%s) %s ORDER BY %s %s NULLS LAST",
				searchConditions.toString(), filterQuery, orderByClause, sortDirection);

		if (jobType != null && jobType.length() > 0) {
			queryString = getQuery(queryString, jobType, userId, isActive, userIds);
		} else {
			queryString = queryString.replace("{1}", "");
		}

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobEntity.class);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		if (!userIds.isEmpty() && !jobType.equals("active_jobs")) {
			query.setParameter("userIds", userIds);
		}
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM job WHERE {1} is_deleted = :isDeleted AND is_active = :isActive AND (%s) %s",
				searchConditions.toString(), filterQuery);

		if (jobType != null && jobType.length() > 0) {
			countQueryString = getQuery(countQueryString, jobType, userId, isActive, userIds);
		} else {
			countQueryString = countQueryString.replace("{1}", "");
		}

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
		if (!userIds.isEmpty() && !jobType.equals("active_jobs")) {
			countQuery.setParameter("userIds", userIds);
		}
		countQuery.setParameter("searchTerm", "%" + searchTerm + "%");
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	private String getQuery(String queryString, String jobType, Long userId, Boolean isActive, List<Long> userIds) {
		if (jobType != null && jobType.length() > 0 && !userIds.isEmpty()) {
			switch (jobType) {
			case "new_job": {
				queryString = queryString.replace("{1}",
						"id not in (select distinct(fod.job_id) from job_recruiter_fod fod) and is_active = true and is_deleted = false and (created_by IN :userIds or CAST(NULLIF(job_submission_data->>'accountOwnerId', '') as INTEGER) in :userIds) and CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Active' AND ");
				break;
			}
			case "active_jobs": {
				queryString = queryString.replace("{1}",
						"id not in (select distinct(fod.job_id) from job_recruiter_fod fod where (fod.recruiter_id IN ("
								+ userId
								+ "))) and CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Active' AND ");
				break;
			}
			case "inactive_jobs": {
				isActive = false;
				queryString = queryString.replace("{1}",
						"(created_by IN :userIds or CAST(NULLIF(job_submission_data->>'accountOwnerId', '') as INTEGER) in :userIds or id in (select distinct(fod.job_id) from job_recruiter_fod fod where (fod.recruiter_id IN :userIds or fod.sales_id IN :userIds))) and CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Inactive' AND ");
				break;
			}
			case "closed_jobs": {
				queryString = queryString.replace("{1}",
						"(created_by IN :userIds or CAST(NULLIF(job_submission_data->>'accountOwnerId', '') as INTEGER) in :userIds or id in (select distinct(fod.job_id) from job_recruiter_fod fod where (fod.recruiter_id IN :userIds or fod.sales_id IN :userIds))) and CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Closed' AND ");
				break;
			}
			case "fod": {
				queryString = queryString.replace("{1}",
						"id in (select distinct(fod.job_id) from job_recruiter_fod fod where (fod.recruiter_id IN :userIds or fod.sales_id IN :userIds) and fod.updated_at >= current_date) and CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Active' AND ");
				break;
			}
			case "assigned_jobs": {
				queryString = queryString.replace("{1}",
						"id in (select distinct(fod.job_id) from job_recruiter_fod fod where (fod.recruiter_id IN :userIds or fod.sales_id IN :userIds)) and CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Active' AND ");
				break;
			}
			case "all_jobs": {
				queryString = queryString.replace("{1}",
						"(created_by IN :userIds or CAST(NULLIF(job_submission_data->>'accountOwnerId', '') as INTEGER) in :userIds or id in (select distinct(fod.job_id) from job_recruiter_fod fod where (fod.recruiter_id IN :userIds or fod.sales_id IN :userIds))) AND ");
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
						"id not in (select distinct(fod.job_id) from job_recruiter_fod fod) and is_active = true and is_deleted = false and CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Active' AND ");
				break;
			}
			case "active_jobs": {
				queryString = queryString.replace("{1}",
						"CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Active' AND ");
				break;
			}
			case "inactive_jobs": {
				isActive = false;
				queryString = queryString.replace("{1}",
						"CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Inactive' AND ");
				break;
			}
			case "closed_jobs": {
				queryString = queryString.replace("{1}",
						"CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Closed' AND ");
				break;
			}
			case "fod": {
				queryString = queryString.replace("{1}",
						"id in (select distinct(fod.job_id) from job_recruiter_fod fod where fod.updated_at >= current_date) and CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Active' AND ");
				break;
			}
			case "assigned_jobs": {
				queryString = queryString.replace("{1}",
						"id in (select distinct(fod.job_id) from job_recruiter_fod fod) and CAST(NULLIF(job_submission_data->>'jobStatus', '') as TEXT) = 'Active' AND ");
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

	@Override
	public void insertVector(Long jobId, String columnName, List<Float> vector) {
		// Convert your List<Float> to the format expected by the database for the
		// vector type
		String vectorString = vector.stream().map(Object::toString).collect(Collectors.joining(",", "[", "]")); // Note
																												// the
																												// change
																												// to
																												// square
																												// brackets

		// Prepare your SQL query, ensuring the casting and formatting align with your
		// database's requirements
		String sql = "INSERT INTO job (id, :columnName) VALUES (:id, CAST(:vectorText AS vector))"; // Adjust as
																									// necessary

		// Execute the native query with parameters, ensuring the correct format is
		// applied
		entityManager.createNativeQuery(sql).setParameter("id", jobId).setParameter("columnName", columnName)
				.setParameter("vectorText", vectorString) // The vector is now correctly formatted
				.executeUpdate();
	}

	@Override
	@Transactional
	public void updateVector(Long jobId, String columnName, List<Float> vector) {
		// Convert your List<Float> to the format expected by the database for the
		// vector type
		String vectorString = vector.stream().map(Object::toString).collect(Collectors.joining(",", "[", "]")); // Note
																												// the
																												// change
																												// to
																												// square
																												// brackets

		// Prepare your SQL query, ensuring the casting and formatting align with your
		// database's requirements
		// Note the change to the SQL statement for updating instead of inserting
		String sql = "UPDATE job SET " + columnName + " = CAST(:vectorText AS vector) WHERE id = :id";

		// Execute the native query with parameters, ensuring the correct format is
		// applied
		entityManager.createNativeQuery(sql).setParameter("id", jobId).setParameter("vectorText", vectorString) // The
																												// vector
																												// is
																												// now
																												// correctly
																												// formatted
				.executeUpdate();
	}

	@Override
	public Optional<List<Float>> getEmbeddingsById(Long jobId, String columnName) {
		// Prepare your SQL query to retrieve the vector from the database
		String sql = "SELECT " + columnName + " FROM job WHERE id = :id";

		// Execute the native query with the job ID parameter
		Object result = entityManager.createNativeQuery(sql).setParameter("id", jobId).getSingleResult();

		// If the result is null, return an empty Optional
		if (result == null) {
			return Optional.empty();
		}

		// Convert the result to a String, assuming it's not null here
		String vectorString = result.toString();
		// If the vector string is null or empty, return an empty Optional
		if (vectorString == null || vectorString.isEmpty()) {
			return Optional.empty();
		}

		// Remove the brackets, split by commas, and convert each element to Float
		List<Float> vector = Arrays.stream(vectorString.replace("[", "").replace("]", "").split(","))
				.map(Float::valueOf).collect(Collectors.toList());

		// Wrap the list in an Optional and return
		return Optional.of(vector);
	}

	@Override
	public List<JobEntity> findAllByEmbeddingIsNull() {
		String queryString = "SELECT * FROM job WHERE job_embeddings IS NULL AND is_deleted = false AND is_draft = false AND is_active = true";
		Query query = entityManager.createNativeQuery(queryString, JobEntity.class);
		return query.getResultList();
	}

}