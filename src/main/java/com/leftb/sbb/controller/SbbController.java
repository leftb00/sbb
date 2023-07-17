package com.leftb.sbb.controller;

import java.security.Principal;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.leftb.sbb.entity.Answer;
import com.leftb.sbb.entity.Question;
import com.leftb.sbb.entity.SbbUser;
import com.leftb.sbb.form.AnswerForm;
import com.leftb.sbb.form.QuestionForm;
import com.leftb.sbb.form.UserForm;
import com.leftb.sbb.service.AnswerService;
import com.leftb.sbb.service.QuestionService;
import com.leftb.sbb.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@Controller @RequiredArgsConstructor
public class SbbController {

	private final QuestionService questionService;
	private final AnswerService answerService;
	private final UserService userService;

	@GetMapping("/signup")
	public String signup(UserForm userForm) {

		return "signup";
	}

	@PostMapping("/signup")
	public String signup(@Valid UserForm userForm,
			BindingResult bindingResult) {

		do{
			if(bindingResult.hasErrors()) break;
			if(!userForm.getPassword1().equals(userForm.getPassword2())) {
				bindingResult.rejectValue(
					"password2",
					"passwordInCorrect",
					"2개의 패스워드가 일치하지 않습니다.");
				break;
			}

			try {
				userService.create(userForm.getName(),
					userForm.getEmail(),
					userForm.getPassword1());
			} catch(DataIntegrityViolationException e) {
				e.printStackTrace();
				bindingResult.reject("signupFailed",
					"이미 등록된 사용자 입니다.");
				break;
			} catch(Exception e) {
				e.printStackTrace();
				bindingResult.reject("signupFailed",
					e.getMessage());
				break;
			}

			return "redirect:/";
		} while(false);

		return "signup";
	}

	@GetMapping("/login")
	public String login() {

		return "login";
	}

	@GetMapping({"/", "/list"})
	public String list(Model model,
		@RequestParam(value="page", defaultValue="0") int page,
		@RequestParam(value="kw", defaultValue="") String kw) {

        Page<Question> paging = this.questionService.getList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);

		return "list";
	}

	@PreAuthorize("isAuthenticated")
	@GetMapping("/question")
	public String questionForm(Principal principal,
			QuestionForm questionForm) {

		return "question";
	}

	@PreAuthorize("isAuthenticated")
	@PostMapping("/question")
	public String question(
			Principal principal,
			@Valid QuestionForm questionForm,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors())
			return "question";

		SbbUser sbbUser = this.userService.getUser(principal.getName());
		this.questionService.create(
			questionForm.getSubject(),
			questionForm.getContent(),
			sbbUser
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

	@PreAuthorize("isAuthenticated")
	@PostMapping("/detail/{id}")
	public String answer(Model model,
			Principal principal,
			@PathVariable("id") Integer id,
			@Valid AnswerForm answerForm,
			BindingResult bindingResult) {

		Question question = this.questionService.getQuestion(id);
		SbbUser sbbUser = this.userService.getUser(principal.getName());
		if (bindingResult.hasErrors()) {
			model.addAttribute("question", question);
			return "detail";
		}
		Answer answer = this.answerService.create(question,
							answerForm.getContent(), sbbUser);

		return String.format("redirect:/detail/%s#answer_%s",
					id, answer.getId());
	}

	@PreAuthorize("isAuthenticated")
	@GetMapping("/modify/{id}")
	public String modify(Principal principal,
			QuestionForm questionForm,
			@PathVariable("id") Integer id) {

		Question question = this.questionService.getQuestion(id);
		if(!question.getAuthor().getName().equals(principal.getName())) {
			throw new ResponseStatusException(
						HttpStatus.BAD_REQUEST,
						"수정권한이 없습니다.");
		}
		questionForm.setSubject(question.getSubject());
		questionForm.setContent(question.getContent());

		return "question";
	}

	@PreAuthorize("isAuthenticated")
	@PostMapping("/modify/{id}")
	public String modify(Principal principal,
			@Valid QuestionForm questionForm,
			@PathVariable("id") Integer id,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return "question";
		}
		Question question = this.questionService.getQuestion(id);
		if(!question.getAuthor().getName().equals(principal.getName())) {
			throw new ResponseStatusException(
						HttpStatus.BAD_REQUEST,
						"수정권한이 없습니다.");
		}
		this.questionService.modify(question,
				questionForm.getSubject(),
				questionForm.getContent());

		return String.format("redirect:/detail/%s", id);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{id}")
	public String delete(Principal principal,
			@PathVariable("id") Integer id) {

		Question question = this.questionService.getQuestion(id);
		if (!question.getAuthor().getName().equals(principal.getName())) {
			throw new ResponseStatusException(
						HttpStatus.BAD_REQUEST,
						"삭제권한이 없습니다.");
		}
		this.questionService.delete(question);

		return "redirect:/";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/vote/{id}")
	public String questionVote(Principal principal,
			@PathVariable("id") Integer id) {

		Question question = this.questionService.getQuestion(id);
		SbbUser sbbUser = this.userService.getUser(principal.getName());
		this.questionService.vote(question, sbbUser);

		return String.format("redirect:/detail/%s", id);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/answer/modify/{id}")
	public String answerModify(Principal principal,
			AnswerForm answerForm,
			@PathVariable("id") Integer id) {

		Answer answer = this.answerService.getAnswer(id);
		if (!answer.getAuthor().getName().equals(principal.getName())) {
			throw new ResponseStatusException(
						HttpStatus.BAD_REQUEST,
						"수정권한이 없습니다.");
		}
		answerForm.setContent(answer.getContent());

		return "answer";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/answer/modify/{id}")
	public String answerModify(Principal principal,
			@Valid AnswerForm answerForm,
			@PathVariable("id") Integer id,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors())
			return "answer";

		Answer answer = this.answerService.getAnswer(id);
		if (!answer.getAuthor().getName().equals(principal.getName())) {
			throw new ResponseStatusException(
						HttpStatus.BAD_REQUEST,
						"수정권한이 없습니다.");
		}
		this.answerService.modify(answer, answerForm.getContent());

		return String.format("redirect:/detail/%s#answer_%s",
					answer.getQuestion().getId(), answer.getId());
	}

	@PreAuthorize("isAuthenticated()")
    @GetMapping("/answer/delete/{id}")
    public String answerDelete(Principal principal,
			@PathVariable("id") Integer id) {

		Answer answer = this.answerService.getAnswer(id);
        if (!answer.getAuthor().getName().equals(principal.getName())) {
            throw new ResponseStatusException(
						HttpStatus.BAD_REQUEST,
						"삭제권한이 없습니다.");
        }
        this.answerService.delete(answer);

		return String.format("redirect:/detail/%s", answer.getQuestion().getId());
    }

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/answer/vote/{id}")
	public String answerVote(Principal principal,
			@PathVariable("id") Integer id) {

		Answer answer = this.answerService.getAnswer(id);
		SbbUser sbbUser = this.userService.getUser(principal.getName());
		this.answerService.vote(answer, sbbUser);

		return String.format("redirect:/detail/%s#answer_%s",
					answer.getQuestion().getId(), answer.getId());
	}
}
