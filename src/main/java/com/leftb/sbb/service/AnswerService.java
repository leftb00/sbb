package com.leftb.sbb.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.leftb.sbb.entity.Answer;
import com.leftb.sbb.entity.Question;
import com.leftb.sbb.repository.AnswerRepository;

import lombok.RequiredArgsConstructor;


@Service @RequiredArgsConstructor
public class AnswerService {

	private final AnswerRepository answerRepository;

	public void create(Question question, String content) {
		Answer answer = new Answer();
		answer.setContent(content);
		answer.setQuestion(question);
		answer.setCreateTime(LocalDateTime.now());
		this.answerRepository.save(answer);
	}
}
