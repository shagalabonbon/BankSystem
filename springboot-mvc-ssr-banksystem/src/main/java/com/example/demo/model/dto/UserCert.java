package com.example.demo.model.dto;

import lombok.Value;

// 使用者憑證

@Value
public class UserCert {
	Integer userId;       
    String  username;
	String  role; 
	
}
