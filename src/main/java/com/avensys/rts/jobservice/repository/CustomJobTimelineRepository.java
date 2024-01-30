package com.avensys.rts.jobservice.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.avensys.rts.jobservice.entity.JobTimelineEntity;

public interface CustomJobTimelineRepository {

	// Check only user id
	Page<JobTimelineEntity> findAllByOrderByStringWithUserIds(List<Long> userIds, Boolean isDeleted, Boolean isActive,
			Pageable pageable, Long userId);

	Page<JobTimelineEntity> findAllByOrderByNumericWithUserIds(List<Long> userIds, Boolean isDeleted, Boolean isActive,
			Pageable pageable, Long userId);

	Page<JobTimelineEntity> findAllByOrderByAndSearchStringWithUserIds(List<Long> userIds, Boolean isDeleted,
			Boolean isActive, Pageable pageable, List<String> searchFields, String searchTerm, Long userId);

	Page<JobTimelineEntity> findAllByOrderByAndSearchNumericWithUserIds(List<Long> userIds, Boolean isDeleted,
			Boolean isActive, Pageable pageable, List<String> searchFields, String searchTerm, Long userId);
}
