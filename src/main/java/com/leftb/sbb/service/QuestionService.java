package com.leftb.sbb.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.leftb.sbb.DataNotFoundException;
import com.leftb.sbb.entity.Answer;
import com.leftb.sbb.entity.Question;
import com.leftb.sbb.entity.SbbUser;
import com.leftb.sbb.repository.QuestionRepository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import lombok.RequiredArgsConstructor;


@Service @RequiredArgsConstructor
public class QuestionService {

	private final QuestionRepository questionRepository;
	static final int pageSize = 10;

	public List<Question> getList() {

		return this.questionRepository.findAll();
	}

	public Page<Question> getList(int page, String kw) {

		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createTime"));
		Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sorts));
		Specification<Question> spec = search(kw);

		return this.questionRepository.findAll(spec, pageable);
	}

	public Question getQuestion(Integer id) {

		Optional<Question> question = this.questionRepository.findById(id);
		if(question.isPresent())
			return question.get();
		else
			throw new DataNotFoundException("question not found");
	}

	public void create(String subject, String content,
						SbbUser author) {

		Question q = new Question();
		q.setSubject(subject);
		q.setContent(content);
		q.setCreateTime(LocalDateTime.now());
		q.setAuthor(author);
		this.questionRepository.save(q);
	}

	public void modify(Question question,
			String subject, String content) {

		question.setSubject(subject);
		question.setContent(content);
		question.setModifyTime(LocalDateTime.now());
		this.questionRepository.save(question);
	}

	public void delete(Question question) {

		this.questionRepository.delete(question);
	}

	public void vote(Question question, SbbUser sbbUser) {

		question.getVoter().add(sbbUser);
        this.questionRepository.save(question);
    }

	private Specification<Question> search(String kw) {

		return new Specification<>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<Question> q,
						CriteriaQuery<?> query,
						CriteriaBuilder cb) {
				query.distinct(true);
				Join<Question, SbbUser> u1
					= q.join("author", JoinType.LEFT);
				Join<Question, Answer> a
					= q.join("answers", JoinType.LEFT);
				Join<Question, SbbUser> u2
					= q.join("author", JoinType.LEFT);

				return cb.or(
					cb.like(q.get("subject"), "%" + kw + "%"),
					cb.like(q.get("content"), "%" + kw + "%"),
					cb.like(u1.get("name"), "%" + kw + "%"),
					cb.like(a.get("content"), "%" + kw + "%"),
					cb.like(u2.get("name"), "%" + kw + "%"));
			}
		};
	}
}
