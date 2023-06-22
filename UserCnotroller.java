	package com.contact.manager.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.contact.manager.dao.ContactRepo;
import com.contact.manager.dao.UserRepository;
import com.contact.manager.helper.Message;
import com.contact.manager.model.Contact;
import com.contact.manager.model.User;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/user")
public class UserCnotroller {

	@Autowired
	private BCryptPasswordEncoder psaawordEncoder;
	
	
	@Autowired
	private ContactRepo contactRepo;
	
	@Autowired
	private UserRepository userRepo;

	//getting logedin user details
	@ModelAttribute
	public void addCommonData(Model m,Principal p)
	{
		String username = p.getName();
		User user = this.userRepo.getUserByUserName(username);
		m.addAttribute("user", user);
		
	}
	
	//index page url
	@RequestMapping("/index")
	public String dashboard(Model m,Principal p)
	{
		m.addAttribute("title","User-Dashboard");
		return"redirect:/user/home";
	}
	
	
	//home page url
	@RequestMapping("/home")
	public String userHome(Model m)
	{
		m.addAttribute("title","User-Home");
		return"normal/userhome";
	}
	
	
	@RequestMapping(value = "/login",method = RequestMethod.POST)
	public String login()
	{
		return"login";
	}
	
	
	//Add contact form
	@GetMapping("/add-contact")
	public String addContact(Model m)
	{
		m.addAttribute("title","User-Add-Contact");
		m.addAttribute("contact",new Contact());
		return"normal/addContact";
	}
	
	//add contact url handling
	@PostMapping("/addcontact")
	public String handleAddContact(@ModelAttribute Contact contact, Principal p,HttpSession session)
	{
		try {
			String name = p.getName();
			User user = this.userRepo.getUserByUserName(name);
			contact.setUser(user);
			user.getContacts().add(contact);
			User save = this.userRepo.save(user);
			session.setAttribute("message",new Message("Contact Added Succesfully!!!","alert-success"));
		} catch (Exception e) {
			session.setAttribute("message",new Message("Something went wrong!!!","alert-danger"));

			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return"redirect:/user/add-contact";
	}
	
	//show contacts Through pegination
	@GetMapping("/view-contact/{page}")
	public String viewContact(@PathVariable("page") Integer page,Model m,Principal p)
	{
		m.addAttribute("title","User-ViewContact");
		String name = p.getName();
		User user = this.userRepo.getUserByUserName(name);
		
		//pageable required two things 1.Current page,2.Record per page
		Pageable pageable = PageRequest.of(page,4);
		
		Page<Contact> list = this.contactRepo.findContactsByUser(user.getId(),pageable);
		m.addAttribute("cont", list);
		m.addAttribute("currentPage",page);
		m.addAttribute("totalPages",list.getTotalPages());
		return"normal/viewContact";
	}
	
	//showing particular contact
	@RequestMapping("/{cid}/contact")
	public String showContact(@PathVariable("cid") Integer cid,Model m,Principal p)
	{
		m.addAttribute("title","User-PerticularContact");
		Optional<Contact> findById = this.contactRepo.findById(cid);
		Contact contact=findById.get();
		 
		String name = p.getName();
		User user = this.userRepo.getUserByUserName(name);
		if(user.getId()==contact.getUser().getId())
			m.addAttribute("contact", contact);
		
		return"normal/show-contact";
	}
	
	//delete contact
	@RequestMapping("/delete/{cid}")
	@Transactional
	public String deleteContact(@PathVariable("cid") Integer cid,Principal p)
	{
		Optional<Contact> id = this.contactRepo.findById(cid);
		Contact contact=id.get();
		
		User user=this.userRepo.getUserByUserName(p.getName());
		user.getContacts().remove(contact);
		
		this.userRepo.save(user);
		
		return"redirect:/user/view-contact/0";
	}
	
	
	//Upadating contact
	@RequestMapping("/update/{cid}")
	public String updateContact(@PathVariable("cid") Integer cid,Model m,Principal p)
	{
		Contact contact = this.contactRepo.findById(cid).get();
		m.addAttribute("title","User-UpdateContact");

		String name = p.getName();
		User user = this.userRepo.getUserByUserName(name);
		if(user.getId()==contact.getUser().getId())
		m.addAttribute("contact", contact);
		return"normal/update-contact";
	}
	
	//Handling update Contact
	@PostMapping("/updatecontact")
	public String updateContactHandler(@ModelAttribute Contact contact,Model m,Principal p)
	{
		m.addAttribute("title","User-UpdateContact");
		String name = p.getName();
		User user = this.userRepo.getUserByUserName(name);
		contact.setUser(user);
		this.contactRepo.save(contact);
		return"redirect:/user/"+contact.getCid()+"/contact";
	}
	
	//view profile
	@RequestMapping("/profile")
	public String viewProfile(Model m)
	{
		m.addAttribute("title","User-Profile");
		return"/normal/profile";
	}
	
	//search handler
	@RequestMapping("/search/{query}")
	@ResponseBody
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal p)
	{
	
		User user=this.userRepo.getUserByUserName(p.getName());
		List<Contact> contacts = this.contactRepo.findByNameContainingAndUser(query, user);
		System.out.println(contacts);
		return ResponseEntity.ok(contacts);
	}
	
	//open Setting handler
	@RequestMapping("/setting")
	public String openStetting(Model m)
	{
		m.addAttribute("title", "User-Setting");
		return"/normal/setting";
	}
	

	
	//handling changePassword
	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public String handleChangePassword(@RequestParam("p1") String p1,@RequestParam("p2") String p2,Model m,Principal p,HttpSession session)
	{
		String name = p.getName();
		
		User user = this.userRepo.getUserByUserName(name);
		
		if(this.psaawordEncoder.matches(p1,user.getPassword()))
		{
			user.setPassword(this.psaawordEncoder.encode(p2));
			this.userRepo.save(user);
			return"redirect:/user/home";
		}
		else {
			session.setAttribute("message",new Message("Old Password Not Matches!!!","alert-danger"));
			return"redirect:/user/setting";
		}
		
		
	}
	
	
}