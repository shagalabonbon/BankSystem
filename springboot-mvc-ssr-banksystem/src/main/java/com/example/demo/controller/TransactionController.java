package com.example.demo.controller;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.dto.ExchangeRate;
import com.example.demo.model.dto.TransactionRecordDto;
import com.example.demo.model.entity.TransactionRecord;
import com.example.demo.service.AccountService;
import com.example.demo.service.ExchangeRateService;
import com.example.demo.service.TransactionService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/* 
 * 請求方法      URL 路徑                                  功能                                                      
 * ---------------------------------------------------
 * 	 GET     /transaction/txHistory/{accountId}           查詢帳號交易紀錄     
 *   POST    /transaction/transfer                        轉帳
 *   POST    /transaction/exchange                        換匯
        
     顯示於： 
  
 * */

@Controller
@RequestMapping("/bank/transaction")   
public class TransactionController {
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private ExchangeRateService exchangeRateService;
	
	@Autowired
	private AccountService accountService;
	

	@GetMapping("/transfer")               // 轉帳頁面
	public String transferPage(Model model) {
		
		model.addAttribute("transferDto", new TransactionRecordDto()); // 當有使用 th:object，就必須要有已存入 model 的物件、或創建初始化的物件傳遞 
		
		return "transfer";            
	}
	
	@PostMapping("/transfer/check")
	public String checkTransfer(@ModelAttribute TransactionRecordDto transferDto,Model model ) {
		
		model.addAttribute("transferDto",transferDto); // 將輸入資料傳遞到確認頁
		
		return "transfer_check";
	}
	
	
	@PostMapping("/transfer/confirm")
	public String doTransfer( @ModelAttribute TransactionRecordDto transferDto,Model model) {
		
		transactionService.transfer(transferDto.getFromAccountNumber(),transferDto.getToAccountNumber(),transferDto.getAmount(),transferDto.getDescription());
		
		model.addAttribute("transferDto",transferDto);
		
		return "transfer_result"; 
	}
	
	
	// 換匯  --------------------------------------

	@GetMapping("/exchange")
	public String exchangePage(Model model) { 
		
		// 執行匯率更新
		
		List<ExchangeRate> exchangeRates = exchangeRateService.getTwdExchangeRate();
		
		model.addAttribute("exchangeRates",exchangeRates);  
		
		model.addAttribute("exchangeDto", new TransactionRecordDto()); // 傳遞初始化物件 
			
		return "exchange";
	}
	
	
	@PostMapping("/exchange/check")
	public String doExchange( @ModelAttribute TransactionRecordDto exchangeDto,@RequestParam String targetRate,Model model) {
		
		String formatAmount =  String.format("%.2f",exchangeDto.getAmount().divide(new BigDecimal(targetRate)).toString()); 
		
		model.addAttribute("exchangeDto",exchangeDto);
		
		model.addAttribute("targetRate",targetRate);
		
		model.addAttribute("formatAmount",formatAmount);
		
		return "exchange_check"; 
	}
	
	
	
	@PostMapping("/exchange/confirm") 
	public String checkExchange(@ModelAttribute TransactionRecordDto exchangeDto,@RequestParam String targetRate,@RequestParam String formatAmount,Model model) {
		
		// 進行換匯 
		
		transactionService.exchange(exchangeDto.getFromAccountNumber(),exchangeDto.getToAccountNumber(),new BigDecimal(targetRate),exchangeDto.getAmount(),exchangeDto.getDescription());
		
		model.addAttribute("exchangeDto",exchangeDto);
		
        model.addAttribute("targetRate",targetRate);
		
		model.addAttribute("formatAmount",formatAmount);
		
		return "exchange_result";
	}
	
	
	
	
	
	
	/* 匯率爬蟲 - 完成 */
	
	@GetMapping("/exchange-rate")
	public String exchangeRatePage(Model model,HttpSession session) {
		
		List<ExchangeRate> exchangeRates = exchangeRateService.getTwdExchangeRate();
		
		// 確保列表非空，並提取第一個 ExchangeRate 的更新時間
		
	    String renewTime = exchangeRates.get(0).getRenewTime();
		
		model.addAttribute("exchangeRates",exchangeRates);
		
		model.addAttribute("renewTime",renewTime);
		
		// 存入 session 以便登入期間取用 ( 測試非必須 )
		
		session.setAttribute("exchangeRates",exchangeRates);
		
		return "exchange_rate";
		
	}
	
	
	
	
	
}
