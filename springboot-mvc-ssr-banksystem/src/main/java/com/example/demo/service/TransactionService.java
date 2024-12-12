package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.example.demo.exception.accountexception.InsufficientFundsException;
import com.example.demo.model.dto.TransactionDto;
import com.example.demo.model.entity.Transaction;
import com.example.demo.model.enums.TransactionStatus;
import com.example.demo.model.enums.TransactionType;

public interface TransactionService {
	
	// 建立交易紀錄
	Transaction createTxRecord(String fromAccountNumber,String toAccountNumber,BigDecimal amount,TransactionType transactionType,String description);     
	
	// 查詢前50筆交易歷史
	List<TransactionDto> getTop50TxHistory(Long accountId); 
	
	// 查詢所有帳戶交易紀錄
	List<TransactionDto> getAllTxHistory(Long accountId);
	
	// 查詢區間交易歷史
	List<TransactionDto> getIntervalTxHistory(Long accountId, Date startDate, Date endDate);  
	
	
	Transaction transfer(String fromAccountNumber,String toAccountNumber, BigDecimal amount, String description) ;
	
	Transaction exchange(String fromAccountNumber,String toAccountNumber,BigDecimal exchangeRate ,BigDecimal amount,String description);
}
	

