package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.aop.check.CheckAdminSession;
import com.example.demo.config.MessageConfig;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.User;
import com.example.demo.service.AccountService;
import com.example.demo.service.GmailService;
import com.example.demo.service.UserService;
import com.google.api.services.gmail.Gmail;

import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("/bank/admin")
public class AdminController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private GmailService gmailService;
	
	@Autowired
	private MessageConfig message;   

	// 用戶管理 ------------------------------------------------------------
  
	@GetMapping("/user-manage")
	private String manageUserPage(Model model,RedirectAttributes redirectAttributes) {
		
		// 從 Model 取得 Flash 資料
		
		List<UserDto> allUserDtos = (List<UserDto>) model.asMap().get("allUserDtos");
		
		String errorMessage = (String)model.asMap().get("errormessage");
		
		if (errorMessage != null) {
			model.addAttribute("errorMessage",errorMessage);
			model.addAttribute("allUserDtos",allUserDtos);
			return "admin_manage";
		}
		
		if (allUserDtos != null) {
			model.addAttribute("allUserDtos",allUserDtos);
			return "admin_manage";
		}
		
		// 預設回傳所有用戶資料 ( model 無資料 )
			
	    allUserDtos = userService.findAllUsers()   
	                             .stream()
	                             .filter( user-> user.getRole().equals("ROLE_USER"))
	                             .filter( user-> user.getApprove().equals("APPROVED"))
	                             .toList();    
	    				                             
		model.addAttribute("allUserDtos",allUserDtos);
		
		return "admin_manage";
	}
	
	// 管理指定用戶
	
	@PostMapping("/user-manage")	
	private String manageCertainUser(@RequestParam String idNumber,RedirectAttributes redirectAttributes) {  // redirect會清空 model 資料，因此要用 redirectAttributes 儲存
		
		List<UserDto> certainUserDtos = userService.getUserByIdNumber(idNumber);
		
		redirectAttributes.addFlashAttribute("allUserDtos",certainUserDtos);   // addFlashAttribute() 不會在請求中顯示 allUserDtos 資料 ( addAttribute 會 )
		
		// 產生 UserNotFoundException 會回傳錯誤到原頁面
		
		return "redirect:/bank/admin/user-manage";
	}
	
	// 變更用戶資料
	
	@GetMapping("/user-manage/{id}/update")
	private String adminUpdatePage(@PathVariable("id") Long userId,Model model) { 
		
		UserDto manageUserDto = userService.getUser(userId);
		
		model.addAttribute("manageUserDto",manageUserDto);
		
		
		
		return "admin_manage_update";
	}
	
	
	@PostMapping("/user-manage/{id}/update")
	private String updateUser( @RequestParam Long manageUserId ,@ModelAttribute UserDto manageUserDto) { 
		
		userService.updateUser(manageUserId,manageUserDto.getUsername(),manageUserDto.getGender(), manageUserDto.getEmail(),manageUserDto.getPhone());
					                            		
		return "redirect:/bank/admin/user-manage";
	}
	
	
	// 刪除用戶
	
	@PostMapping("/user-manage/{id}/remove")
	private String deleteUser( @PathVariable("id") Long userId) { 

		userService.deleteUser(userId);  // 刪除用戶
  		
		return "redirect:/bank/admin/user-manage";
	}
	
	
	
	// 用戶審核 --------------------------------------------------------------
	
	@GetMapping("/user-approval")
	private String approvalPage(Model model) {
		
		List<User> pendingUsers = userService.findAllApprovePendingUsers();
		
		model.addAttribute("pendingUsers", pendingUsers);
		
		return "admin_approve" ;
		
	}	
	
	// 審核用戶
	
	@PostMapping("/user-approval/approve/{id}")
	private String approveUserRegister(@PathVariable(value = "id") Long userId) {
		
		// 變更用戶 approve 狀態
		userService.approveUser(userId);
		
		// 發送成功申請郵件，包含已註冊帳號資訊
		UserDto approvedUserDto = userService.getUser(userId);

		try {  	 
			// googleMailServer 寄信
			Gmail service = gmailService.getGmailService();
	    	gmailService.sendMessage(service, "me", gmailService.createEmail(approvedUserDto.getEmail(), approvedUserDto.getUsername() + message.getApproveGmailHead() , message.getApproveGmailBody()));  // 使用 @Value 帶入的屬性
		} catch (Exception e) {
	        e.printStackTrace();
	    }
				
		return "redirect:/bank/admin/user-approval";
	}
	
	
	
	@PostMapping("/user-approval/reject/{id}")
	private String rejectUserRegister(@PathVariable(value = "id") Long userId) {
		
		// 發送申請失敗郵件
		UserDto rejectUserDto = userService.getUser(userId);
	
		try {  	 
			Gmail service = gmailService.getGmailService();
	    	gmailService.sendMessage(service, "me", gmailService.createEmail(rejectUserDto.getEmail(), rejectUserDto.getUsername() + message.getRejectGmailHead() , message.getRejectGmailBody()));  // 使用 @Value 帶入的屬性
		} catch (Exception e) {
	        e.printStackTrace();
	    }
		
		// 刪除用戶資訊
				
		userService.deleteUser(userId);
		
		return "redirect:/bank/admin/user-approval";
	}
	
	
	
	@GetMapping("/statistics")
	public String statisticPage(Model model) {
		
		// 計算性別數量
		
		Long maleCount = userService.calcUserQuantityByGender("male");
		
		Long femaleCount = userService.calcUserQuantityByGender("female");
		
		model.addAttribute("maleCount", maleCount );
		
		model.addAttribute("femaleCount", femaleCount );
		
		// 可添加其他統計 ...
		
		model.addAttribute("showChart", true );  // 用來顯示 main 模板中 GoogleChart 的 JS ( main_login_admin.html row-10 )
		
		return "admin_statistics";
	}
	
	
}
