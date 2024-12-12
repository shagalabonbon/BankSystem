package com.example.demo.model.entity;

import java.util.Date;

import com.example.demo.model.enums.CardType;
import com.example.demo.model.enums.CardStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Card {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long   id;
	
	private String cardNumber;      // 卡號
	
	private Date   expiryDate;      // 到期日
	
	private String cvv;             // CVV 碼 ( 可能包含 0 所以用 String )
	
	
	@Enumerated(EnumType.STRING)
	private CardType   cardType;    // 類型 ( 金融卡、簽帳卡、信用卡 )
	
	@Enumerated(EnumType.STRING)
	private CardStatus cardStatus;  // 狀態 ( 啟用、停用、過期 )
	
	// 關聯實體
	
	@ManyToOne
	@JoinColumn(name = "account_id")
	private Account account;        
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User    user;
	
	
	/*
	id：卡片的唯一標識符。
	cardNumber：卡片號碼。
	expiryDate：卡片的有效期。
	cvv：卡片的安全碼。
	accountId：與此卡片關聯的帳戶ID。
	cardType：卡片類型（例如，借記卡、信用卡）。
	status：卡片的狀態（例如，啟用、停用、過期）
	
	@Enumerated 指定如何將 enum 類型的屬性映射到資料庫
	
	*/
}
