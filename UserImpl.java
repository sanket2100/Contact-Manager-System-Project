package com.contact.manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.contact.manager.dao.UserRepository;
import com.contact.manager.model.User;


@Service
public class UserImpl {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	public User createUser(User user)
	{
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRole("ROLE_USER");
		return this.userRepo.save(user);
	}
	
	public boolean checkEmail(String email)
	{
		return this.userRepo.existsByEmail(email);
	}
}
