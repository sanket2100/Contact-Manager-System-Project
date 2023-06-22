package com.contact.manager.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.contact.manager.model.Contact;
import com.contact.manager.model.User;

public interface ContactRepo extends JpaRepository<Contact,Integer> {

	@Query("from Contact as c where c.user.id=:userId")
	//pageable required two things 1.Current page,2.Record per page
	public Page<Contact> findContactsByUser(@Param("userId") int userId,Pageable pageable);
	
	
	public List<Contact> findByNameContainingAndUser(String name,User user);
}
