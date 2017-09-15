package com.speculation1000.specdb.market;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.speculation1000.specdb.trade.TradeStatusEnum;

public class Entry {
	
	private String base;
	
	private String counter;
	
	private String exchange;
	
	private long date;
	
	private BigDecimal close;
	
	private int volume;
	
	private BigDecimal atr;
	
	private BigDecimal amount;
	
	private BigDecimal total;
	
	private String direction;
	
	private BigDecimal stop;
	
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

	public int getVolume() {
		return volume;
	}
	
	public void setVolume(int volume){
		this.volume = volume;
	}
	
	public BigDecimal getATR(){
		return atr.setScale(8,RoundingMode.UP);
	}
	
	private void setATR(List<Market> marketList) {
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
	
	public void setATR(BigDecimal atr){
		this.atr = atr;
	}
	
	public void setAmount(BigDecimal accountBalance) {
		BigDecimal risk = new BigDecimal(1.00).divide(new BigDecimal(100));
		BigDecimal max = accountBalance.divide(close, MathContext.DECIMAL32).setScale(9, RoundingMode.DOWN);
		BigDecimal size = accountBalance.multiply(risk, MathContext.DECIMAL32).divide(atr, MathContext.DECIMAL32).setScale(9, RoundingMode.UP);
		amount = (size.compareTo(max) > 0) ? max : size; 
	}
	
	public BigDecimal getAmount(){
		return amount.setScale(2,RoundingMode.UP);
	}
	
	public BigDecimal getTotal() {
		return total.setScale(2,RoundingMode.UP);
	}
	
	private void setTotal() {
		this.total = close.multiply(amount);
	}
	
	public void setTotal(BigDecimal total){
		this.total = total;
	}
	
	private void setDirection(TradeStatusEnum tse) {
		this.direction = tse.getTradeStatus();
	}
	
	public void setDirection(String direction){
		this.direction = direction;
	}
	
	public String getDirection(){
		return direction;
	}
	
	private void setStop() {		
		if(direction.equalsIgnoreCase(TradeStatusEnum.LONG.getTradeStatus())){
			stop = close.subtract(new BigDecimal(2.00).multiply(atr, MathContext.DECIMAL32));
		}else{
			stop = close.add(new BigDecimal(2.00).multiply(atr, MathContext.DECIMAL32));
		}
	}
	
	public void setStop(BigDecimal stop){
		this.stop = stop;
	}
	
	public BigDecimal getStop(){
		return stop.setScale(8,RoundingMode.UP);
	}
	
	@Override
	public String toString(){
		return base + counter + ":" + exchange + " " + "@" + close;
	}	

}
