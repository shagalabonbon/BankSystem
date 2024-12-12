package com.example.demo.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.demo.exception.accountexception.InsufficientFundsException;
import com.example.demo.exception.authexception.UnauthorizedException;
import com.example.demo.exception.securityexception.PasswordInvalidException;
import com.example.demo.exception.userexception.UserAlreadyExistException;
import com.example.demo.exception.userexception.UserNotFoundException;

// 自訂例外可決定是否繼承受檢例外 ( ex.Exception )
// 也可繼承非受檢例外 ( ex.RuntimrException )，可簡潔代碼，減少顯式撰寫 throws，但拋出錯誤時仍需要處裡

// 當例外未處理時最終會由 JVM 進行錯誤堆疊 ( Stack Trace )

@ControllerAdvice
public class GlobalExceptionHandler {
	
	Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	
	@ExceptionHandler(PasswordInvalidException.class)
    public String handleSecurityException(Exception ex, Model model) {
        
		if( ex instanceof PasswordInvalidException ){
			
			System.out.println("PasswordInvalidException 發生： " + ex.getMessage());	// 記錄錯誤（可選）
		}
		
		model.addAttribute("errorMessage", ex.getMessage());   // 將錯誤消息添加到 model
		
		return "error";
		
    }
	
	
	@ExceptionHandler({UserNotFoundException.class,UserAlreadyExistException.class})
    public String handleUserException(Exception ex, Model model) {
		
		if( ex instanceof UserNotFoundException ){
			
			System.out.println("UserNotFoundException 發生： " + ex.getMessage());	// 記錄錯誤（可選）
		}
        
		if( ex instanceof UserAlreadyExistException ){
			
			logger.error( ex.getMessage() );	// 記錄錯誤（可選）
		}
		
        model.addAttribute("errorMessage", ex.getMessage());
        
        return "error";  
    }
	
	
	@ExceptionHandler(InsufficientFundsException.class)
    public String handleAccountException(Exception ex, Model model) {
        
        model.addAttribute("errorMessage", ex.getMessage());
        
        return "error";  
    }
	
	
	@ExceptionHandler(UnauthorizedException.class)
    public String handleAuthException(Exception ex, Model model) {
        
        model.addAttribute("errorMessage", ex.getMessage());
        
        return "error";  
    }
	
	
	
}
