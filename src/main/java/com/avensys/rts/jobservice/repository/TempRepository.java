package com.avensys.rts.jobservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.avensys.rts.jobservice.entity.JobEntity;

public interface TempRepository extends JpaRepository<JobEntity, Long> {

	@Query(value = "select id from job where job_submission_data  ->> 'jobId' is null", nativeQuery = true)
	public List<Long> getNullJobs();

	@Query(value = "select id from account where account_submission_data  ->> 'accountId' is null", nativeQuery = true)
	public List<Integer> getNullAccounts();

	@Query(value = "select id from candidate c where candidate_submission_data ->> 'candidateId' is null", nativeQuery = true)
	public List<Long> getNullCandidates();

	@Query(value = "select iso3 from countries where name = ?1", nativeQuery = true)
	public String getISO3(String country);

	@Query(value = "UPDATE generate_id SET counter = COALESCE(counter, 0) + 1 WHERE module_name = ?1 RETURNING counter", nativeQuery = true)
	public Long getPulseId(String module);

	@Modifying
	@Query(value = "update job set job_submission_data = (select submission_data from form_submissions where id = ?1) where id = ?2", nativeQuery = true)
	public void updateJobId(Long submissionId, Long id);

	@Modifying
	@Query(value = "update account set account_submission_data = (select submission_data from form_submissions where id = ?1) where id = ?2", nativeQuery = true)
	public void updateAccountId(Integer submissionId, Integer id);

	@Modifying
	@Query(value = "update candidate set candidate_submission_data = (select submission_data from form_submissions where id = ?1) where id = ?2", nativeQuery = true)
	public void updateCandidateId(Integer submissionId, Long id);

}