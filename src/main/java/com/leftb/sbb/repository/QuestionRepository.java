package com.leftb.sbb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.leftb.sbb.entity.Question;

public interface QuestionRepository
	extends JpaRepository<Question, Integer> {

	Page<Question> findAll(Pageable pageable);
	Page<Question> findAll(Specification<Question> spec,
							Pageable pageable);
}
