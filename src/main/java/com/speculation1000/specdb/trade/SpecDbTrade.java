package com.speculation1000.specdb.trade;

import java.math.BigDecimal;

public class SpecDbTrade {
	
	private String base;
	
	private String counter;
	
	private String exchange;
	
	private long date;
	
	private BigDecimal price;
	
	private BigDecimal amount;
	
	private BigDecimal total;
	
	private BigDecimal stop;
	
	private BigDecimal currentPrice;
	
	private boolean isOpen;
	
	public String getBase(){
		return base;
	}
	
	public void setBase(String base){
		this.base = base;
	}
	
	public String getCounter(){
		return counter;
	}
	
	public void setCounter(String counter){
		this.counter = counter;
	}

	public String getExchange() {
		return exchange;
	}
	
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public long getDate() {
		return date;
	}
	
	public void setDate(long date){
		this.date = date;
	}
	
	public BigDecimal getPrice() {
		return price;
	}
	
	public void setPrice(BigDecimal price){
		this.price = price;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount){
		this.amount = amount;
	}
	
	public BigDecimal getTotal() {
		return total;
	}
	
	public void setTotal(BigDecimal total){
		this.total = total;
	}
	
	public BigDecimal getStop() {
		return stop;
	}
	
	public void setStop(BigDecimal stop){
		this.stop = stop;
	}
	
	public BigDecimal getCurrentPrice() {
		return currentPrice;
	}
	
	public void setCurrentPrice(BigDecimal currentPrice){
		this.currentPrice = currentPrice;
	}
	
	public boolean isOpen() {
		return isOpen;
	}
	
	public void setIsOpen(boolean isOpen){
		this.isOpen = isOpen;
	}
	
	
	@Override
	public String toString(){
		return "";
	}

}
