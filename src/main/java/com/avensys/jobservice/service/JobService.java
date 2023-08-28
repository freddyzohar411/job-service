package com.avensys.jobservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.avensys.jobservice.dto.JobRequest;
import com.avensys.jobservice.entity.JobEntity;
import com.avensys.jobservice.repository.JobRepository;

@Service
public class JobService {
	@Autowired
	private JobRepository jobRepository;

	public JobEntity createJob(JobRequest jobRequest) {
		JobEntity jobEntity = mapRequestToEntity(jobRequest);
		return jobRepository.save(jobEntity);
	}

	private JobEntity mapRequestToEntity(JobRequest jobRequest) {
		JobEntity jobEntity = new JobEntity();
		if(!ObjectUtils.isEmpty(jobRequest.getJobOpeningInformation())) {
			jobEntity.setJobDescription(jobRequest.getJobOpeningInformation().getJobDescription());
			jobEntity.setJobType(jobRequest.getJobOpeningInformation().getJobType());
			jobEntity.setOpenDate(jobRequest.getJobOpeningInformation().getDateOpen());
			jobEntity.setCloseDate(jobRequest.getJobOpeningInformation().getTargetClosingDate());
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
			jobEntity.setTurnAroundTimeDay(jobRequest.getJobOpeningInformation().getTurnaroundTime());
			jobEntity.setJobRatings(jobRequest.getJobOpeningInformation().getJobRatingSales());
			jobEntity.setSecurityClearance(Integer.valueOf(jobRequest.getJobOpeningInformation().getSecurityClearance()));
		}
		
		
		if(!ObjectUtils.isEmpty(jobRequest.getJobCommercials())) {
			jobEntity.setSalaryBudget(Integer.valueOf(jobRequest.getJobCommercials().getSalaryBudgetLocal()));
			jobEntity.setCurrency(jobRequest.getJobCommercials().getLocalCurrency());
			jobEntity.setBudgetType(jobRequest.getJobCommercials().getBudgetType());
			jobEntity.setSalaryBudget(Integer.valueOf(jobRequest.getJobCommercials().getSalaryBudgetSGD()));
			jobEntity.setExpMarginSgd(Integer.valueOf(jobRequest.getJobCommercials().getExpectedMarginSGD()));
			if(!ObjectUtils.isEmpty(jobRequest.getJobCommercials().getScreening())) {
				jobEntity.setScreenRequired(false);
				jobEntity.setTechScreenTemplate(Integer.valueOf(jobRequest.getJobCommercials().getScreening().getSelectTechnicalScreeningTemplate()));
				jobEntity.setRecScrenTemplate(Integer.valueOf(jobRequest.getJobCommercials().getScreening().getSelectRecruitmentScreeningTemplate()));
			}
			
		}
		
		
		return jobEntity;
	}
}
