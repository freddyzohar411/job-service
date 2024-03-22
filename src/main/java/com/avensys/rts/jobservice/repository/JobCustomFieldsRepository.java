package com.avensys.rts.jobservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avensys.rts.jobservice.entity.CustomFieldsEntity;

public interface JobCustomFieldsRepository extends JpaRepository<CustomFieldsEntity, Long>  {

}
