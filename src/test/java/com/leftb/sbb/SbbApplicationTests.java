package com.leftb.sbb;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.leftb.sbb.entity.Answer;
import com.leftb.sbb.entity.Question;
import com.leftb.sbb.repository.AnswerRepository;
import com.leftb.sbb.repository.QuestionRepository;


@SpringBootTest
class SbbApplicationTests {

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
    private AnswerRepository answerRepository;

	@Test
	void testJpa() {

		Optional<Question> oq = this.questionRepository.findById(2);
		assertTrue(oq.isPresent());
		Question q = oq.get();

		Answer a = new Answer();
		a.setContent("네 자동으로 생성됩니다.");
		a.setQuestion(q);  // 어떤 질문의 답변인지 알기위해서 Question 객체가 필요하다.
		a.setCreateTime(LocalDateTime.now());
		this.answerRepository.save(a);
	}
}
