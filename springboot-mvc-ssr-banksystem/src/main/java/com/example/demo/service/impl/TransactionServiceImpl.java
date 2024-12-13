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
public class TransactionServiceImpl implements TransactionService {
	
	@Autowired
	private TransactionRecordService transactionRecordService ;
	
	@Autowired
	private TransactionRecordRepository transactionRecordRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private ModelMapper modelMapper;

	
	// 轉帳 ( 差錯誤處理 )

	@Override
	@Transactional(
		propagation = Propagation.REQUIRES_NEW,
		isolation = Isolation.READ_COMMITTED,
		rollbackFor = {RuntimeException.class}
    )
	public TransactionRecord transfer(String fromAccountNumber,String toAccountNumber, BigDecimal amount, String description) {
		
		TransactionRecord transactionRecord = new TransactionRecord();
		
		try {
		
			// 確認用戶
				
		    Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
		                                           .orElseThrow(() -> new AccountNotFoundException("來源帳戶不存在"));
	
		    Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
		                                         .orElseThrow(() -> new AccountNotFoundException("目標帳戶不存在"));
			
			// 驗證金額
		    
		    if( amount.compareTo(BigDecimal.ZERO) <= 0 ) {	         // 轉帳金額需大於 0
		    	throw new RuntimeException("轉帳金額需大於 0");
		    }
		    
		    if( amount.compareTo(fromAccount.getBalance()) > 0 ) {	 // 轉帳金額需小於帳戶餘額
		    	throw new RuntimeException("帳戶餘額不足");
		    }
		    
		    // 進行轉帳
		    
		    BigDecimal fromAccountNewBalance = fromAccount.getBalance().subtract(amount); 
		    
		    BigDecimal toAccountNewBalance = toAccount.getBalance().add(amount); 
		    
		    fromAccount.setBalance(fromAccountNewBalance);
		    
		    toAccount.setBalance(toAccountNewBalance);
			
		    accountRepository.save(fromAccount);
		    
		    accountRepository.save(toAccount);
		    
			// 創建交易紀錄並設置成功狀態
		    
		    transactionRecord = transactionRecordService.createTransactionRecord( 
		    		            	fromAccount.getAccountNumber(),
		    		                toAccount.getAccountNumber(),
		    		                amount,
		    		                TransactionType.Transfer,
		    		                description );
		    
		    transactionRecord.setStatus(TransactionStatus.Success);
		    
		}catch (Exception e) {
			
			transactionRecord = transactionRecordService.createTransactionRecord( 
					fromAccountNumber,
					toAccountNumber,
	                amount,
	                TransactionType.Transfer,
	                "交易失敗：" + e.getMessage() );

            transactionRecord.setStatus(TransactionStatus.Failed);
 
		}
		
		transactionRecordRepository.save(transactionRecord);  // 儲存交易紀錄
	    
		return transactionRecord;
	}

	// 換匯
	
	@Override
	public TransactionRecord exchange(String fromAccountNumber,String toAccountNumber, BigDecimal exchangeRate, BigDecimal amount ,String description) {
		
		TransactionRecord transactionRecord = new TransactionRecord();
		
		try {
			
			// 確認帳戶
			
			Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
	                                               .orElseThrow(() -> new RuntimeException("來源帳戶不存在"));
			
			Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
	                                             .orElseThrow(() -> new RuntimeException("來源帳戶不存在"));
			
			// 驗證金額
		    
		    if( amount.compareTo(BigDecimal.ZERO) <= 0 ) {	         // 轉帳金額需大於 0
		    	throw new RuntimeException("轉帳金額需大於 0");
		    }
		    
		    if( amount.compareTo(fromAccount.getBalance()) > 0 ) {	 // 轉帳金額需小於帳戶餘額
		    	throw new RuntimeException("帳戶餘額不足");
		    }
			
			// 進行換匯 ( 賣出 )
			
			BigDecimal exchangeAmount = amount.divide(exchangeRate); 
					
			fromAccount.setBalance( fromAccount.getBalance().subtract(amount) );   // 台幣帳戶減少
			
			toAccount.setBalance( toAccount.getBalance().add(exchangeAmount) );    // 外幣帳戶增加
			
			accountRepository.save(fromAccount);
			
			accountRepository.save(toAccount);
			
			// 創建交易紀錄並設置成功狀態
		    
		    transactionRecord = transactionRecordService.createTransactionRecord( 
		    						fromAccountNumber,
		    						toAccountNumber,
		    		                amount,
		    		                TransactionType.Transfer,
		    		                description
		    		            );
		    
		    transactionRecord.setStatus(TransactionStatus.Success);
						
		}catch (Exception e) {
			
			// 設置交易狀態為失敗，並記錄失敗原因
			transactionRecord.setStatus(TransactionStatus.Failed);
			transactionRecord.setDescription("交易失敗：" + e.getMessage());
		}
		
		
		transactionRecordRepository.save(transactionRecord);  // 儲存交易紀錄
		
		return transactionRecord;
	}



}
