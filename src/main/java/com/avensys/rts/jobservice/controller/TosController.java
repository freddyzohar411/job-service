package com.avensys.rts.jobservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.avensys.rts.jobservice.service.TosService;
import com.avensys.rts.jobservice.util.ResponseUtil;

@CrossOrigin
@RestController
@RequestMapping("/api/tos")
public class TosController {
	@Autowired
	private TosService tosService;

	@Autowired
	private MessageSource messageSource;

	@GetMapping("/jobs/{jobId}/candidates/{candidateId}/status/{status}")
	public ResponseEntity<?> getTosWithJobIdAndCandidateIdAndStatus(@PathVariable Long jobId, @PathVariable Long candidateId, @PathVariable String status) {
		return ResponseUtil.generateSuccessResponse(tosService.getTosEntity(jobId, candidateId, status), HttpStatus.CREATED,
				messageSource.getMessage("job.tag.created", null, LocaleContextHolder.getLocale()));
	}

}
