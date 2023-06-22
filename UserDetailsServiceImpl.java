package com.contact.manager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.contact.manager.dao.UserRepository;
import com.contact.manager.model.User;


@Service
public class UserDetailsServiceImpl implements UserDetailsService{

	@Autowired
	private UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user=userRepo.getUserByUserName(username);
		if(user!=null)
		{
			return new CustomeUserDetails(user) ;
		}
		
		throw new UsernameNotFoundException("User not available");
	}

	

}
