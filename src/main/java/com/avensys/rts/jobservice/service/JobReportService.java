package com.avensys.rts.jobservice.service;

import com.avensys.rts.jobservice.apiclient.UserAPIClient;
import com.avensys.rts.jobservice.entity.JobTimelineEntity;
import com.avensys.rts.jobservice.exception.ServiceException;
import com.avensys.rts.jobservice.repository.JobCandidateStageRepository;
import com.avensys.rts.jobservice.repository.JobRecruiterFODRepository;
import com.avensys.rts.jobservice.repository.JobTimelineRepository;
import com.avensys.rts.jobservice.response.HttpResponse;
import com.avensys.rts.jobservice.response.JobReportCountsResponseDTO;
import com.avensys.rts.jobservice.response.JobTimelineResponseDTO;
import com.avensys.rts.jobservice.response.UserResponseDTO;
import com.avensys.rts.jobservice.util.JobCanddateStageUtil;
import com.avensys.rts.jobservice.util.MappingUtil;
import com.avensys.rts.jobservice.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JobReportService {

	@Autowired
	JobRecruiterFODRepository jobRecruiterFODRepository;

	@Autowired
	JobCandidateStageRepository jobCandidateStageRepository;

	@Autowired
	JobTimelineRepository jobTimelineRepository;

	@Autowired
	private UserUtil userUtil;

	@Autowired
	private UserAPIClient userAPIClient;

	@Autowired
	private MessageSource messageSource;

	public JobReportCountsResponseDTO getJobReportCounts() throws ServiceException {
		Boolean getAll = true;
		Integer newJobsCount = getNewJobsCount(getAll);
		Integer activeJobsCount = getActiveJobsCount(getAll);
		// Associated
		Long associatedCount = jobCandidateStageRepository.findActiveJobsByStageName(JobCanddateStageUtil.ASSOCIATE);
		// Submit to Sales
		Long submitToSalesCount = jobCandidateStageRepository
				.findActiveJobsByStageName(JobCanddateStageUtil.SUBMIT_TO_SALES);
		// Submit to Client
		Long submitToClientCount = jobCandidateStageRepository
				.findActiveJobsByStageName(JobCanddateStageUtil.SUBMIT_TO_CLIENT);
		// Find Selected count
		Long selectedCount = jobCandidateStageRepository.findActiveJobsByStageNameAndStatus(
				JobCanddateStageUtil.CONDITIONAL_OFFER_ACCEPTED_OR_DECLINED, JobCanddateStageUtil.COMPLETED);

		// Find Rejected count
		Long rejectedCount = jobCandidateStageRepository.findActiveJobByStatus(JobCanddateStageUtil.REJECTED);

		JobReportCountsResponseDTO jobReportCountsResponseDTO = new JobReportCountsResponseDTO();
		jobReportCountsResponseDTO.setNewRequirementsCount(newJobsCount.longValue());
		jobReportCountsResponseDTO.setActiveRequirementsCount(activeJobsCount.longValue());
		jobReportCountsResponseDTO.setAssociatedCount(associatedCount);
		jobReportCountsResponseDTO.setSubmitToSalesCount(submitToSalesCount);
		jobReportCountsResponseDTO.setSubmitToClientCount(submitToClientCount);
		jobReportCountsResponseDTO.setSelectedCount(selectedCount);
		jobReportCountsResponseDTO.setRejectedCount(rejectedCount);
		return jobReportCountsResponseDTO;
	}

	public JobTimelineResponseDTO getJobTimelineListingPage(Integer page, Integer size, String sortBy,
			String sortDirection, Long userId, Long jobId, Boolean isAdmin, Integer stageType) {
		// Get sort direction
		Sort.Direction direction = Sort.DEFAULT_DIRECTION;
		if (sortDirection != null && !sortDirection.isEmpty()) {
			direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
		}
		if (sortBy == null || sortBy.isEmpty() || sortBy.equals("")) {
			sortBy = "updated_at";
			direction = Sort.Direction.DESC;
		}

		// User condition
		List<Long> userIds = new ArrayList<>();
		PageRequest pageRequest = PageRequest.of(page, size, Sort.by(direction, sortBy));

		Page<JobTimelineEntity> jobEntitiesPage = null;
		// Try with numeric first else try with string (jsonb)
		try {
			jobEntitiesPage = jobTimelineRepository.findAllByOrderByNumericWithUserIdsReport(userIds, false, true,
					pageRequest, userId, "Active");
		} catch (Exception e) {
			e.printStackTrace();
			jobEntitiesPage = jobTimelineRepository.findAllByOrderByStringWithUserIdsReport(userIds, false, true,
					pageRequest, userId, "Active");
		}

		return pageJobListingToJobListingResponseDTO(jobEntitiesPage);
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

	private Integer getNewJobsCount(Boolean getAll) throws ServiceException {
		try {
			List<Long> userIds = new ArrayList<>();
			Optional<Integer> entityOptional = Optional.empty();
			if (getAll) {
				entityOptional = jobRecruiterFODRepository.getNewJobsCountAll();
			} else {
				entityOptional = jobRecruiterFODRepository.getNewJobsCount(userIds);
			}
			if (entityOptional.isPresent()) {
				return entityOptional.get();
			} else {
				return 0;
			}
		} catch (Exception e) {
			throw new ServiceException(e.getLocalizedMessage());
		}
	}

	private Integer getActiveJobsCount(Boolean getAll) throws ServiceException {
		try {
			List<Long> userIds = new ArrayList<>();
			Optional<Integer> entityOptional = Optional.empty();
			if (getAll) {
				entityOptional = jobRecruiterFODRepository.getActiveJobsCountAll();
			} else {
				entityOptional = jobRecruiterFODRepository.getActiveJobsCount(userIds);
			}
			if (entityOptional.isPresent()) {
				return entityOptional.get();
			} else {
				return 0;
			}
		} catch (Exception e) {
			throw new ServiceException(e.getLocalizedMessage());
		}
	}
}
