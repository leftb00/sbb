package com.leftb.sbb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.leftb.sbb.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

	Question findBySubject(String subject);
	Question findBySubjectAndContent(String subject, String content);
	List<Question> findBySubjectLike(String subject);
}