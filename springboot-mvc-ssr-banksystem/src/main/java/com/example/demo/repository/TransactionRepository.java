package com.example.demo.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.Account;
import com.example.demo.model.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long>  {
	
	// 尋照前50筆
	List<Transaction> findTop50ByAccountIdOrderByTransactionTimeDesc(Long accountId);
	
	// 尋找所有
	List<Transaction> findByAccountIdOrderByTransactionTimeDesc(Long accountId);
	
	// 尋找區間 (待修正)
	@Query("SELECT * From transaction_record WHERE account_id=:accountId AND transaction_time between :startDate AND :endDate order by transaction_time desc")
	List<Transaction> findByAccountIdAndTransactionTimeBetweenOrderByTransactionTimeDesc(Long accountId,Date startDate ,Date endDate);
}
