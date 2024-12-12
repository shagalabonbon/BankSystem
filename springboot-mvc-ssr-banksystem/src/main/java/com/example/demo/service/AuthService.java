package com.example.demo.service;

import com.example.demo.exception.securityexception.PasswordInvalidException;
import com.example.demo.exception.userexception.UserNotFoundException;
import com.example.demo.model.dto.UserDto;

import jakarta.servlet.http.HttpSession;

public interface AuthService {
	
	UserDto login(String idNumber, String password) throws PasswordInvalidException,UserNotFoundException;
	
    void generateToken();
}
