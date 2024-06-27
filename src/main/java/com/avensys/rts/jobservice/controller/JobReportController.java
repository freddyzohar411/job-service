package com.avensys.rts.jobservice.controller;

import com.avensys.rts.jobservice.constant.MessageConstants;
import com.avensys.rts.jobservice.exception.ServiceException;
import com.avensys.rts.jobservice.payload.JobCandidateStageRequest;
import com.avensys.rts.jobservice.payload.JobListingRequestDTO;
import com.avensys.rts.jobservice.response.JobReportCountsResponseDTO;
import com.avensys.rts.jobservice.service.JobCandidateStageService;
import com.avensys.rts.jobservice.service.JobReportService;
import com.avensys.rts.jobservice.util.JwtUtil;
import com.avensys.rts.jobservice.util.ResponseUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/jobreports")
public class JobReportController {

	private static final Logger LOG = LoggerFactory.getLogger(JobCandidateStageController.class);

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private JobReportService jobReportService;

	@GetMapping("/get-counts")
	public ResponseEntity<?> create() throws ServiceException {
		LOG.info("Get job report counts request received");
		JobReportCountsResponseDTO jobReportCountsResponseDTO = jobReportService.getJobReportCounts();
		return ResponseUtil.generateSuccessResponse(jobReportCountsResponseDTO, HttpStatus.CREATED,
				messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));

	}

	@PostMapping("/listing")
	public ResponseEntity<Object> getJobListing(@RequestBody JobListingRequestDTO jobListingRequestDTO,
			@RequestHeader(name = "Authorization") String token) {
		Long userId = jwtUtil.getUserId(token);
		Long jobId = jobListingRequestDTO.getJobId();
		Integer page = jobListingRequestDTO.getPage();
		Integer pageSize = jobListingRequestDTO.getPageSize();
		String sortBy = jobListingRequestDTO.getSortBy();
		String sortDirection = jobListingRequestDTO.getSortDirection();
		String searchTerm = jobListingRequestDTO.getSearchTerm();
		Integer stageType = null;
		Boolean isAdmin = true;
		List<String> searchFields = jobListingRequestDTO.getSearchFields();
		if (searchTerm == null || searchTerm.isEmpty()) {
			return ResponseUtil.generateSuccessResponse(
					jobReportService.getJobTimelineListingPage(page, pageSize, sortBy, sortDirection, userId, jobId,
							isAdmin, stageType),
					HttpStatus.OK,
					messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
		}
		return null;
	}


}
