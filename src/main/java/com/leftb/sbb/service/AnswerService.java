package com.leftb.sbb.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.leftb.sbb.DataNotFoundException;
import com.leftb.sbb.entity.Answer;
import com.leftb.sbb.entity.Question;
import com.leftb.sbb.entity.SbbUser;
import com.leftb.sbb.repository.AnswerRepository;

import lombok.RequiredArgsConstructor;


@Service @RequiredArgsConstructor
public class AnswerService {

	private final AnswerRepository answerRepository;

	public Answer getAnswer(Integer id) {

		Optional<Answer> answer = this.answerRepository.findById(id);
		if (answer.isEmpty())
			throw new DataNotFoundException("answer not found");

		return answer.get();
	}

	public Answer create(Question question, String content,
			SbbUser author) {

		Answer answer = new Answer();
		answer.setContent(content);
		answer.setQuestion(question);
		answer.setAuthor(author);
		answer.setCreateTime(LocalDateTime.now());
		this.answerRepository.save(answer);

		return answer;
	}

	public void modify(Answer answer, String content) {

		answer.setContent(content);
		answer.setModifyTime(LocalDateTime.now());
		this.answerRepository.save(answer);
	}

	public void delete(Answer answer) {

		this.answerRepository.delete(answer);
	}

	public void vote(Answer answer, SbbUser sbbUser) {

		answer.getVoter().add(sbbUser);
		this.answerRepository.save(answer);
	}
}
