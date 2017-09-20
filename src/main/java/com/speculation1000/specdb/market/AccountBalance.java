package com.speculation1000.specdb.market;

import java.math.BigDecimal;

public class AccountBalance extends Market {
	
	private BigDecimal amount;
	
	public AccountBalance(){}
	
	public AccountBalance(long date, String counter, String exchange, BigDecimal amount){
		super(date,counter,exchange);
		this.amount = amount;
	}
	
	public void setAmount(BigDecimal amount){
		this.amount = amount;
	}
	
	public BigDecimal getAmount(){
		return amount;
	}

}
