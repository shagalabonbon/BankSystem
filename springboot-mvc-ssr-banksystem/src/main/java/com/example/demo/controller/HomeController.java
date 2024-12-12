package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.dto.UserDto;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/bank")
public class HomeController {
	
	@GetMapping("/index")
	public String indexPage() {
		
		return "index";
	}
	
	
	@GetMapping("/home")
	public String homePage(HttpSession session, Model model) {
		
		UserDto loginUserDto = (UserDto) session.getAttribute("loginUserDto");   // 檢查有沒有登入時存入的 Dto 資料
		
		if( loginUserDto != null) {                           
			model.addAttribute("loginUserDto",loginUserDto);  // 將 Dto 傳遞到頁面
			
			return "homepage";
		}
		
		return "redirect:/bank/login";  // 尚未登入，返回登入頁面
	}
	
	
	
	
	
	
}
