package com.example.demo.model.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.example.demo.model.entity.Currency;
import com.example.demo.model.entity.User;
import com.example.demo.model.enums.AccountStatus;
import com.example.demo.model.enums.AccountType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* 待設計 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
	
	private Long          id;                  
	
	private String        accountNumber;    // 帳戶號碼
	
	private BigDecimal    balance;      // 帳戶餘額
	
	@Enumerated(EnumType.STRING)
	private AccountStatus status;
	
	@Enumerated(EnumType.STRING)
	private AccountType   accountType;
	
	private Currency currency;
	
}
