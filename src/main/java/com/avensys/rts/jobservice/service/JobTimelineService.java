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

import com.avensys.rts.jobservice.apiclient.UserAPIClient;
import com.avensys.rts.jobservice.entity.JobTimelineEntity;
import com.avensys.rts.jobservice.exception.ServiceException;
import com.avensys.rts.jobservice.repository.JobTimelineRepository;
import com.avensys.rts.jobservice.response.HttpResponse;
import com.avensys.rts.jobservice.response.JobTimelineResponseDTO;
import com.avensys.rts.jobservice.response.UserResponseDTO;
import com.avensys.rts.jobservice.util.MappingUtil;
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
	private UserAPIClient userAPIClient;

	@Autowired
	private MessageSource messageSource;

	public JobTimelineResponseDTO getJobTimelineListingPage(Integer page, Integer size, String sortBy,
			String sortDirection, Long userId, Long jobId) {
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
					userUtil.getUsersIdUnderManager(), false, true, pageRequest, userId, jobId);
		} catch (Exception e) {
			e.printStackTrace();
			jobEntitiesPage = jobTimelineRepository.findAllByOrderByStringWithUserIds(userUtil.getUsersIdUnderManager(),
					false, true, pageRequest, userId, jobId);
		}

		return pageJobListingToJobListingResponseDTO(jobEntitiesPage);
	}

	public JobTimelineResponseDTO getJobTimelineListingPageWithSearch(Integer page, Integer size, String sortBy,
			String sortDirection, String searchTerm, List<String> searchFields, Long userId, Long jobId) {
		// Get sort direction
		Sort.Direction direction = Sort.DEFAULT_DIRECTION;
		if (sortDirection != null) {
			direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
		}
		if (sortBy == null) {
			sortBy = "job_timeline.updated_at";
			direction = Sort.Direction.DESC;
		}
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

		Page<JobTimelineEntity> JobTimelineEntityPage = null;
		// Try with numeric first else try with string (jsonb)
		try {
			JobTimelineEntityPage = jobTimelineRepository.findAllByOrderByAndSearchNumericWithUserIds(
					userUtil.getUsersIdUnderManager(), false, true, pageRequest, searchFields, searchTerm, userId,
					jobId);
		} catch (Exception e) {
			JobTimelineEntityPage = jobTimelineRepository.findAllByOrderByAndSearchStringWithUserIds(
					userUtil.getUsersIdUnderManager(), false, true, pageRequest, searchFields, searchTerm, userId,
					jobId);
		}

		return pageJobListingToJobListingResponseDTO(JobTimelineEntityPage);
	}

	@Transactional
	public List<Map<String, Long>> getJobTimelineCount(Long jobId) throws ServiceException {
		if (jobId == null) {
			throw new ServiceException(messageSource.getMessage("error.provide.id", new Object[] { jobId },
					LocaleContextHolder.getLocale()));
		}

		List<Map<String, Long>> data = jobTimelineRepository.findJobTimelineCount(jobId);

		if (data != null) {
			try {
				Map<String, Long> interviewScheduledCount = jobTimelineRepository.findInterviewScheduledCount(jobId);
				Map<String, Long> interviewHappenedCount = jobTimelineRepository.findInterviewHappenedCount(jobId);
				Map<String, Long> interviewCancelledCount = jobTimelineRepository.findInterviewCancelledCount(jobId);
				Map<String, Long> interviewFeedbackPendingCount = jobTimelineRepository
						.findInterviewFeedbackPendingCount(jobId);

				data.add(interviewScheduledCount);
				data.add(interviewHappenedCount);
				data.add(interviewCancelledCount);
				data.add(interviewFeedbackPendingCount);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return data;
		} else {
			throw new ServiceException(messageSource.getMessage("error.jobnotfound", new Object[] { jobId },
					LocaleContextHolder.getLocale()));
		}
	}

	private JobTimelineResponseDTO pageJobListingToJobListingResponseDTO(
			Page<JobTimelineEntity> jobTimelineEntityPage) {
		JobTimelineResponseDTO jobListingNewResponseDTO = new JobTimelineResponseDTO();
		jobListingNewResponseDTO.setTotalPages(jobTimelineEntityPage.getTotalPages());
		jobListingNewResponseDTO.setTotalElements(jobTimelineEntityPage.getTotalElements());
		jobListingNewResponseDTO.setPage(jobTimelineEntityPage.getNumber());
		jobListingNewResponseDTO.setPageSize(jobTimelineEntityPage.getSize());
		List<JobTimelineEntity> list = jobTimelineEntityPage.getContent().stream().map(jobEntity -> {

			// Get created by User data from user service
			// Cast to Integer
			HttpResponse userResponse = userAPIClient.getUserById(jobEntity.getCreatedBy().intValue());
			UserResponseDTO userData = MappingUtil.mapClientBodyToClass(userResponse.getData(), UserResponseDTO.class);
			jobEntity.setCreatedByName(userData.getFirstName() + " " + userData.getLastName());

			// Get updated by User data from user service
			HttpResponse updatedByUserResponse = userAPIClient.getUserById(jobEntity.getUpdatedBy().intValue());
			UserResponseDTO updatedByUserData = MappingUtil.mapClientBodyToClass(updatedByUserResponse.getData(),
					UserResponseDTO.class);
			jobEntity.setUpdatedByName(updatedByUserData.getFirstName() + " " + updatedByUserData.getLastName());
			return jobEntity;
		}).toList();
		jobListingNewResponseDTO.setJobs(list);
		return jobListingNewResponseDTO;
	}

}
