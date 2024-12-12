package com.example.demo.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.User;

@Component
public class UserMapper {

	@Autowired
	private ModelMapper modelMapper;
	
	public UserDto toUserDto(User user) {
		
		return modelMapper.map(user, UserDto.class);
		
	}
	
	public User toUser(UserDto userDto) {
		
		return modelMapper.map(userDto, User.class);
		
	} 
	
}
