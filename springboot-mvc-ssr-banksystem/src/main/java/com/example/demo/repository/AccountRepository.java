package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.entity.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.example.demo.model.entity.Branch;
import com.example.demo.model.entity.User;
import com.example.demo.model.entity.Currency;





@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	
	long countByBranch(Branch branch);
	
	List<Account> findAllAccountsByUser(User user);
	
	Optional<Account> findByAccountNumber(String accountNumber);
	
	List<Account> findAllAccountByUserAndCurrency(User user,Currency currency);
 
}
