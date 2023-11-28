package com.avensys.rts.jobservice.service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.avensys.rts.jobservice.apiclient.DocumentAPIClient;
import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.exception.ServiceException;
import com.avensys.rts.jobservice.payload.DocumentRequestDTO;
import com.avensys.rts.jobservice.payload.JobRequest;
import com.avensys.rts.jobservice.repository.JobRepository;
import com.avensys.rts.jobservice.response.HttpResponse;
import com.avensys.rts.jobservice.search.job.JobSpecificationBuilder;

import jakarta.transaction.Transactional;

/**
 * @author Rahul Sahu
 * 
 */
@Service
public class JobService {

	private static final Logger LOG = LoggerFactory.getLogger(JobService.class);
	private static final String JOB_TYPE = "job";

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private DocumentAPIClient documentAPIClient;

	/**
	 * This method is used to convert JobRequest to JobEntity
	 * 
	 * @param jobRequest
	 * @param jobEntity
	 * @return
	 */
	private JobEntity mapRequestToEntity(JobRequest jobRequest) {
		JobEntity entity = new JobEntity();
		if (jobRequest.getId() != null) {
			entity.setId(jobRequest.getId());
		}
		entity.setTitle(jobRequest.getTitle());
		entity.setCreatedBy(jobRequest.getCreatedBy());
		entity.setUpdatedBy(jobRequest.getUpdatedBy());
		entity.setFormSubmissionData(jobRequest.getFormData());
		entity.setIsActive(true);
		entity.setIsDeleted(false);
		return entity;
	}

	/**
	 * This method is used to save job Need to implement roll back if error occurs.
	 * 
	 * @param jobRequest
	 * @return
	 */
	@Transactional
	public JobEntity save(JobRequest jobRequest) throws ServiceException {
		// add check for title exists in a DB
		if (jobRepository.existsByTitle(jobRequest.getTitle())) {
			throw new ServiceException(
					messageSource.getMessage("error.jobtitletaken", null, LocaleContextHolder.getLocale()));
		}

		JobEntity jobEntity = mapRequestToEntity(jobRequest);

		jobEntity = jobRepository.save(jobEntity);

		if (jobRequest.getUploadJobDocuments() != null) {
			DocumentRequestDTO documentRequestDTO = new DocumentRequestDTO();
			// Save document and tag to account entity
			documentRequestDTO.setEntityId(jobEntity.getId());
			documentRequestDTO.setEntityType(JOB_TYPE);

			documentRequestDTO.setFile(jobRequest.getUploadJobDocuments());
			HttpResponse documentResponse = documentAPIClient.createDocument(documentRequestDTO);
			if (documentResponse.getCode() != 200) {
				throw new ServiceException(
						messageSource.getMessage("error.jobtitletaken", null, LocaleContextHolder.getLocale()));
			}
		}
		return jobEntity;
	}

	/**
	 * This method is used to update a job
	 * 
	 * @param id
	 * @param jobRequest
	 * @return
	 */
	public void update(JobRequest jobRequest) throws ServiceException {

		Optional<JobEntity> dbJob = jobRepository.findByTitle(jobRequest.getTitle());

		// add check for title exists in a DB
		if (dbJob.isPresent() && dbJob.get().getId() != jobRequest.getId()) {
			throw new ServiceException(
					messageSource.getMessage("error.jobtitletaken", null, LocaleContextHolder.getLocale()));
		}
		JobEntity jobEntity = getById(jobRequest.getId());
		jobEntity.setTitle(jobRequest.getTitle());
		jobEntity.setFormSubmissionData(jobRequest.getFormData());
		jobEntity.setUpdatedBy(jobRequest.getUpdatedBy());
		LOG.info("Job updated : Service");
		jobRepository.save(jobEntity);
	}

	/**
	 * This method is used to retrieve a job Information
	 * 
	 * @param id
	 * @return
	 */
	public JobEntity getById(Long id) throws ServiceException {
		if (id == null) {
			throw new ServiceException(
					messageSource.getMessage("error.provide.id", new Object[] { id }, LocaleContextHolder.getLocale()));
		}

		Optional<JobEntity> permission = jobRepository.findById(id);
		if (permission.isPresent() && !permission.get().getIsDeleted()) {
			return permission.get();
		} else {
			throw new ServiceException(messageSource.getMessage("error.jobnotfound", new Object[] { id },
					LocaleContextHolder.getLocale()));
		}
	}

	/**
	 * This method is used to Delete a job
	 * 
	 * @param id
	 */
	public void delete(Long id) throws ServiceException {
		JobEntity dbJob = getById(id);
		dbJob.setIsDeleted(true);
		jobRepository.save(dbJob);
	}

	public List<JobEntity> getAllJobs(Integer pageNo, Integer pageSize, String sortBy) throws ServiceException {
		LOG.info("getAllJobs request processing");
		try {
			Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
			List<JobEntity> jobEntityList = jobRepository.findAllAndIsDeleted(false, paging);
			LOG.info("jobEntityList retrieved : Service");
			return jobEntityList;
		} catch (Exception e) {
			throw new ServiceException(e.getLocalizedMessage());
		}
	}

	public Page<JobEntity> search(String searchTerms, Pageable pageable) throws ServiceException {
		LOG.info("search request processing");
		try {
			JobSpecificationBuilder builder = new JobSpecificationBuilder();
			Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
			Matcher matcher = pattern.matcher(searchTerms + ",");

			while (matcher.find()) {
				builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
			}

			return jobRepository.findAll(builder.build(), pageable);
		} catch (Exception e) {
			throw new ServiceException(e.getLocalizedMessage());
		}
	}
}
