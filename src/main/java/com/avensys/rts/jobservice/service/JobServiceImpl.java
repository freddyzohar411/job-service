package com.avensys.rts.jobservice.service;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.rowset.serial.SerialException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.avensys.rts.jobservice.apiclient.DocumentAPIClient;
import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.payloadrequest.DocumentRequestDTO;
import com.avensys.rts.jobservice.payloadrequest.JobRequest;
import com.avensys.rts.jobservice.payloadresponse.HttpResponse;
import com.avensys.rts.jobservice.repository.JobRepository;
import com.avensys.rts.jobservice.search.job.JobSpecificationBuilder;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;


/**
 * @author Kotaiah nalleboina
 * This class is used to implement the JobService interface and perform CRUD operations
 */
@Service
public class JobServiceImpl implements JobService {
	
	private static final Logger LOG = LoggerFactory.getLogger(JobServiceImpl.class);
	private static final String JOB_TYPE = "job";
	@Autowired
	private JobRepository jobRepository;
	@Autowired
	private DocumentAPIClient documentAPIClient;
	
	 /**
     * This method is used to save job
     * Need to implement roll back if error occurs.
     * @param jobRequest
     * @return
     */
	@Override
	@Transactional
	public JobEntity createJob(JobRequest jobRequest) {
		JobEntity jobEntity = jobRepository.save(mapRequestToEntity(jobRequest, null));
		if(jobRequest.getUploadJobDocuments() != null) {
			 DocumentRequestDTO documentRequestDTO = new DocumentRequestDTO();
            // Save document and tag to account entity
            documentRequestDTO.setEntityId(jobEntity.getId());
            documentRequestDTO.setEntityType(JOB_TYPE);

            documentRequestDTO.setFile(jobRequest.getUploadJobDocuments());
            HttpResponse documentResponse = documentAPIClient.createDocument(documentRequestDTO);
		}
		return jobEntity;
	}
	
	/**
	 * This method is used to retrieve a job Information
	 * @param id
	 * @return
	 */
	@Override
	public JobEntity getJob(Integer id) {
		JobEntity jobEntity = jobRepository.findByIdAndIsDeleted(id, false).orElseThrow(
                () -> new EntityNotFoundException("Job with %s not found".formatted(id))
        );
		LOG.info("Job retrieved : Service");
		return jobEntity;
	}
	
	/**
	 * This method is used to update a job
	 * @param id
	 * @param jobRequest
	 * @return
	 */
	@Override
	public JobEntity updateJob(Integer id, JobRequest jobRequest) {
		JobEntity jobEntity = jobRepository.findByIdAndIsDeleted(id, false).orElseThrow(
                () -> new EntityNotFoundException("Job with %s not found".formatted(id))
        );
		jobEntity = mapRequestToEntity(jobRequest, jobEntity);
		LOG.info("Job updated : Service");
		return jobRepository.save(jobEntity);
	}
	
	/**
	 * This method is used to Delete a job
	 * @param id
	 */
	@Override
	public void deleteJob(Integer id) {
		JobEntity jobEntity = jobRepository.findById(id.longValue()).orElseThrow(
                () -> new EntityNotFoundException("Job with %s not found".formatted(id))
        );
		jobEntity.setDeleted(true);
		jobRepository.save(jobEntity);
		 LOG.info("Job deleted : Service");
	}
	
	@Override
	public List<JobEntity> getAllJobs(Integer pageNo, Integer pageSize, String sortBy) {
		LOG.info("getAllJobs request processing");
		Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
		List<JobEntity> jobEntityList = jobRepository.findAllAndIsDeleted(false, paging);
		LOG.info("jobEntityList retrieved : Service");
		return jobEntityList;
	}
	
