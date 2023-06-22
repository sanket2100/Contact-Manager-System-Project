package com.contact.manager.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.contact.manager.dao.UserRepository;
import com.contact.manager.helper.Message;
import com.contact.manager.model.User;
import com.contact.manager.service.UserImpl;

import jakarta.servlet.http.HttpSession;


@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserImpl userImpl;
	
	
	//Home handler
	@RequestMapping("/")
	public String home(Model m)
	{
		m.addAttribute("title","Home-Smart Contact Manager");
		return"home";
	}
	
	//About handler
	@RequestMapping("/about")
	public String about(Model m)
	{
		m.addAttribute("title","About-Smart Contact Manager");
		return"about";
	}
	
	@GetMapping("/signin")
	public String login(Model model)
	{
		model.addAttribute("title","Login Page");
		return"login";
	}
	
	//SignUp handler
	@RequestMapping("/signup")
	public String signUp(Model m)
	{
		m.addAttribute("title","SignUp-Smart Contact Manager");
		m.addAttribute("user",new User());
		return"signup";
	}
	
	//UserRegistration handler
	@RequestMapping(value = "/do_register",method = RequestMethod.POST)
	public String registerUser(@ModelAttribute("user") User user,@RequestParam(value = "agreement",defaultValue = "false") boolean agreement,Model m,HttpSession session)
	{
		try {
			if(!agreement)
			{
				System.out.println("You have not agreed terms and conditions");
				throw new Exception("You have not agreed terms and conditions");
			}
			
			
			user.setEnabled(true);
			
			System.out.println("Agreement"+agreement);
			System.out.println("User"+user);
			
			User result=this.userImpl.createUser(user);
			m.addAttribute("user",new User());
			session.setAttribute("message",new Message("successfully registered!!","alert-success"));
			return"signup";
			
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message",new Message("something went wrong"+e.getMessage(),"alert-error"));
			return"signup";
		}
		
		
	}
	
	// Forgot password url
	@RequestMapping("/forgot-password")
	public String forgotPassword(Model m) {
		m.addAttribute("title", "ForgotPassword");
		return "forgot";
	}

	// change password url
	@RequestMapping("/change-password/{id}")
	public String changePassword(@PathVariable("id") int id, Model m) {
		m.addAttribute("title", "ChangePassword");
		m.addAttribute("id", id);
		return "change-password";
	}
		
	// verify details
	@RequestMapping(value = "/verify-details", method = RequestMethod.POST)
	public String verifyDetails(@RequestParam("e1") String e1, @RequestParam("cont") String cont, Model m,
			HttpSession session) {
		User user = this.userRepository.getUserByEmailAndName(e1, cont);
		if (user != null) {
			return "redirect:/change-password/" + user.getId();
		} else {
			session.setAttribute("message", new Message("Email or User Name Missmatch", "alert-danger"));
			return "redirect:/forgot-password";
		}
	}

	// handle change password
	@RequestMapping(value = "/resetpassword", method = RequestMethod.POST)
	public String resetPassword(@RequestParam("p1") String p1, @RequestParam("p2") String p2,
			@RequestParam("id") int id, HttpSession session) {
		if (p1.equals(p2)) {
			User user = this.userRepository.findById(id).get();
			user.setPassword(this.passwordEncoder.encode(p1));
			User save = this.userRepository.save(user);
			session.setAttribute("message", new Message("Password changed Successfully", "alert-success"));
			return "redirect:/signin";
		} else {
			session.setAttribute("message", new Message("Something went wrong!!", "alert-danger"));
			return "redirect:/change-password/" + id;
		}
	}
		
}
