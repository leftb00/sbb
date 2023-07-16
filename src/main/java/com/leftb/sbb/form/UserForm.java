package com.leftb.sbb.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class UserForm {

	@Size(min = 3, max = 25)
	@NotEmpty(message = "사용자ID는 필수 항목입니다.")
	private String name;

	@NotEmpty(message = "비밀번호는 필수 항목입니다.")
	private String password1;

	@NotEmpty(message = "비밀번호 확인은 필수 항목입니다.")
	private String password2;

	@Email
	@NotEmpty(message = "이메일은 필수 항목입니다.")
	private String email;
}
