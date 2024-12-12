package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.exception.branchexception.BranchNotFoundException;
import com.example.demo.exception.currencyexception.CurrencyNotFoundException;
import com.example.demo.exception.userexception.UserNotFoundException;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.User;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/bank/admin")
public class AdminController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/login")
	private String adminLoginPage() {
		
		return "admin_login";
	}
	
	
	@PostMapping("login")
	private String adminLogin() {
			
		return "admin_homepage" ;
		
	}
	

	// 用戶管理
	
	@GetMapping("/user-manage")
	private String manageUserPage(Model model) {
		
		List<UserDto> allUserDtos = userService.findAllUsers();
		
		model.addAttribute("allUserDtos",allUserDtos);
		
		return "admin_manage";
	}
	
	// 用戶審核
	
	@GetMapping("/user-approval")
	private String approvalPage(Model model) {
		
		List<User> pendingUsers = userService.findAllApprovePendingUsers();
		
		model.addAttribute("pendingUsers", pendingUsers);
		
		return "admin_approve" ;
		
	}	
	
	// ok
	
	@PostMapping("/user-approval/approve/{id}")
	private String approveUserRegister(@PathVariable(value = "id") Long userId) throws UserNotFoundException,BranchNotFoundException,CurrencyNotFoundException {
		
		// 變更用戶 approve 狀態
		
		userService.approveUser(userId);
		
		// 發送成功申請郵件，包含已註冊帳號資訊
		
		
		return "redirect:/bank/admin/user-approval";
	}
	
	
	
	@PostMapping("/user-approval/reject/{id}")
	private String rejectUserRegister(@PathVariable(value = "id") Long userId) throws UserNotFoundException {
		
		// 刪除用戶資訊
		
		userService.deleteUser(userId);
		
		// 發送申請失敗郵件
		
		
		
		
		return "redirect:/bank/admin/user-approval";
	}
	
	
	
	
	
	
	
}
