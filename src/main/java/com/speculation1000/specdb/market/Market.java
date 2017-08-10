package com.speculation1000.specdb.market;

import java.math.BigDecimal;

import com.speculation1000.specdb.time.SpecDbDate;

public class Market {
	
	private String symbol;
	
	private String exchange;
	
	private long date;
	
	private BigDecimal open;
	
	private BigDecimal close;
	
	private BigDecimal high;
	
	private BigDecimal low;
	
	private int volume;
	
	private BigDecimal trueRange;
	
	public String getSymbol(){
		return symbol;
	}
	
	public void setSymbol(String symbol){
		this.symbol = symbol;
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

	public BigDecimal getOpen() {
		return open;
	}
	
	public void setOpen(BigDecimal open){
		this.open = open;
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
	
	public BigDecimal getTrueRange(){
		return trueRange;
	}
	
	public void setTrueRange(BigDecimal trueRange){
		this.trueRange = trueRange;
	}
	
	@Override
	public String toString(){
		return symbol + " " + exchange + " " + SpecDbDate.longToLogStringFormat(date) + " " + "@" + close;
	}
}
