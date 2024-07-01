package com.avensys.rts.jobservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.avensys.rts.jobservice.entity.AccountEntity;

public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {

	public Optional<AccountEntity> findByName(String name);
}
