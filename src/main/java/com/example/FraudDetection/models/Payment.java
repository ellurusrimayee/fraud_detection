package com.example.FraudDetection.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="payment")
public class Payment {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String userEmail;
	private Long fromAccount;
	private Long toAccount;
	private Long TXN_Amount;
	private Long transactionId;
	private String merchantName;
	@Column(name="mcc")
	private Long MCC;
	private Long POS_Entry_Code;
	private String FWES_Code;
	private String Currency_Code;
	private String TXN_Level;
	private String FALCONE_Score;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public Long getFromAccount() {
		return fromAccount;
	}
	public void setFromAccount(Long fromAccount) {
		this.fromAccount = fromAccount;
	}
	public Long getToAccount() {
		return toAccount;
	}
	public void setToAccount(Long toAccount) {
		this.toAccount = toAccount;
	}
	public Long getTXN_Amount() {
		return TXN_Amount;
	}
	public void setTXN_Amount(Long tXN_Amount) {
		TXN_Amount = tXN_Amount;
	}
	public Long getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}
	public String getMerchantName() {
		return merchantName;
	}
	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}
	public Long getMCC() {
		return MCC;
	}
	public void setMCC(Long mCC) {
		MCC = mCC;
	}
	public Long getPOS_Entry_Code() {
		return POS_Entry_Code;
	}
	public void setPOS_Entry_Code(Long pOS_Entry_Code) {
		POS_Entry_Code = pOS_Entry_Code;
	}
	public String getFWES_Code() {
		return FWES_Code;
	}
	public void setFWES_Code(String fWES_Code) {
		FWES_Code = fWES_Code;
	}
	public String getCurrency_Code() {
		return Currency_Code;
	}
	public void setCurrency_Code(String currency_Code) {
		Currency_Code = currency_Code;
	}
	public String getTXN_Level() {
		return TXN_Level;
	}
	public void setTXN_Level(String tXN_Level) {
		TXN_Level = tXN_Level;
	}
	public String getFALCONE_Score() {
		return FALCONE_Score;
	}
	public void setFALCONE_Score(String fALCONE_Score) {
		FALCONE_Score = fALCONE_Score;
	}
	public Payment() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "Payment [id=" + id + ", userEmail=" + userEmail + ", fromAccount=" + fromAccount + ", toAccount="
				+ toAccount + ", TXN_Amount=" + TXN_Amount + ", transactionId=" + transactionId + ", merchantName="
				+ merchantName + ", MCC=" + MCC + ", POS_Entry_Code=" + POS_Entry_Code + ", FWES_Code=" + FWES_Code
				+ ", Currency_Code=" + Currency_Code + ", TXN_Level=" + TXN_Level + ", FALCONE_Score=" + FALCONE_Score
				+ "]";
	}
	
	
}
