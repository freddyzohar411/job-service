package com.avensys.rts.jobservice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avensys.rts.jobservice.apiclient.DocumentAPIClient;
import com.avensys.rts.jobservice.apiclient.FormSubmissionAPIClient;
import com.avensys.rts.jobservice.entity.CandidateEntity;
import com.avensys.rts.jobservice.entity.ConditionalOfferEntity;
import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.payload.*;
import com.avensys.rts.jobservice.repository.ConditionalOfferRepository;
import com.avensys.rts.jobservice.response.FormSubmissionsResponseDTO;
import com.avensys.rts.jobservice.response.HttpResponse;
import com.avensys.rts.jobservice.util.MappingUtil;

@Service
public class ConditionalOfferService {

	@Autowired
	private ConditionalOfferRepository conditionalOfferRepository;

	@Autowired
	private DocumentAPIClient documentAPIClient;

	@Autowired
	private FormSubmissionAPIClient formSubmissionAPIClient;

	public ConditionalOfferEntity createConditionalOfferEntityWithFiles(
			JobCandidateStageWithFilesRequest jobCandidateStageWithFilesRequest, CandidateEntity candidateEntity,
			JobEntity jobEntity) {
		Optional<ConditionalOfferEntity> conditionalOfferEntityOptional = conditionalOfferRepository
				.findByJobAndCandidateAndStatus(jobEntity.getId(), candidateEntity.getId(), "ACCEPTED");
		ConditionalOfferEntity conditionalOfferEntity = null;
		conditionalOfferEntity = conditionalOfferEntityOptional.orElseGet(ConditionalOfferEntity::new);
//		ConditionalOfferEntity conditionalOfferEntity = new ConditionalOfferEntity();
		conditionalOfferEntity.setCreatedBy(jobCandidateStageWithFilesRequest.getCreatedBy());
		conditionalOfferEntity.setUpdatedBy(jobCandidateStageWithFilesRequest.getUpdatedBy());
		conditionalOfferEntity.setIsActive(true);
		conditionalOfferEntity.setIsDeleted(false);
		conditionalOfferEntity.setCandidate(candidateEntity);
		conditionalOfferEntity.setJobEnity(jobEntity);
		conditionalOfferEntity.setStatus("ACCEPTED");
		ConditionalOfferEntity conditionalOfferEntitySaved = conditionalOfferRepository.save(conditionalOfferEntity);

		// Set Form submission
		if (jobCandidateStageWithFilesRequest.getFormId() != null) {
			FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
			formSubmissionsRequestDTO.setUserId(jobCandidateStageWithFilesRequest.getCreatedBy());
			formSubmissionsRequestDTO.setFormId(jobCandidateStageWithFilesRequest.getFormId());
			formSubmissionsRequestDTO.setSubmissionData(
					MappingUtil.convertJSONStringToJsonNode(jobCandidateStageWithFilesRequest.getFormData()));
			formSubmissionsRequestDTO.setEntityId(conditionalOfferEntitySaved.getId());
			formSubmissionsRequestDTO.setEntityType(jobCandidateStageWithFilesRequest.getJobType());

			HttpResponse formSubmissionResponse = formSubmissionAPIClient.addFormSubmission(formSubmissionsRequestDTO);
			FormSubmissionsResponseDTO formSubmissionData = MappingUtil
					.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);
			conditionalOfferEntitySaved
					.setConditionalOfferSubmissionData(formSubmissionsRequestDTO.getSubmissionData());
			conditionalOfferEntitySaved.setFormSubmissionId(formSubmissionData.getId());
		}

