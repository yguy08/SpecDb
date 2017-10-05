package com.speculation1000.specdb.market;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Market implements Comparable<Market> {
	
	private Symbol symbol;
		
	private long date;
	
	private BigDecimal close;
	
	private BigDecimal high;
	
	private BigDecimal low;
	
	private int volume;
	
	private List<Market> historical;
	
	public Market(){
		
	}
	
	public Market(String base, String counter, String exchange, long date, BigDecimal close,BigDecimal high, BigDecimal low, int volume){
		this.symbol = new Symbol(base,counter,exchange);
		this.date = date;
		this.close = close;
		this.high = high;
		this.low = low;
		this.volume = volume;
		historical = new ArrayList<>();
	}
	
	public Market(Symbol s, long date, BigDecimal high,BigDecimal low, BigDecimal close, int volume){
		this.symbol = s;
		this.date = date;
		this.close = close;
		this.high = high;
		this.low = low;
		this.volume = volume;
		historical = new ArrayList<>();
	}
	
	public Market(String base,String counter, String exchange){
		this.symbol = new Symbol(base,counter,exchange);
	}
	
	public Market(Symbol symbol,long date,BigDecimal close) {
		this.symbol = symbol;
		this.date = date;
		this.close = close;
	}
	
	//Account Balance constructor
	public Market(long date,String counter,String exchange){
		this.date = date;
		if(!counter.equalsIgnoreCase("BTC")){
			this.symbol = new Symbol(counter,"BTC",exchange);
		}else{
			this.symbol = new Symbol(counter,"USDT",exchange);
		}
	}
	
	public Symbol getSymbol(){
		return symbol;
	}
	
	public void setSymbol(Symbol s){
		this.symbol = s;
	}

	public long getDate() {
		return date;
	}
	
	public void setDate(long date){
		this.date = date;
	}

	public BigDecimal getHigh() {
		return high;
	}
	
	public void setHigh(BigDecimal high){
		this.high = high;
	}

	public BigDecimal getLow() {
		return low;
	}
	
	public void setLow(BigDecimal low){
		this.low = low;
	}

	public BigDecimal getClose() {
		return close;
	}
	
	public void setClose(BigDecimal close){
		this.close = close;
	}

	public int getVolume() {
		return volume;
	}
	
	public void setVolume(int volume){
		this.volume = volume;
	}
	
	public void setHistorical(List<Market> markets){
		this.historical = markets;
	}
	
	public List<Market> getHistorical(){
		return historical;
	}
	
	@Override
	public String toString(){
			return symbol + " " + "@" + close;		
	}

	@Override
	public int compareTo(Market o) {
		return this.getSymbol().compareTo(o.getSymbol());
	}
}
