package com.avensys.rts.jobservice.service;

import java.util.List;

import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.payloadrequest.JobRequest;

public interface JobService {

	public JobEntity createJob(JobRequest jobRequest);

	public JobEntity getJob(Integer id);

	public JobEntity updateJob(Integer id, JobRequest jobRequest);

	public void deleteJob(Integer id);
	
	public List<JobEntity> getAllJobs(Integer pageNo, Integer pageSize, String sortBy);
}
