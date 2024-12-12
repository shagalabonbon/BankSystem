package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.exception.securityexception.PasswordInvalidException;
import com.example.demo.exception.userexception.UserNotFoundException;
import com.example.demo.model.dto.UserDto;
import com.example.demo.service.AuthService;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/bank")
public class AuthController {

	@Autowired
	private AuthService authService;
	
	@GetMapping("/login")
	public String loginPage() {
		
		return "login";  
	}
	
	@PostMapping("/login")
    public String login( @RequestParam String idNumber , @RequestParam String password , HttpSession session , Model model ) throws UserNotFoundException , PasswordInvalidException {
		
		UserDto loginUserDto = authService.login(idNumber,password);
			
		session.setAttribute("loginUserDto", loginUserDto);  // 將登入者的 DTO 設入 session ( 可改為憑證 )
					
		return "redirect:/bank/home";  
	}
	
	
	@PostMapping("/logout")                      // 使用 spring security
	public String logout(HttpSession session) {
		
		session.invalidate();
		
		return "redirect:/bank/index";  
	}
	
	
	
}
