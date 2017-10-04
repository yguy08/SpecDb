package com.speculation1000.specdb.market;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountBalance {
	
	private Symbol symbol;
	
	private long date;
	
	private BigDecimal amount;
	
	public AccountBalance(String base,String exchange,long date,BigDecimal amount){
		this.symbol = new Symbol(base,exchange);
		this.date = date;
		this.amount = amount;
	}
	
	public AccountBalance(Symbol s,long date,BigDecimal amount){
		this.symbol = s;
		this.date = date;
		this.amount = amount;
	}
	
	public void setSymbol(Symbol s){
		this.symbol = s;
	}
	
	public void setSymbol(String s){
		this.symbol = new Symbol(s);
	}
	
	public Symbol getSymbol(){
		return symbol;
	}
	
	public void setDate(long date){
		this.date = date;
	}
	
	public long getDate(){
		return date;
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
