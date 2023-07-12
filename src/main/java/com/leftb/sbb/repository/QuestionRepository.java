package com.leftb.sbb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.leftb.sbb.entity.Question;

public interface QuestionRepository
	extends JpaRepository<Question, Integer> {

}
