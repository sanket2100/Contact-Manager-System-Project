package com.contact.manager.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contact.manager.model.User;



public interface UserRepository extends JpaRepository<User, Integer>{
	
	public boolean existsByEmail(String email);
	public User findByEmail(String email);
	
	@Query("select u from User u where u.email=:email")
	public User getUserByUserName(@Param("email") String email);
	
	public User getUserByEmailAndName(String email,String name);
	

}