		if (jobCandidateStageWithFilesRequest.getFiles() != null) {
			// Use For loop
			for (FileDataDTO fileData : jobCandidateStageWithFilesRequest.getFiles()) {
				DocumentRequestDTO documentRequestDTO = new DocumentRequestDTO();
				documentRequestDTO.setEntityId(conditionalOfferEntitySaved.getId());
				documentRequestDTO.setEntityType("condition_offer");
				documentRequestDTO.setFile(fileData.getFile());
				documentRequestDTO.setDocumentKey(fileData.getFileKey());
				documentAPIClient.createDocument(documentRequestDTO);
			}
		}
		return conditionalOfferRepository.save(conditionalOfferEntitySaved);
	}

	public ConditionalOfferEntity createConditionalOfferEntityEntityWithForm(
			JobCandidateStageRequest jobCandidateStageRequest, CandidateEntity candidateEntity, JobEntity jobEntity) {
		ConditionalOfferEntity conditionalOfferEntity = new ConditionalOfferEntity();
		conditionalOfferEntity.setCreatedBy(jobCandidateStageRequest.getCreatedBy());
		conditionalOfferEntity.setUpdatedBy(jobCandidateStageRequest.getUpdatedBy());
		conditionalOfferEntity.setIsActive(true);
		conditionalOfferEntity.setIsDeleted(false);
		conditionalOfferEntity.setCandidate(candidateEntity);
		conditionalOfferEntity.setJobEnity(jobEntity);
		if (jobCandidateStageRequest.getStatus().equals("COMPLETED")) {
			conditionalOfferEntity.setStatus("APPROVED");
		} else if (jobCandidateStageRequest.getStatus().equals("REJECTED")) {
			conditionalOfferEntity.setStatus("REJECTED");
		}
		ConditionalOfferEntity conditionalOfferEntitySaved = conditionalOfferRepository.save(conditionalOfferEntity);

		// Set Form submission
		if (jobCandidateStageRequest.getFormId() != null) {
			FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
			formSubmissionsRequestDTO.setUserId(jobCandidateStageRequest.getCreatedBy());
			formSubmissionsRequestDTO.setFormId(jobCandidateStageRequest.getFormId());
			formSubmissionsRequestDTO
					.setSubmissionData(MappingUtil.convertJSONStringToJsonNode(jobCandidateStageRequest.getFormData()));
			formSubmissionsRequestDTO.setEntityId(conditionalOfferEntitySaved.getId());
			formSubmissionsRequestDTO.setEntityType(jobCandidateStageRequest.getJobType());

			HttpResponse formSubmissionResponse = formSubmissionAPIClient.addFormSubmission(formSubmissionsRequestDTO);
			FormSubmissionsResponseDTO formSubmissionData = MappingUtil
					.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);
			conditionalOfferEntitySaved
					.setConditionalOfferSubmissionData(formSubmissionsRequestDTO.getSubmissionData());
			conditionalOfferEntitySaved.setFormSubmissionId(formSubmissionData.getId());
		}
		return conditionalOfferRepository.save(conditionalOfferEntitySaved);
	}

	public ConditionalOfferEntity createConditionalOfferEntity(JobCandidateStageRequest jobCandidateStageRequest,
			CandidateEntity candidateEntity, JobEntity jobEntity) {
		Optional<ConditionalOfferEntity> conditionalOfferEntityOptional = conditionalOfferRepository
				.findByJobAndCandidateAndStatus(jobEntity.getId(), candidateEntity.getId(), "DRAFT");
		if (conditionalOfferEntityOptional.isPresent()) {
			ConditionalOfferEntity conditionalOfferEntity = conditionalOfferEntityOptional.get();
			conditionalOfferEntity.setUpdatedBy(jobCandidateStageRequest.getUpdatedBy());
			conditionalOfferEntity.setConditionalOfferSubmissionData(
					MappingUtil.convertJSONStringToJsonNode(jobCandidateStageRequest.getFormData()));
			conditionalOfferEntity.setStatus("DRAFT");
			return conditionalOfferRepository.save(conditionalOfferEntity);
		}
		ConditionalOfferEntity conditionalOfferEntity = new ConditionalOfferEntity();
		conditionalOfferEntity.setCreatedBy(jobCandidateStageRequest.getCreatedBy());
		conditionalOfferEntity.setUpdatedBy(jobCandidateStageRequest.getUpdatedBy());
		conditionalOfferEntity.setIsActive(true);
		conditionalOfferEntity.setIsDeleted(false);
		conditionalOfferEntity.setCandidate(candidateEntity);
		conditionalOfferEntity.setJobEnity(jobEntity);
		if (jobCandidateStageRequest.getStatus().equals("DRAFT")) {
			conditionalOfferEntity.setStatus("DRAFT");
		} else if (jobCandidateStageRequest.getStatus().equals("COMPLETED")) {
			conditionalOfferEntity.setStatus("COMPLETED");
		}
		conditionalOfferEntity.setConditionalOfferSubmissionData(
				MappingUtil.convertJSONStringToJsonNode(jobCandidateStageRequest.getFormData()));
		ConditionalOfferEntity conditionalOfferEntitySaved = conditionalOfferRepository.save(conditionalOfferEntity);

		return conditionalOfferRepository.save(conditionalOfferEntitySaved);
	}

	public ConditionalOfferEntity updateConditionalOfferEntity(JobCandidateStageRequest jobCandidateStageRequest,
			CandidateEntity candidateEntity, JobEntity jobEntity) {
		Optional<ConditionalOfferEntity> conditionalOfferEntityOptional = conditionalOfferRepository
				.findByJobAndCandidateAndStatus(jobEntity.getId(), candidateEntity.getId(), "DRAFT");
		if (conditionalOfferEntityOptional.isPresent()) {
			ConditionalOfferEntity conditionalOfferEntity = conditionalOfferEntityOptional.get();
			conditionalOfferEntity.setUpdatedBy(jobCandidateStageRequest.getUpdatedBy());
			conditionalOfferEntity.setConditionalOfferSubmissionData(
					MappingUtil.convertJSONStringToJsonNode(jobCandidateStageRequest.getFormData()));
			conditionalOfferEntity.setStatus("COMPLETED");
			conditionalOfferEntity.setCandidate(candidateEntity);
			conditionalOfferEntity.setJobEnity(jobEntity);
			return conditionalOfferRepository.save(conditionalOfferEntity);
		}
		return null;
	}

	public ConditionalOfferEntity createReleaseConditionalOfferEntity(
			JobCandidateStageWithAttachmentsRequest jobCandidateStageWithAttachmentsRequest,
			CandidateEntity candidateEntity, JobEntity jobEntity) {
		Optional<ConditionalOfferEntity> conditionalOfferEntityOptional = conditionalOfferRepository
				.findByJobAndCandidateAndStatus(jobEntity.getId(), candidateEntity.getId(), "RELEASED");
		if (conditionalOfferEntityOptional.isPresent()) {
			ConditionalOfferEntity conditionalOfferEntity = conditionalOfferEntityOptional.get();
			conditionalOfferEntity.setUpdatedBy(jobCandidateStageWithAttachmentsRequest.getUpdatedBy());
			conditionalOfferEntity.setConditionalOfferSubmissionData(
					MappingUtil.convertJSONStringToJsonNode(jobCandidateStageWithAttachmentsRequest.getFormData()));
			conditionalOfferEntity.setStatus("RELEASED");
			conditionalOfferEntity.setCandidate(candidateEntity);
			conditionalOfferEntity.setJobEnity(jobEntity);
			return conditionalOfferRepository.save(conditionalOfferEntity);
		}
		ConditionalOfferEntity conditionalOfferEntity = new ConditionalOfferEntity();
		conditionalOfferEntity.setCreatedBy(jobCandidateStageWithAttachmentsRequest.getCreatedBy());
		conditionalOfferEntity.setUpdatedBy(jobCandidateStageWithAttachmentsRequest.getUpdatedBy());
		conditionalOfferEntity.setIsActive(true);
		conditionalOfferEntity.setIsDeleted(false);
		conditionalOfferEntity.setCandidate(candidateEntity);
		conditionalOfferEntity.setJobEnity(jobEntity);
		conditionalOfferEntity.setStatus("RELEASED");
		conditionalOfferEntity.setConditionalOfferSubmissionData(
				MappingUtil.convertJSONStringToJsonNode(jobCandidateStageWithAttachmentsRequest.getFormData()));
		ConditionalOfferEntity conditionalOfferEntitySaved = conditionalOfferRepository.save(conditionalOfferEntity);
		return conditionalOfferRepository.save(conditionalOfferEntitySaved);
	}

	public ConditionalOfferEntity createRejectedConditionalOfferEntityEntityWithForm(
			JobCandidateStageRequest jobCandidateStageRequest, CandidateEntity candidateEntity, JobEntity jobEntity) {
		Optional<ConditionalOfferEntity> conditionalOfferEntityOptional = conditionalOfferRepository
				.findByJobAndCandidateAndStatus(jobEntity.getId(), candidateEntity.getId(), "REJECTED");
		ConditionalOfferEntity conditionalOfferEntity = null;
		conditionalOfferEntity = conditionalOfferEntityOptional.orElseGet(ConditionalOfferEntity::new);
//		ConditionalOfferEntity conditionalOfferEntity = new ConditionalOfferEntity();
		conditionalOfferEntity.setCreatedBy(jobCandidateStageRequest.getCreatedBy());
		conditionalOfferEntity.setUpdatedBy(jobCandidateStageRequest.getUpdatedBy());
		conditionalOfferEntity.setIsActive(true);
		conditionalOfferEntity.setIsDeleted(false);
		conditionalOfferEntity.setCandidate(candidateEntity);
		conditionalOfferEntity.setJobEnity(jobEntity);
		conditionalOfferEntity.setStatus("REJECTED");
		conditionalOfferEntity.setFormId(jobCandidateStageRequest.getFormId());
		ConditionalOfferEntity conditionalOfferEntitySaved = conditionalOfferRepository.save(conditionalOfferEntity);

		// Set Form submission
		if (jobCandidateStageRequest.getFormId() != null) {
			FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
			formSubmissionsRequestDTO.setUserId(jobCandidateStageRequest.getCreatedBy());
			formSubmissionsRequestDTO.setFormId(jobCandidateStageRequest.getFormId());
			formSubmissionsRequestDTO
					.setSubmissionData(MappingUtil.convertJSONStringToJsonNode(jobCandidateStageRequest.getFormData()));
			formSubmissionsRequestDTO.setEntityId(conditionalOfferEntitySaved.getId());
			formSubmissionsRequestDTO.setEntityType(jobCandidateStageRequest.getJobType());

			HttpResponse formSubmissionResponse = formSubmissionAPIClient.addFormSubmission(formSubmissionsRequestDTO);
			FormSubmissionsResponseDTO formSubmissionData = MappingUtil
					.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);
			conditionalOfferEntitySaved
					.setConditionalOfferSubmissionData(formSubmissionsRequestDTO.getSubmissionData());
			conditionalOfferEntitySaved.setFormSubmissionId(formSubmissionData.getId());
		}
		return conditionalOfferRepository.save(conditionalOfferEntitySaved);
	}

}
