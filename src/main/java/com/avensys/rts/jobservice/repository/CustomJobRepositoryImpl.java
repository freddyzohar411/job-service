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
				"SELECT * FROM job WHERE created_by = :userId AND is_deleted = :isDeleted AND is_active = :isActive ORDER BY %s %s NULLS LAST",
				orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobEntity.class);
		query.setParameter("userId", userId);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = "SELECT COUNT(*) FROM job WHERE created_by = :userId AND is_deleted = :isDeleted AND is_active = :isActive";

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("userId", userId);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
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
				"SELECT * FROM job WHERE created_by = :userId AND is_deleted = :isDeleted AND is_active = :isActive ORDER BY %s %s NULLS LAST",
				orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobEntity.class);
		query.setParameter("userId", userId);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = "SELECT COUNT(*) FROM job WHERE created_by = :userId AND is_deleted = :isDeleted AND is_active = :isActive";

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("userId", userId);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
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
				"SELECT * FROM job WHERE created_by = :userId AND is_deleted = :isDeleted AND is_active = :isActive ORDER BY %s %s NULLS LAST",
				orderByClause, sortDirection);

		// Log the generated SQL (for debugging)
		System.out.println(queryString);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobEntity.class);
		query.setParameter("userId", userId);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = "SELECT COUNT(*) FROM job WHERE created_by = :userId AND is_deleted = :isDeleted AND is_active = :isActive";

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("userId", userId);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
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
				"SELECT * FROM job WHERE created_by = :userId AND is_deleted = :isDeleted AND is_active = :isActive AND (%s) ORDER BY %s %s NULLS LAST",
				searchConditions.toString(), orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobEntity.class);
		query.setParameter("userId", userId);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM job WHERE created_by = :userId AND is_deleted = :isDeleted AND is_active = :isActive AND (%s)",
				searchConditions.toString());

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("userId", userId);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
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
				"SELECT * FROM job WHERE created_by = :userId AND is_deleted = :isDeleted AND is_active = :isActive AND (%s) ORDER BY %s %s NULLS LAST",
				searchConditions.toString(), orderByClause, sortDirection);

		// Create and execute the query
		Query query = entityManager.createNativeQuery(queryString, JobEntity.class);
		query.setParameter("userId", userId);
		query.setParameter("isDeleted", isDeleted);
		query.setParameter("isActive", isActive);
		query.setParameter("searchTerm", "%" + searchTerm + "%");
		query.setFirstResult((int) pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		// Get the result list
		List<JobEntity> resultList = query.getResultList();

		// Build the count query string
		String countQueryString = String.format(
				"SELECT COUNT(*) FROM job WHERE created_by = :userId AND is_deleted = :isDeleted AND is_active = :isActive AND (%s)",
				searchConditions.toString());

		// Create and execute the count query
		Query countQuery = entityManager.createNativeQuery(countQueryString);
		countQuery.setParameter("userId", userId);
		countQuery.setParameter("isDeleted", isDeleted);
		countQuery.setParameter("isActive", isActive);
		countQuery.setParameter("searchTerm", "%" + searchTerm + "%");
		Long countResult = ((Number) countQuery.getSingleResult()).longValue();

		// Create and return a Page object
		return new PageImpl<>(resultList, pageable, countResult);
	}

	@Override
	public List<JobEntity> getAllAccountsNameWithSearch(String query, Long userId, Boolean isDeleted, Boolean isDraft) {
		StringBuilder sql = new StringBuilder(
				"SELECT * FROM job WHERE created_by = :userId AND is_deleted = :isDeleted");

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

}