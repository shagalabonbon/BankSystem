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
import com.example.demo.model.dto.TransactionDto;
import com.example.demo.model.entity.Account;
import com.example.demo.model.entity.Transaction;
import com.example.demo.model.enums.TransactionStatus;
import com.example.demo.model.enums.TransactionType;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {
	
	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private ModelMapper modelMapper;

	
	@Override
	public Transaction createTransactionRecord( String fromAccountNumber,String toAccountNumber,BigDecimal amount,TransactionType transactionType,String description ) {
		
		Transaction newTransaction = new Transaction();
		
		newTransaction.setFromAccountNumber(fromAccountNumber);
		newTransaction.setToAccountNumber(toAccountNumber);
		newTransaction.setAmount(amount);
		newTransaction.setTransactionType(transactionType);
		newTransaction.setStatus(TransactionStatus.Pending);
		newTransaction.setDescription(description);
		newTransaction.setTransactionTime(new Timestamp(System.currentTimeMillis()));
		newTransaction.setAccount(accountRepository.findByAccountNumber(fromAccountNumber)
				                                   .orElseThrow(()->new AccountNotFoundException("帳戶不存在")));
		
		return newTransaction;
						
	}
	
	
	@Override
	public List<TransactionDto> getAllTransactionHistory(Long accountId){
		
		List<TransactionDto> allTransactionHistory = transactionRepository.findByAccountIdOrderByTransactionTimeDesc(accountId)
                												          .stream()
                												          .map(tx -> modelMapper.map(tx,TransactionDto.class))
                												          .toList();
		return allTransactionHistory; 
	}
	

	@Override
	public List<TransactionDto> getTop50TransactionHistory(Long accountId) {
		
		// session 獲取 id ( 可修改 )
		
		// 獲取前50筆紀錄	 	                           
				                           
		List<TransactionDto> Top50TransactionHistory = transactionRepository.findTop50ByAccountIdOrderByTransactionTimeDesc(accountId)
				                                                            .stream()
				                                                            .map(tx -> modelMapper.map(tx,TransactionDto.class))
				                                                            .toList();			                           
		return Top50TransactionHistory;
	}
	
	
	@Override
	public List<TransactionDto> getIntervalTransactionHistory(Long accountId, Date startDate, Date endDate) {
		
		java.sql.Date sqlStartDate = new java.sql.Date(startDate);
		
		java.sql.Date sqlEndDate   = new java.sql.Date(endDate);
		
		transactionRepository.findByAccountIdAndTransactionTimeBetweenOrderByTransactionTimeDesc(accountId, startDate, endDate);
		
		return null;
	}
	
	
	
	// 轉帳 ( 差錯誤處理 )


	@Override
	@Transactional(
		propagation = Propagation.REQUIRES_NEW,
		isolation = Isolation.READ_COMMITTED,
		rollbackFor = {RuntimeException.class}
    )
	public Transaction transfer(String fromAccountNumber,String toAccountNumber, BigDecimal amount, String description) {
		
		Transaction transactionRecord = new Transaction();
		
		try {
		
			// 確認用戶
				
		    Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
		                                           .orElseThrow(() -> new RuntimeException("來源帳戶不存在"));
	
		    Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
		                                         .orElseThrow(() -> new RuntimeException("目標帳戶不存在"));
			
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
		    
		    transactionRecord = createTransactionRecord( 
		    		            	fromAccount.getAccountNumber(),
		    		                toAccount.getAccountNumber(),
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
		
		transactionRepository.save(transactionRecord);  // 儲存交易紀錄
	    
		return transactionRecord;
	}

	// 換匯
	
	@Override
	public Transaction exchange(String fromAccountNumber,String toAccountNumber, BigDecimal exchangeRate, BigDecimal amount ,String description) {
		
		Transaction transactionRecord = new Transaction();
		
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
		    
		    transactionRecord = createTransactionRecord( 
		    		            	fromAccount.getAccountNumber(),
		    		                toAccount.getAccountNumber(),
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
		
		
		transactionRepository.save(transactionRecord);  // 儲存交易紀錄
		
		return transactionRecord;
	}



}
