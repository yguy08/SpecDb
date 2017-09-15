package com.speculation1000.specdb.market;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.speculation1000.specdb.time.SpecDbDate;
import com.speculation1000.specdb.trade.TradeStatusEnum;

public class Market implements Comparable<Market> {
	
	private String base;
	
	private String counter;
	
	private String exchange;
	
	private long date;
	
	private BigDecimal close;
	
	private BigDecimal high;
	
	private BigDecimal low;
	
	private int volume;
	
	//PROTO
	private BigDecimal atr;
	
	private BigDecimal amount;
	
	private BigDecimal total;
	
	private int dayHighLow;
	
	private String direction;
	
	private BigDecimal stop;
	
	private String tradeStatus;
	
	private String toStr = null;
	
	public Market(){
		
	}
	
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
	
	public static Market createNewMarketTrade(Symbol symbol,List<Market> marketList, BigDecimal accountBalance,TradeStatusEnum tse){
		Market market = new Market();
		market.setBase(symbol.getBase());
		market.setCounter(symbol.getCounter());
		market.setExchange(symbol.getExchange());
		market.setDate(SpecDbDate.getTodayMidnightEpochSeconds(Instant.ofEpochSecond(marketList.get(0).getDate())));
		market.setClose(marketList.get(0).getClose());
		market.setVolume(marketList.get(0).getVolume());
		market.setATR(marketList);
		market.setAmount(accountBalance);
		market.setTotal();
		market.setDirection(tse);
		market.setStop();
		market.setStatus(TradeStatusEnum.NEW);
		market.toStr = market.getSymbol()+" @"+market.getClose()+" "+market.getDirection();
		return market;
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
	
	private void setStatus(TradeStatusEnum tse){
		tradeStatus = tse.getTradeStatus();
	}
	
	public void setStatus(String status){
		tradeStatus = status;
	}
	
	public String getStatus(){
		return tradeStatus;
	}
	
	@Override
	public String toString(){
		if(toStr!=null){
			return toStr;
		}else{
			return base + counter + ":" + exchange + " " + "@" + close;
		}		
	}
	
	public void setToStr(String toStr){
		this.toStr = toStr;
	}

	@Override
	public int compareTo(Market o) {
		return this.getSymbol().compareTo(o.getSymbol());
	}
}
