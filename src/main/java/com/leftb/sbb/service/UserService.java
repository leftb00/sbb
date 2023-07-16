package com.leftb.sbb.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.leftb.sbb.DataNotFoundException;
import com.leftb.sbb.entity.SbbUser;
import com.leftb.sbb.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service @RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public SbbUser create(String name, String email, String password) {

		SbbUser user = new SbbUser();
		user.setName(name);
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));
		this.userRepository.save(user);

		return user;
	}

	public SbbUser getUser(String name) {
		Optional<SbbUser> sbbUser
			= this.userRepository.findByName(name);
		if(sbbUser.isEmpty())
			throw new DataNotFoundException("sbbuser not found");
	
		return sbbUser.get();
	}
}
