package com.example.demo.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.exception.securityexception.PasswordInvalidException;
import com.example.demo.exception.userexception.UserNotFoundException;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import com.example.demo.service.PasswordService;

import jakarta.servlet.http.HttpSession;

@Service
public class AuthServiceImpl implements AuthService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private PasswordService passwordService;
	
	@Override
	public UserDto login(String idNumber, String password) throws PasswordInvalidException,UserNotFoundException {
		
		// 尋找用戶
		
		User loginUser = userRepository.findByIdNumber(idNumber)
				                       .orElseThrow( ()->new UserNotFoundException("用戶不存在") );
		
		// 驗證密碼
		
		if(!passwordService.verifyPassword(password,loginUser.getHashPassword())){
			throw new PasswordInvalidException("密碼錯誤");
		}
		
		return modelMapper.map(loginUser,UserDto.class); 
		
	}


	@Override
	public void generateToken() {
		

	}

}
