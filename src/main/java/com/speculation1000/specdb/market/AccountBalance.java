package com.speculation1000.specdb.market;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountBalance extends Market {
	
	private BigDecimal amount;
	
	public AccountBalance(){}
	
	public AccountBalance(long date, String counter, String exchange, BigDecimal amount){
		super(date,counter,exchange);
		this.amount = amount;
	}
	
	public AccountBalance(Symbol s,long date,BigDecimal amount){
		super(s.getBase(),s.getCounter(),s.getExchange());
		setDate(date);
		this.amount = amount;
	}
	
	public void setAmount(BigDecimal amount){
		this.amount = amount;
	}
	
	public BigDecimal getAmount(){
		return amount;
	}
	
	public static List<Symbol> getSymbolsListAccBalList(List<AccountBalance> accBalList){
		List<Symbol> symbolList = new ArrayList<>();
		for(AccountBalance ab : accBalList){
			if(!ab.getSymbol().getCounter().equalsIgnoreCase("BTC")){
				symbolList.add(new Symbol(ab.getSymbol().getCounter(),"BTC",ab.getSymbol().getExchange()));
			}else{
				symbolList.add(new Symbol(ab.getSymbol().getCounter(),"USDT",ab.getSymbol().getExchange()));
			}
		}
		return symbolList;			
	}
	
	@Override
	public String toString() {
		return getSymbol().getCounter() + ":" + getSymbol().getExchange() + " " + SymbolsEnum.POUND.getSymbol() + getAmount(); 
	}

}
