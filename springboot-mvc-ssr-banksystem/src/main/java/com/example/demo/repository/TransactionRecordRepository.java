package com.example.demo.repository;


import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.TransactionRecord;

@Repository
public interface TransactionRecordRepository extends JpaRepository<TransactionRecord,Long>  {
	
	// 尋照前50筆
	List<TransactionRecord> findTop50ByAccountIdOrderByTransactionTimeDesc(Long accountId);
	
	// 尋找所有
	List<TransactionRecord> findByAccountIdOrderByTransactionTimeDesc(Long accountId);
	
	// 尋找區間 (修正)
	@Query(value = "SELECT * From transaction_record WHERE account_id=:accountId AND transaction_time Between :startDate AND :endDate order by transaction_time desc" , nativeQuery = true)
	List<TransactionRecord> findRecordsByChosenTime( @Param("accountId") Long accountId, 
			                     	                 @Param("startDate") Date startDate,
			                                         @Param("endDate")   Date endDate);

}
