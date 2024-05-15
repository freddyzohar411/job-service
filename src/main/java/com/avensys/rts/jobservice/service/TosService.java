package com.avensys.rts.jobservice.service;

import com.avensys.rts.jobservice.apiclient.DocumentAPIClient;
import com.avensys.rts.jobservice.apiclient.FormSubmissionAPIClient;
import com.avensys.rts.jobservice.entity.CandidateEntity;
import com.avensys.rts.jobservice.entity.JobEntity;
import com.avensys.rts.jobservice.entity.TosEntity;
import com.avensys.rts.jobservice.payload.*;
import com.avensys.rts.jobservice.repository.TosRepository;
import com.avensys.rts.jobservice.response.FormSubmissionsResponseDTO;
import com.avensys.rts.jobservice.response.HttpResponse;
import com.avensys.rts.jobservice.util.MappingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TosService {

	@Autowired
	private TosRepository tosRepository;

	@Autowired
	private DocumentAPIClient documentAPIClient;

	@Autowired
	private FormSubmissionAPIClient formSubmissionAPIClient;

	public TosEntity createTosEntity(JobCandidateStageWithFilesRequest jobCandidateStageWithFilesRequest, CandidateEntity candidateEntity, JobEntity jobEntity) {
		TosEntity tosEntity = new TosEntity();
		tosEntity.setCreatedBy(jobCandidateStageWithFilesRequest.getCreatedBy());
		tosEntity.setUpdatedBy(jobCandidateStageWithFilesRequest.getUpdatedBy());
		tosEntity.setIsActive(true);
		tosEntity.setIsDeleted(false);
		tosEntity.setCandidate(candidateEntity);
		tosEntity.setJobEnity(jobEntity);
		tosEntity.setStatus("PREPARE");
		TosEntity tosEnitySaved = tosRepository.save(tosEntity);

		// Set Form submission
		if (jobCandidateStageWithFilesRequest.getFormId() != null) {
			FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
			formSubmissionsRequestDTO.setUserId(jobCandidateStageWithFilesRequest.getCreatedBy());
			formSubmissionsRequestDTO.setFormId(jobCandidateStageWithFilesRequest.getFormId());
			formSubmissionsRequestDTO.setSubmissionData(
					MappingUtil.convertJSONStringToJsonNode(jobCandidateStageWithFilesRequest.getFormData()));
			formSubmissionsRequestDTO.setEntityId(tosEnitySaved.getId());
			formSubmissionsRequestDTO.setEntityType(jobCandidateStageWithFilesRequest.getJobType());

			HttpResponse formSubmissionResponse = formSubmissionAPIClient
					.addFormSubmission(formSubmissionsRequestDTO);
			FormSubmissionsResponseDTO formSubmissionData = MappingUtil
					.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);
			tosEntity.setTosSubmissionData(formSubmissionsRequestDTO.getSubmissionData());
			tosEntity.setFormSubmissionId(formSubmissionData.getId());
		}

		if (jobCandidateStageWithFilesRequest.getFiles() != null) {
			// Use For loop
			for (FileDataDTO fileData : jobCandidateStageWithFilesRequest.getFiles()) {
				DocumentRequestDTO documentRequestDTO = new DocumentRequestDTO();
				documentRequestDTO.setEntityId(tosEnitySaved.getId());
				documentRequestDTO.setEntityType("TOS");
				documentRequestDTO.setFile(fileData.getFile());
				documentRequestDTO.setDocumentKey(fileData.getFileKey());
				documentAPIClient.createDocument(documentRequestDTO);
			}
		}
		return tosRepository.save(tosEntity);
	}

	public TosEntity updateTosEntity(TosEntity tosEntity, JobCandidateStageWithFilesRequest jobCandidateStageWithFilesRequest) {
		// Set the form submission update
		if (jobCandidateStageWithFilesRequest.getFormId() != null) {
			FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
			formSubmissionsRequestDTO.setUserId(jobCandidateStageWithFilesRequest.getCreatedBy());
			formSubmissionsRequestDTO.setFormId(jobCandidateStageWithFilesRequest.getFormId());
			formSubmissionsRequestDTO.setSubmissionData(
					MappingUtil.convertJSONStringToJsonNode(jobCandidateStageWithFilesRequest.getFormData()));
			formSubmissionsRequestDTO.setEntityId(tosEntity.getId());
			formSubmissionsRequestDTO.setEntityType(jobCandidateStageWithFilesRequest.getJobType());
			HttpResponse formSubmissionResponse = formSubmissionAPIClient
					.updateFormSubmission(tosEntity.getFormSubmissionId().intValue(), formSubmissionsRequestDTO);
			FormSubmissionsResponseDTO formSubmissionData = MappingUtil
					.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);
			tosEntity.setTosSubmissionData(formSubmissionsRequestDTO.getSubmissionData());
			tosEntity.setFormSubmissionId(formSubmissionData.getId());

			// Set the document update even if null set empty array
			UpdateDocumentListKeyDTO updateDocumentListKeyDTO = new UpdateDocumentListKeyDTO();
			updateDocumentListKeyDTO.setEntityType("TOS");
			updateDocumentListKeyDTO.setEntityId(tosEntity.getId().intValue());
			System.out.println("jobCandidateStageWithFilesRequest.getFiles().length: " + jobCandidateStageWithFilesRequest.getFiles().length);

			// Loop Through and set the document key, file and entity id
			DocumentKeyRequestDTO[] documentKeyRequestDTO = new DocumentKeyRequestDTO[jobCandidateStageWithFilesRequest
					.getFiles().length];
			if (jobCandidateStageWithFilesRequest.getFiles().length > 0) {
				System.out.println("jobCandidateStageWithFilesRequest.getFiles().length2: " + jobCandidateStageWithFilesRequest.getFiles().length);
				for (int i = 0; i < jobCandidateStageWithFilesRequest.getFiles().length; i++) {
					FileDataDTO fileData = jobCandidateStageWithFilesRequest.getFiles()[i];
					DocumentKeyRequestDTO documentKeyRequestDTO1 = new DocumentKeyRequestDTO();
					if (fileData.getFileKey() == null) {
						documentKeyRequestDTO1.setDocumentKey("");
					} else {
						documentKeyRequestDTO1.setDocumentKey(fileData.getFileKey());
					}
					if (fileData.getFile() != null) {
						documentKeyRequestDTO1.setFile(fileData.getFile());
					}
					documentKeyRequestDTO[i] = documentKeyRequestDTO1;
				}
				updateDocumentListKeyDTO.setDocumentKeyRequestDTO(documentKeyRequestDTO);
				documentAPIClient.updateDocumentListWithKeys(updateDocumentListKeyDTO.getEntityType(),
						updateDocumentListKeyDTO.getEntityId(), documentKeyRequestDTO);
			} else {
				documentAPIClient.updateDocumentListWithKeys(updateDocumentListKeyDTO.getEntityType(),
						updateDocumentListKeyDTO.getEntityId(), documentKeyRequestDTO);
			}
		}
		return tosRepository.save(tosEntity);
	}
	public TosEntity getTosEntity(Long jobId, Long candidateId) {
		return tosRepository.findByJobAndCandidate(jobId, candidateId).orElse(null);
	}
}
