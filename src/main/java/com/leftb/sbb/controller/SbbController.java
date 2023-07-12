package com.leftb.sbb.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.leftb.sbb.entity.Question;
import com.leftb.sbb.service.QuestionService;

import lombok.RequiredArgsConstructor;


@Controller @RequiredArgsConstructor
public class SbbController {

	private final QuestionService questionService;

	@GetMapping({"/", "/list"})
	public String list(Model model) {

        List<Question> questionList = this.questionService.getList();
        model.addAttribute("questionList", questionList);

		return "list";
	}

	@GetMapping("/detail/{id}")
	public String detail(Model model, @PathVariable("id") Integer id) {

		Question question = this.questionService.getQuestion(id);
		model.addAttribute("question", question);

		return "detail";
	}

	@PostMapping("/answer/{id}")
	public String answer(Model model, @PathVariable("id") Integer id) {

		Question question = this.questionService.getQuestion(id);
		// TODO: 답변 저장

		return String.format("redirect:/detail/%d", id);
	}
}
