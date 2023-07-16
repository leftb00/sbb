package com.leftb.sbb.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.leftb.sbb.entity.SbbUser;


public interface UserRepository extends JpaRepository<SbbUser, Long> {

	Optional<SbbUser> findByName(String name);
}
