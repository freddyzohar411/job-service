package com.avensys.rts.jobservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.avensys.rts.jobservice.entity.JobStageEntity;
import com.avensys.rts.jobservice.exception.ServiceException;
import com.avensys.rts.jobservice.repository.JobStageRepository;

import jakarta.transaction.Transactional;

/**
 * @author Rahul Sahu
 * 
 */
@Service
public class JobStageService {

	@Autowired
	private JobStageRepository jobStageRepository;

	@Autowired
	private MessageSource messageSource;
	
	public JobStageService (JobStageRepository jobStageRepository,MessageSource messageSource) {
		this.jobStageRepository = jobStageRepository;
		this.messageSource = messageSource;
	}

	/**
	 * This method is used to save stage Need to implement roll back if error
	 * occurs.
	 * 
	 * @param entity
	 * @return
	 */
	@Transactional
	public JobStageEntity save(JobStageEntity entity) throws ServiceException {
		// add check for name exists in a DB
		if (jobStageRepository.existsByName(entity.getName())) {
			throw new ServiceException(
					messageSource.getMessage("error.nametaken", null, LocaleContextHolder.getLocale()));
		}
		entity.setIsActive(true);
		entity.setIsDeleted(false);
		entity = jobStageRepository.save(entity);
		return entity;
	}

	public void update(JobStageEntity entity) throws ServiceException {

		// check for name exists in a DB
		Optional<JobStageEntity> dbStage = jobStageRepository.findByName(entity.getName());

		if (dbStage.isPresent() && dbStage.get().getId() != entity.getId()) {
			throw new ServiceException(
					messageSource.getMessage("error.nametaken", null, LocaleContextHolder.getLocale()));
		}

		JobStageEntity stageEntity = getById(entity.getId());
		stageEntity.setIsActive(true);
		stageEntity.setIsDeleted(false);
		stageEntity.setName(entity.getName());
		stageEntity.setOrder(entity.getOrder());
		stageEntity.setType(entity.getType());
		stageEntity.setUpdatedBy(entity.getUpdatedBy());
		jobStageRepository.save(stageEntity);
	}

	public void delete(Long id) throws ServiceException {
		JobStageEntity dbUser = getById(id);
		dbUser.setIsDeleted(true);
		dbUser.setIsActive(false);
		jobStageRepository.save(dbUser);
	}

	public JobStageEntity getById(Long id) throws ServiceException {
		if (id == null) {
			throw new ServiceException(
					messageSource.getMessage("error.provide.id", new Object[] { id }, LocaleContextHolder.getLocale()));
		}
		Optional<JobStageEntity> jobStage = jobStageRepository.findById(id);
		if (jobStage.isPresent() && !jobStage.get().getIsDeleted()) {
			return jobStage.get();
		} else {
			throw new ServiceException(messageSource.getMessage("error.stagenotfound", new Object[] { id },
					LocaleContextHolder.getLocale()));
		}
	}

	public List<JobStageEntity> getAll() {
		List<JobStageEntity> jobStageList = jobStageRepository.findAllAndIsDeleted(false);
		return jobStageList;
	}

}
