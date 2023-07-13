package com.leftb.sbb.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.leftb.sbb.entity.Question;
import com.leftb.sbb.form.AnswerForm;
import com.leftb.sbb.form.QuestionForm;
import com.leftb.sbb.service.AnswerService;
import com.leftb.sbb.service.QuestionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@Controller @RequiredArgsConstructor
public class SbbController {

	private final QuestionService questionService;
	private final AnswerService answerService;

	@GetMapping({"/", "/list"})
	public String list(Model model) {

        List<Question> questionList = this.questionService.getList();
        model.addAttribute("questionList", questionList);

		return "list";
	}

	@GetMapping("/question")
	public String questionForm(QuestionForm questionForm) {

		return "question";
	}

	@PostMapping("/question")
	public String question(
			@Valid QuestionForm questionForm,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors())
			return "question";

		this.questionService.create(
			questionForm.getSubject(),
			questionForm.getContent()
		);
		return "redirect:/list";
	}

	@GetMapping("/detail/{id}")
	public String detail(Model model,
			@PathVariable("id") Integer id,
			AnswerForm answerForm) {

		Question question = this.questionService.getQuestion(id);
		model.addAttribute("question", question);

		return "detail";
	}

	@PostMapping("/detail/{id}")
	public String answer(Model model,
			@PathVariable("id") Integer id,
			@Valid AnswerForm answerForm,
			BindingResult bindingResult) {

		Question question = this.questionService.getQuestion(id);
		if (bindingResult.hasErrors()) {
			model.addAttribute("question", question);
			return "detail";
		}
		this.answerService.create(question, answerForm.getContent());

		return String.format("redirect:/detail/%d", id);
	}
}
