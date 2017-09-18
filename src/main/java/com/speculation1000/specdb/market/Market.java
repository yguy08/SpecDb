package com.speculation1000.specdb.market;

import java.math.BigDecimal;

public class Market implements Comparable<Market> {
	
	private String base;
	
	private String counter;
	
	private String exchange;
	
	private long date;
	
	private BigDecimal close;
	
	private BigDecimal high;
	
	private BigDecimal low;
	
	private int volume;
	
	public Market(){}
	
	public Market(String base, String counter, String exchange, long date, BigDecimal close, BigDecimal high, BigDecimal low, int volume){
		this.base = base;
		this.counter = counter;
		this.exchange = exchange;
		this.date = date;
		this.close = close;
		this.high = high;
		this.low = low;
		this.volume = volume;
	}
	
	public Market(String base,String counter, String exchange){
		this.base = base;
		this.counter = counter;
		this.exchange = exchange;
	}
	
	public Market(Symbol symbol,long date,BigDecimal close,int volume) {
		this.base = symbol.getBase();
		this.counter = symbol.getCounter();
		this.exchange = symbol.getExchange();
		this.date = date;
		this.close = close;
		this.volume = volume;
	}
	
	public Symbol getSymbol(){
		return new Symbol(base,counter,exchange);
	}
	
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
	
	@Override
	public String toString(){
			return base + counter + ":" + exchange + " " + "@" + close;		
	}

	@Override
	public int compareTo(Market o) {
		return this.getSymbol().compareTo(o.getSymbol());
	}
}
