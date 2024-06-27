package com.avensys.rts.jobservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.avensys.rts.jobservice.entity.CustomFieldsEntity;
import com.avensys.rts.jobservice.response.CustomFieldsResponseDTO;
import org.springframework.transaction.annotation.Transactional;

public interface JobCustomFieldsRepository extends JpaRepository<CustomFieldsEntity, Long>  {
	
	public Boolean existsByName(String name);
	
	  @Query(value = "SELECT c FROM customView c WHERE c.createdBy = ?1 AND c.isSelected = ?2")
	    CustomFieldsResponseDTO findAllByUserAndSelected(Long userId,boolean isSelected);
	  
	  @Query(value = "SELECT c FROM customView c WHERE c.createdBy = ?1 AND c.type = ?2 AND c.isDeleted =?3")
	    List<CustomFieldsEntity> findAllByUser(Long userId,String type,boolean isDeleted);
	  
	  //for checkeing is there is any isSelected true.
	   // @Query(value = "SELECT c FROM customView c WHERE c.createdBy = ?1 AND c.type = ?2")
	   // CustomFieldsEntity findByUserAndType(Long userId,String type);
	    
	    @Query(value = "SELECT c FROM customView c WHERE c.id = ?1")
	    Optional<CustomFieldsEntity> findById(Long id);
	    
	    @Query(value = "SELECT c FROM customView c WHERE c.id = ?1 AND c.isDeleted = ?2 AND c.isActive = ?3")
	    Optional<CustomFieldsEntity> findByIdAndDeleted(Long id, boolean isDeleted, boolean isActive);

	// Find Boolean exist by name if type and is_deleted and created By, return true or false
	@Query(value = "SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END FROM customView c WHERE c.name = ?1 AND c.type = ?2 AND c.isDeleted = ?3 AND c.createdBy = ?4")
	Boolean findByNameAndTypeAndIsDeletedAndCreatedBy(String name, String type, boolean isDeleted,
			Integer createdBy);

	@Modifying
	@Transactional
	@Query("UPDATE customView c SET c.isSelected = ?1 WHERE c.type = ?2 AND c.isDeleted = ?3 AND c.createdBy = ?4")
	void updateIsSelected(boolean isSelected, String type, boolean isDeleted, Integer createdBy);

}
