package com.example.demo;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.model.dto.ExchangeRate;
import com.example.demo.model.dto.TransactionRecordDto;
import com.example.demo.model.entity.Account;
import com.example.demo.repository.AccountRepository;
import com.example.demo.service.AccountService;
import com.example.demo.service.ExchangeRateService;
import com.example.demo.service.TransactionRecordService;
import com.example.demo.service.impl.TransactionRecordServiceImpl;

@SpringBootTest
public class TestTransactionRecordService {
	
    @Autowired
    private TransactionRecordService transactionRecordService;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private AccountRepository accountRepository;
	
	@Test
	public void test() {
		
		List<TransactionRecordDto> Top50TransactionHistory = transactionRecordService.getAllTransactionHistory(1L);
		
		System.out.println(Top50TransactionHistory);
		
	 	Optional<Account> optAccount = accountRepository.findById(1L);
	 	
	 	System.out.println(optAccount);
		
	}
}
