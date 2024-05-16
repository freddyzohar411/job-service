package com.avensys.rts.jobservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

@Service
public class TosService {

	@Autowired
	private TosRepository tosRepository;

	@Autowired
	private DocumentAPIClient documentAPIClient;

	@Autowired
	private FormSubmissionAPIClient formSubmissionAPIClient;

	public TosEntity createTosEntityWithFiles(JobCandidateStageWithFilesRequest jobCandidateStageWithFilesRequest,
			CandidateEntity candidateEntity, JobEntity jobEntity) {
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

			HttpResponse formSubmissionResponse = formSubmissionAPIClient.addFormSubmission(formSubmissionsRequestDTO);
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

	public TosEntity createTosEntity(JobCandidateStageRequest jobCandidateStageRequest , CandidateEntity candidateEntity, JobEntity jobEntity) {
		TosEntity tosEntity = new TosEntity();
		tosEntity.setCreatedBy(jobCandidateStageRequest.getCreatedBy());
		tosEntity.setUpdatedBy(jobCandidateStageRequest.getUpdatedBy());
		tosEntity.setIsActive(true);
		tosEntity.setIsDeleted(false);
		tosEntity.setCandidate(candidateEntity);
		tosEntity.setJobEnity(jobEntity);
		if (jobCandidateStageRequest.getStatus().equals("COMPLETED")) {
			tosEntity.setStatus("APPROVED");
		} else if (jobCandidateStageRequest.getStatus().equals("REJECTED")) {
			tosEntity.setStatus("REJECTED");
		}
		TosEntity tosEnitySaved = tosRepository.save(tosEntity);

		// Set Form submission
		if (jobCandidateStageRequest.getFormId() != null) {
			FormSubmissionsRequestDTO formSubmissionsRequestDTO = new FormSubmissionsRequestDTO();
			formSubmissionsRequestDTO.setUserId(jobCandidateStageRequest.getCreatedBy());
			formSubmissionsRequestDTO.setFormId(jobCandidateStageRequest.getFormId());
			formSubmissionsRequestDTO.setSubmissionData(
					MappingUtil.convertJSONStringToJsonNode(jobCandidateStageRequest.getFormData()));
			formSubmissionsRequestDTO.setEntityId(tosEnitySaved.getId());
			formSubmissionsRequestDTO.setEntityType(jobCandidateStageRequest.getJobType());

			HttpResponse formSubmissionResponse = formSubmissionAPIClient
					.addFormSubmission(formSubmissionsRequestDTO);
			FormSubmissionsResponseDTO formSubmissionData = MappingUtil
					.mapClientBodyToClass(formSubmissionResponse.getData(), FormSubmissionsResponseDTO.class);
			tosEntity.setTosSubmissionData(formSubmissionsRequestDTO.getSubmissionData());
			tosEntity.setFormSubmissionId(formSubmissionData.getId());
		}
		return tosRepository.save(tosEntity);
	}

	public TosEntity updateTosEntity(TosEntity tosEntity,
			JobCandidateStageWithFilesRequest jobCandidateStageWithFilesRequest) {
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

			if (jobCandidateStageWithFilesRequest.getFiles() != null) {
				System.out.println("jobCandidateStageWithFilesRequest.getFiles().length2: "
						+ jobCandidateStageWithFilesRequest.getFiles().length);
				int fileLength = jobCandidateStageWithFilesRequest.getFiles().length;
				String[] fileKeys = new String[fileLength];
				MultipartFile[] files = new MultipartFile[fileLength];
				for (int i = 0; i < jobCandidateStageWithFilesRequest.getFiles().length; i++) {
					FileDataDTO fileData = jobCandidateStageWithFilesRequest.getFiles()[i];
					if (fileData.getFileKey() == null) {
						fileKeys[i] = null;
					} else {
						fileKeys[i] = fileData.getFileKey();
					}
					if (fileData.getFile() != null) {
						files[i] = fileData.getFile();
					} else {
						files[i] = new MockMultipartFile("mock_emptyFile", "", "multipart/form-data", new byte[0]);
					}
				}
				updateDocumentListKeyDTO.setFileKeys(fileKeys);
				updateDocumentListKeyDTO.setFiles(files);
				documentAPIClient.updateDocumentListWithKeys(updateDocumentListKeyDTO);
			} else {
				documentAPIClient.updateDocumentListWithKeys(updateDocumentListKeyDTO);
			}

		}
		return tosRepository.save(tosEntity);
	}

	public TosEntity getTosEntity(Long jobId, Long candidateId, String status) {
		return tosRepository.findByJobAndCandidateAndStatus(jobId, candidateId, status).orElse(null);
	}
}