	private LocalDateTime convertStringToLocalDateTime(String dateStr) {
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss");

		return LocalDateTime.parse(dateStr, formatter);
	}
	/**
	 * This method is used to convert JobRequest to JobEntity
	 * @param jobRequest
	 * @param jobEntity
	 * @return
	 */
	private JobEntity mapRequestToEntity(JobRequest jobRequest, JobEntity jobEntity) {
		if(jobEntity == null)
			jobEntity = new JobEntity();
		
		jobEntity.setRemarks(jobRequest.getJobRemarks());
		if(!ObjectUtils.isEmpty(jobRequest.getJobOpeningInformation())) {
			jobEntity.setJobDescription(jobRequest.getJobOpeningInformation().getJobDescription());
			jobEntity.setTitle(jobRequest.getJobOpeningInformation().getJobTitle());
			jobEntity.setJobType(jobRequest.getJobOpeningInformation().getJobType());
			jobEntity.setOpenDate(convertStringToLocalDateTime(jobRequest.getJobOpeningInformation().getDateOpen()));
			jobEntity.setCloseDate(convertStringToLocalDateTime(jobRequest.getJobOpeningInformation().getTargetClosingDate()));
			jobEntity.setClientJobId(jobRequest.getJobOpeningInformation().getClientJobID());
			jobEntity.setJobType(jobRequest.getJobOpeningInformation().getJobType());
			jobEntity.setDurationId(Integer.valueOf(jobRequest.getJobOpeningInformation().getDuration()));
			jobEntity.setPrimarySkills(jobRequest.getJobOpeningInformation().getPrimarySkills());
			jobEntity.setSecondarySkills(jobRequest.getJobOpeningInformation().getSecondarySkills());
			jobEntity.setNoOfHeadcount(Integer.valueOf(jobRequest.getJobOpeningInformation().getNoOfHeadcounts()));
			jobEntity.setWorkType(jobRequest.getJobOpeningInformation().getWorkType());
			jobEntity.setJobDescription(jobRequest.getJobOpeningInformation().getJobDescription());
			jobEntity.setVisaStatus(jobRequest.getJobOpeningInformation().getVisaStatus());
			jobEntity.setCountryName(jobRequest.getJobOpeningInformation().getCountry());
			jobEntity.setLanguages(jobRequest.getJobOpeningInformation().getLanguages());
			jobEntity.setRequiredDocument(jobRequest.getJobOpeningInformation().getRequiredDocuments());
			jobEntity.setWorkLocation(jobRequest.getJobOpeningInformation().getWorkLocation());
			jobEntity.setPriotity(Integer.valueOf(jobRequest.getJobOpeningInformation().getPriority()));
			jobEntity.setQualitifcation(jobRequest.getJobOpeningInformation().getQualification());
			jobEntity.setTurnAroundTimeUnit(jobRequest.getJobOpeningInformation().getTurnaroundTimeUnit());
			jobEntity.setTurnAroundTimeDay(jobRequest.getJobOpeningInformation().getTurnaroundTimeDay());
			jobEntity.setJobRatings(jobRequest.getJobOpeningInformation().getJobRatingSales());
			jobEntity.setSecurityClearance(Integer.valueOf(jobRequest.getJobOpeningInformation().getSecurityClearance()));
		}
		if(!ObjectUtils.isEmpty(jobRequest.getAccountInformation())) {
			jobEntity.setAccountId(jobRequest.getAccountInformation().getAccountId());
			jobEntity.setContactId(jobRequest.getAccountInformation().getContactId());
		}
		
		if(!ObjectUtils.isEmpty(jobRequest.getJobCommercials())) {
			jobEntity.setSalaryBudget(Integer.valueOf(jobRequest.getJobCommercials().getSalaryBudgetLocal()));
			jobEntity.setCurrency(jobRequest.getJobCommercials().getLocalCurrency());
			jobEntity.setBudgetType(jobRequest.getJobCommercials().getBudgetType());
			jobEntity.setSalaryBudget(jobRequest.getJobCommercials().getSalaryBudgetSGD());
			jobEntity.setSalaryBudgetRange(jobRequest.getJobCommercials().getSalaryBudgetLocal());
			jobEntity.setExpMarginSgd(jobRequest.getJobCommercials().getExpectedMarginSGD());
			jobEntity.setExpMarginMax(jobRequest.getJobCommercials().getExpectedMarginMax());
			jobEntity.setExpMarginMin(jobRequest.getJobCommercials().getExpectedMarginMin());
			jobEntity.setExpMarginCurrency(jobRequest.getJobCommercials().getExpectedMarginCurrency());
			if(!ObjectUtils.isEmpty(jobRequest.getJobCommercials().getScreening())) {
				jobEntity.setScreenRequired(true);
				jobEntity.setTechScreenTemplate(Integer.valueOf(jobRequest.getJobCommercials().getScreening().getSelectTechnicalScreeningTemplate()));
				jobEntity.setRecScrenTemplate(Integer.valueOf(jobRequest.getJobCommercials().getScreening().getSelectRecruitmentScreeningTemplate()));
			}
			
		}
		if(jobRequest.getUploadJobDocuments() != null) {
			try {
				jobEntity.setJobOverview(new javax.sql.rowset.serial.SerialBlob(jobRequest.getUploadJobDocuments().getBytes()));
			} catch (IOException e) {
				LOG.error("Error occured while converting multipart file into Blob ", e.getMessage());
			} catch (SerialException e) {
				LOG.error("Error occured while converting multipart file into Blob ", e.getMessage());
			} catch (SQLException e) {
				LOG.error("Error occured while converting multipart file into Blob ", e.getMessage());
			}			
		}
		
		
		return jobEntity;
	}

	@Override
	public Page<JobEntity> search(String searchTerms, Pageable pageable) {
		JobSpecificationBuilder builder = new JobSpecificationBuilder();
		Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
		Matcher matcher = pattern.matcher(searchTerms + ",");

		while (matcher.find()) {
			builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
		}

		return jobRepository.findAll(builder.build(), pageable);
	}
}
