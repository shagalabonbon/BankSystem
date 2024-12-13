package com.example.demo.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.exception.accountexception.AccountNotFoundException;
import com.example.demo.exception.accountexception.InsufficientFundsException;
import com.example.demo.model.dto.TransactionRecordDto;
import com.example.demo.model.entity.Account;
import com.example.demo.model.entity.TransactionRecord;
import com.example.demo.model.enums.TransactionStatus;
import com.example.demo.model.enums.TransactionType;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRecordRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TransactionRecordService;
import com.example.demo.service.TransactionService;

@Service
public class TransactionRecordServiceImpl implements TransactionRecordService {
	
	@Autowired
	private TransactionRecordRepository transactionRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private ModelMapper modelMapper;

	
	@Override
	public TransactionRecord createTransactionRecord( String fromAccountNumber,String toAccountNumber,BigDecimal amount,TransactionType transactionType,String description ) {
		
		TransactionRecord newTransactionRecord = new TransactionRecord();
		
		newTransactionRecord.setFromAccountNumber(fromAccountNumber);
		newTransactionRecord.setToAccountNumber(toAccountNumber);
		newTransactionRecord.setAmount(amount);
		newTransactionRecord.setTransactionType(transactionType);
		newTransactionRecord.setStatus(TransactionStatus.Pending);
		newTransactionRecord.setDescription(description);
		newTransactionRecord.setTransactionTime(new Timestamp(System.currentTimeMillis()));
		newTransactionRecord.setAccount(accountRepository.findByAccountNumber(fromAccountNumber)
				                                         .orElseThrow(()->new AccountNotFoundException("帳戶不存在")));
		
		return newTransactionRecord;
						
	}
	
	
	
	
	
	@Override
	public List<TransactionRecordDto> getAllTransactionHistory(Long accountId){
		
		List<TransactionRecordDto> allTransactionHistory = transactionRepository.findByAccountIdOrderByTransactionTimeDesc(accountId)
                												                .stream()
                												                .map(tx -> modelMapper.map(tx,TransactionRecordDto.class))
                												                .toList();
		return allTransactionHistory; 
	}
	

	@Override
	public List<TransactionRecordDto> getTop50TransactionHistory(Long accountId) {
		
		// session 獲取 id ( 可修改 )
		
		// 獲取前50筆紀錄	 	                           
				                           
		List<TransactionRecordDto> Top50TransactionHistory = transactionRepository.findTop50ByAccountIdOrderByTransactionTimeDesc(accountId)
				                                                            .stream()
				                                                            .map(tx -> modelMapper.map(tx,TransactionRecordDto.class))
				                                                            .toList();			                           
		return Top50TransactionHistory;
	}
	
	
	@Override
	public List<TransactionRecordDto> getIntervalTransactionHistory(Long accountId, String startDate, String endDate) {
		
		Date start = new Date(startDate);
		
		Date end   = new Date(endDate);
		
		List<TransactionRecordDto> transactionDtos = transactionRepository.findRecordsByChosenTime(accountId, start, end)
				                                                    .stream()
				                                                    .map(tx->modelMapper.map(tx, TransactionRecordDto.class))
				                                                    .toList();
		return transactionDtos;
	}
	
	

}
