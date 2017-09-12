package com.speculation1000.specdb.market;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.time.SpecDbDate;

public class MarketEntry {
	
	private Symbol symbol;
	
	private long date;
	
	private BigDecimal price;
	
	private BigDecimal atr;
	
	private BigDecimal amount;
	
	private BigDecimal total;
	
	private int dayHighLow;
	
	private String direction;
	
	private BigDecimal stop;
	
	private BigDecimal accountBal;
	
	public MarketEntry(List<Market> marketList, DbConnectionEnum dbce, int highLow, BigDecimal accountBal){
		this.accountBal = accountBal;
		setSymbol(marketList.get(0).getSymbol());
		setDate(marketList.get(0).getDate());
		setPrice(marketList.get(0).getClose());
		setAtr(marketList);
		setAmount(dbce);
		setTotal();
		setDayHighLow(highLow);
		setDirection();
		setStop();
	}
	
	public MarketEntry(){
		
	}

	public Symbol getSymbol() {
		return symbol;
	}

	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}
	
	public void setSymbol(String symbol){
		this.symbol = new Symbol(symbol);
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getAtr() {
		return atr;
	}

	public void setAtr(List<Market> marketList) {
		Collections.reverse(marketList);
		int movingAvg = 20;
		BigDecimal tR = new BigDecimal(0.00);
		if(marketList.size() > movingAvg){
			//set first TR for 0 position (H-L)
			tR = marketList.get(0).getHigh().subtract(marketList.get(0).getClose()).abs();	
			for(int x = 1; x < movingAvg; x++){
				List<BigDecimal> trList = Arrays.asList(
						marketList.get(x).getHigh().subtract(marketList.get(x).getLow().abs(), MathContext.DECIMAL32),
						marketList.get(x).getHigh().subtract(marketList.get(x-1).getClose().abs(), MathContext.DECIMAL32),
						marketList.get(x-1).getClose().subtract(marketList.get(x).getLow().abs(), MathContext.DECIMAL32));				
					tR = tR.add(Collections.max(trList));
			}		
			tR = tR.divide(new BigDecimal(movingAvg), MathContext.DECIMAL32);		
			//20 exponential moving average
			for(int x = movingAvg; x < marketList.size();x++){
				List<BigDecimal> trList = Arrays.asList(
						marketList.get(x).getHigh().subtract(marketList.get(x).getLow().abs(), MathContext.DECIMAL32),
						marketList.get(x).getHigh().subtract(marketList.get(x-1).getClose().abs(), MathContext.DECIMAL32),
						marketList.get(x-1).getClose().subtract(marketList.get(x).getLow().abs(), MathContext.DECIMAL32));					
						tR = tR.multiply(new BigDecimal(movingAvg - 1), MathContext.DECIMAL32)
						.add((Collections.max(trList)), MathContext.DECIMAL32).
						divide(new BigDecimal(movingAvg), MathContext.DECIMAL32);
			}
			this.atr = tR;
		}else{
			this.atr = new BigDecimal(1.00);
		}		
	}
	
	public void setAtr(BigDecimal atr){
		this.atr = atr;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(DbConnectionEnum dbce) {
		BigDecimal risk = new BigDecimal(1.00).divide(new BigDecimal(100));
		BigDecimal max = accountBal.divide(price, MathContext.DECIMAL32).setScale(9, RoundingMode.DOWN);
		BigDecimal size = accountBal.multiply(risk, MathContext.DECIMAL32).divide(atr, MathContext.DECIMAL32).setScale(9, RoundingMode.UP);
		amount = (size.compareTo(max) > 0) ? max : size; 
	}
	
	public void setAmount(BigDecimal amount){
		this.amount = amount;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal() {
		this.total = price.multiply(amount);
	}
	
	public void setTotal(BigDecimal total){
		this.total = total;
	}

	public BigDecimal getStop() {
		return stop;
	}

	public void setStop() {
		if(direction.equalsIgnoreCase("Long")){
			stop = price.subtract(new BigDecimal(2.00).multiply(atr, MathContext.DECIMAL32));
		}else{
			stop = price.add(new BigDecimal(2.00).multiply(atr, MathContext.DECIMAL32));
		}
	}
	
	public void setStop(BigDecimal stop){
		this.stop = stop;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection() {
		if(dayHighLow > 0){
			this.direction = "Long";
		}else{
			this.direction = "Short";
		}
	}
	
	public void setDirection(String direction){
		this.direction = direction;
	}

	public int getDayHighLow() {
		return dayHighLow;
	}

	public void setDayHighLow(int highLow) {
		dayHighLow = highLow;
	}
	
	@Override
	public String toString(){
		return symbol + " " + SpecDbDate.longToLogStringFormat(date) + " " 
				+ price + " " + atr + " " + amount + " " + total + " " + dayHighLow + " " + direction + " " + stop;
	}

}
