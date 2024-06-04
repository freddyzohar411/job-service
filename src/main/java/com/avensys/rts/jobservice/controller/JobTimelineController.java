package com.avensys.rts.jobservice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.avensys.rts.jobservice.annotation.RequiresAllPermissions;
import com.avensys.rts.jobservice.constant.MessageConstants;
import com.avensys.rts.jobservice.enums.Permission;
import com.avensys.rts.jobservice.exception.ServiceException;
import com.avensys.rts.jobservice.payload.JobListingRequestDTO;
import com.avensys.rts.jobservice.service.JobTimelineService;
import com.avensys.rts.jobservice.util.JwtUtil;
import com.avensys.rts.jobservice.util.ResponseUtil;
import com.avensys.rts.jobservice.util.UserUtil;

/**
 * @author Rahul Sahu This class used to get/save/update/delete job timeline
 *         operations
 * 
 */
@CrossOrigin
@RestController
@RequestMapping("/api/jobtimeline")
public class JobTimelineController {

	@Autowired
	private JobTimelineService jobTimelineService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private UserUtil userUtil;

	@RequiresAllPermissions({ Permission.JOB_READ })
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
		Boolean isAdmin = jobListingRequestDTO.getAllActive() ? jobListingRequestDTO.getAllActive()
				: userUtil.checkIsAdmin();
		List<String> searchFields = jobListingRequestDTO.getSearchFields();
		if (searchTerm == null || searchTerm.isEmpty()) {
			return ResponseUtil.generateSuccessResponse(
					jobTimelineService.getJobTimelineListingPage(page, pageSize, sortBy, sortDirection, userId, jobId,
							isAdmin),
					HttpStatus.OK,
					messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
		} else {
			return ResponseUtil.generateSuccessResponse(
					jobTimelineService.getJobTimelineListingPageWithSearch(page, pageSize, sortBy, sortDirection,
							searchTerm, searchFields, userId, jobId, isAdmin),
					HttpStatus.OK,
					messageSource.getMessage(MessageConstants.MESSAGE_SUCCESS, null, LocaleContextHolder.getLocale()));
		}
	}

	@GetMapping("/jobtimelinecount/{jobId}")
	public ResponseEntity<?> jobTimelineCount(@PathVariable Long jobId) {
		try {
			Boolean isAdmin = true;
			List<Map<String, Long>> response = jobTimelineService.getJobTimelineCount(jobId, isAdmin);
			return ResponseUtil.generateSuccessResponse(response, HttpStatus.OK, null);
		} catch (ServiceException e) {
			return ResponseUtil.generateSuccessResponse(null, HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

}
