package com.avensys.rts.jobservice.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.avensys.rts.jobservice.entity.JobTimelineEntity;
import com.avensys.rts.jobservice.exception.ServiceException;
import com.avensys.rts.jobservice.repository.JobTimelineRepository;
import com.avensys.rts.jobservice.response.JobTimelineResponseDTO;
import com.avensys.rts.jobservice.util.UserUtil;

import jakarta.transaction.Transactional;

/**
 * @author Rahul Sahu
 * 
 */
@Service
public class JobTimelineService {

	@Autowired
	private JobTimelineRepository jobTimelineRepository;

	@Autowired
	private UserUtil userUtil;

	@Autowired
	private MessageSource messageSource;

	public JobTimelineResponseDTO getJobTimelineListingPage(Integer page, Integer size, String sortBy,
			String sortDirection, Long userId) {
		// Get sort direction
		Sort.Direction direction = Sort.DEFAULT_DIRECTION;
		if (sortDirection != null && !sortDirection.isEmpty()) {
			direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
		}
		if (sortBy == null || sortBy.isEmpty() || sortBy.equals("")) {
			sortBy = "updated_at";
			direction = Sort.Direction.DESC;
		}
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

		Page<JobTimelineEntity> jobEntitiesPage = null;
		// Try with numeric first else try with string (jsonb)
		try {
			jobEntitiesPage = jobTimelineRepository.findAllByOrderByNumericWithUserIds(
					userUtil.getUsersIdUnderManager(), false, true, pageRequest, userId);
		} catch (Exception e) {
			jobEntitiesPage = jobTimelineRepository.findAllByOrderByStringWithUserIds(userUtil.getUsersIdUnderManager(),
					false, true, pageRequest, userId);
		}

		return pageJobListingToJobListingResponseDTO(jobEntitiesPage);
	}

	public JobTimelineResponseDTO getJobTimelineListingPageWithSearch(Integer page, Integer size, String sortBy,
			String sortDirection, String searchTerm, List<String> searchFields, Long userId) {
		// Get sort direction
		Sort.Direction direction = Sort.DEFAULT_DIRECTION;
		if (sortDirection != null) {
			direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
		}
		if (sortBy == null) {
			sortBy = "updated_at";
			direction = Sort.Direction.DESC;
		}
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

		Page<JobTimelineEntity> JobTimelineEntityPage = null;
		// Try with numeric first else try with string (jsonb)
		try {
			JobTimelineEntityPage = jobTimelineRepository.findAllByOrderByAndSearchNumericWithUserIds(
					userUtil.getUsersIdUnderManager(), false, true, pageRequest, searchFields, searchTerm, userId);
		} catch (Exception e) {
			JobTimelineEntityPage = jobTimelineRepository.findAllByOrderByAndSearchStringWithUserIds(
					userUtil.getUsersIdUnderManager(), false, true, pageRequest, searchFields, searchTerm, userId);
		}

		return pageJobListingToJobListingResponseDTO(JobTimelineEntityPage);
	}

	private JobTimelineResponseDTO pageJobListingToJobListingResponseDTO(
			Page<JobTimelineEntity> jobTimelineEntityPage) {
		JobTimelineResponseDTO jobListingNewResponseDTO = new JobTimelineResponseDTO();
		jobListingNewResponseDTO.setTotalPages(jobTimelineEntityPage.getTotalPages());
		jobListingNewResponseDTO.setTotalElements(jobTimelineEntityPage.getTotalElements());
		jobListingNewResponseDTO.setPage(jobTimelineEntityPage.getNumber());
		jobListingNewResponseDTO.setPageSize(jobTimelineEntityPage.getSize());
		jobListingNewResponseDTO.setJobs(jobTimelineEntityPage.getContent());
		return jobListingNewResponseDTO;
	}

	@Transactional
	public List<Map<String, Long>> getJobTimelineCount(Long jobId) throws ServiceException {
		if (jobId == null) {
			throw new ServiceException(messageSource.getMessage("error.provide.id", new Object[] { jobId },
					LocaleContextHolder.getLocale()));
		}

		List<Map<String, Long>> data = jobTimelineRepository.findJobTimelineCount(jobId);
		if (data != null) {
			return data;
		} else {
			throw new ServiceException(messageSource.getMessage("error.jobnotfound", new Object[] { jobId },
					LocaleContextHolder.getLocale()));
		}
	}

}
