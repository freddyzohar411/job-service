package com.avensys.rts.jobservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.avensys.rts.jobservice.entity.CustomFieldsEntity;
import com.avensys.rts.jobservice.response.CustomFieldsResponseDTO;

public interface JobCustomFieldsRepository extends JpaRepository<CustomFieldsEntity, Long>  {
	
	public Boolean existsByName(String name);
	
	  @Query(value = "SELECT c FROM customView c WHERE c.createdBy = ?1 AND c.isSelected = ?2")
	    CustomFieldsResponseDTO findAllByUserAndSelected(Long userId,boolean isSelected);

}
