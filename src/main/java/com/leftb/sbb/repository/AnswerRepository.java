package com.leftb.sbb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.leftb.sbb.entity.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
	
}
