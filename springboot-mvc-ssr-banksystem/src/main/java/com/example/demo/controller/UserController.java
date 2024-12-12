package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.exception.userexception.UserNotFoundException;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.User;
import com.example.demo.service.AccountService;
import com.example.demo.service.TransactionService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;



/* 使用者
 * 
 * 請求方法      URL 路徑                功能                                                      
 * ---------------------------------------------------
	 GET     /user                      查詢全部 
     GET     /user/get?username=admin   查詢單筆
     POST    /user/register             新增單筆
     POST    /user/update?userId=1      修改單筆
     GET     /user/delete?userId=1      刪除單筆
     
     GET     /user/{id}/{accountId}/txHistory       用戶交易紀錄
     
     
     顯示於： USER 控制後台 ( http://localhost:8086/bank/user )
     
     
 *
 * */ 


@Controller
@RequestMapping("/bank/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	
	// 註冊服務 ( OK! ) -----------------------------
		
	@GetMapping("/register")
	public String registerPage() {
		
		return "user_register";
	}
	
	@PostMapping("/register")
	public String userRegister ( @ModelAttribute User user , @RequestParam String rawPassword , Model model) {  // 輸入密碼為原始密碼 ( 尚未加鹽 )
		
		userService.register(user.getUsername(),user.getIdNumber(),rawPassword,user.getGender(),user.getEmail(),user.getPhone());
		
		return "user_register_result";   
	}  
	
	// ----------------------------------------------
	
	// @RequestParam 對應 name 屬性
	
	// 使用者更新服務 ( OK ) 
	
	@GetMapping("/update")
	public String updatePage( HttpSession session , Model model) {
		
		UserDto loginUserDto = (UserDto) session.getAttribute("loginUserDto");     // session 用來確認用戶有沒有登入
		
		if (loginUserDto == null) {
	        
	        return "redirect:/bank/login";                 // 如果沒有找到 userDto，則可以重定向到登錄頁面或顯示錯誤訊息
	    }
		
		model.addAttribute("loginUserDto", loginUserDto);  // 將 session 資料用 model 傳遞到 update.html ( th:object + th:field 會將欄位帶入已存在資料 )
			
		return "user_update";
	}
	    
	
	@PostMapping("/update/{id}")
	public String updateUserDetail( @PathVariable Long id , @ModelAttribute UserDto loginUserDto , HttpSession session) throws UserNotFoundException{
		
	    userService.updateUser(id,loginUserDto.getUsername(),loginUserDto.getGender(),loginUserDto.getEmail(),loginUserDto.getPhone());
		
	    session.setAttribute("loginUserDto", loginUserDto);  // 可選：將更新後的資料存回 session
	    
		return "user_update_result";              
	}
	
	/* 當資料完整時，th:object + th:field 將表單資料形成一個 UserDto 物件 ( 即 @ModelAttribute 標註的物件 )
	  
	   資料不足以構成一個物件，也可以用 name，會自動帶入 @ModelAttribute 標註的物件屬性 ( 如 userRegister )
	
	   直接用 @ModelAttribute (物件) + th:object 可以解決大部分狀況，缺失資料會設為 null 或空值 */
	
	// ---------------------------------------
	
	@DeleteMapping("/delete/{id}")
	public String deleteTargetUser(@PathVariable(value = "id") Long userId ) {
		
		userService.deleteUser(userId);
		
		return "redirect:/admin/user-manage";
		
	}
	
	
	
	
}
