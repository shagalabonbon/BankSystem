package com.example.demo.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.dto.TransactionDto;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.entity.Account;
import com.example.demo.service.AccountService;
import com.example.demo.service.TransactionService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/bank/account")
public class AccountController {

	@Autowired
	private AccountService accountService;
	
	@Autowired
	private TransactionService transactionService;
	
	@GetMapping("/all-account")
	public String getAllUserAccounts( Model model , HttpSession session) {
		
		// session 取得 id  ( 缺錯誤處理 )
		
		UserDto loginUserDto = (UserDto)session.getAttribute("loginUserDto"); 
		
		// 尋找全部帳號，並用 model 傳遞
		
		List<Account> accounts = accountService.findAllUserAccounts(loginUserDto.getId());
		
		model.addAttribute("accounts",accounts);
		
		return "account";
	}
	
	
	@GetMapping("/{id}/transaction-history")
	public String TxHistoryPage(@PathVariable Long accountId , Model model) {
		
		// 驗證帳戶 ( 可改成從 session 獲取 )
		
		Account account = accountService.getAccount(accountId)
				                        .orElseThrow(()->new RuntimeException("帳戶不存在"));
		
		// 尋找所有交易紀錄
		
		List<TransactionDto> transactionDtos = transactionService.getAllTransactionHistory(accountId);
		
		model.addAttribute("account",account);
		
		model.addAttribute("transactionDtos",transactionDtos);
		
		return "account_tx_history";
		
	}
	
	@PostMapping("/{id}/transaction-history/between")
	public String intervalTxHistory(@PathVariable Long accountId, @RequestParam Date startDate,@RequestParam Date endDate , Model model) {
		
		List<TransactionDto> transactionDtos = transactionService.getIntervalTransactionHistory(accountId, startDate, endDate);
		
		model.addAttribute("transactionDtos",transactionDtos);
		
		return "account_tx_history";
		
	}
	
	@PostMapping("/{id}/transaction-history/top50")
	public String top50TxHistory( @RequestParam Date startDate,@RequestParam Date endDate , Model model) {
		
		
		transactionService.getTop50TransactionHistory(null)
		
		model.addAttribute(model)
		
		return "account_tx_history";
		
	}
	
	
	
	
	
	@PostMapping("/TransactionHistory/{accountId}")
	public String getTransactionHistory(@PathVariable Long accountId) {
		
		transactionService.getTop50TransactionHistory(accountId);
		
		return "account_TransactionHistory";
	}
	
	
	
	
	
}
