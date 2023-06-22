package com.contact.manager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


@Configuration
@EnableWebSecurity
public class MyConfig {
	
	
	
	@Autowired
	public AuthenticationSuccessHandler customSuccessHandler;
	

	@Bean
	public UserDetailsService getUserDetailsService()
	{
		return new UserDetailsServiceImpl();
	}
	
	@Bean
	public BCryptPasswordEncoder getPasswordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider getDaoAuthProvider()
	{
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		 provider.setUserDetailsService(this.getUserDetailsService());
		 provider.setPasswordEncoder(this.getPasswordEncoder());
		 
		
		return provider;
	}


	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
	{
		http.authorizeHttpRequests()
		.requestMatchers("/user/**")
        .hasRole("USER")
        .requestMatchers("/**")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .formLogin()
        .loginPage("/signin")
        .loginProcessingUrl("/login")
        .successHandler(customSuccessHandler)
        .and()
        .csrf().disable();
		
		http.authenticationProvider(getDaoAuthProvider());
		return http.build();
	}
}
