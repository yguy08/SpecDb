package com.speculation1000.specdb.market;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.speculation1000.specdb.dao.AccountDAO;
import com.speculation1000.specdb.dao.MarketDAO;
import com.speculation1000.specdb.start.Config;
import com.speculation1000.specdb.start.StandardMode;
import com.speculation1000.specdb.utils.SpecDbDate;
import com.speculation1000.specdb.utils.SpecDbNumFormat;

public class Entry extends Market {
	
	private BigDecimal atr;
	
	private BigDecimal amount;
	
	private BigDecimal total;
	
	private String direction;
	
	private BigDecimal stop;
	
	private int highLow;
	
	//filter for markets at high/low that are actually bad (i.e. high equals low, 1 day history, etc.)
	private boolean passFilter;
	
	public Entry() {}
	
	public Entry(Symbol symbol,List<Market> marketList,TradeStatusEnum tse,int highLow) {
		super(symbol,SpecDbDate.getLastSixHourSeconds(StandardMode.getStartRunTS()),marketList.get(0).getClose());
		
		setHighLow(highLow);
		
		//volume...
		setVolume(marketList.get(0).getVolume());
		
		//some markets at a high are not actually entries like a new market w/ only 1 day history so filter first
		filter(marketList);
		
		if(passFilter){
			setATR(marketList);
			setAmount();
			setTotal();
			setDirection(tse);
			setStop();
		}
	}
	
	public void setVolume(int volume) {
		Symbol btc = new Symbol("BTC","USDT",super.getExchange());
		Symbol eth = new Symbol("ETH","USDT",super.getExchange());
		BigDecimal btc_price = MarketDAO.getCurrentPrice(btc);
		BigDecimal eth_price = MarketDAO.getCurrentPrice(eth);
		switch(super.getCounter()) {
		case "BTC":
			super.setVolume((new BigDecimal(volume).multiply(btc_price)).intValue());
			break;
		case "ETH":
			super.setVolume((new BigDecimal(volume).multiply(eth_price)).intValue());
			break;
		default:
			super.setVolume(volume);
			break;
		}
	}
	
	public void setVolumeFromDb(int volume) {
		super.setVolume(volume);
	}

	private void filter(List<Market> marketList){
		String counter = super.getCounter();
		if(SupportedCurrenciesEnum.getCounterList().indexOf(counter)!=-1) {			
			if(getVolume() < Config.getVolLimit()) {
				passFilter = false;
			}else if(marketList.size()<5){
				passFilter = false;
			}else if(marketList.get(0).getHigh().compareTo(marketList.get(0).getLow())==0){
				passFilter = false;
			}else{
				passFilter = true;
			}
		}else {
			passFilter = false;
		}
	}
	
	public boolean passFilter(){
		return passFilter;
	}

	public BigDecimal getATR(){
		return atr.setScale(8, RoundingMode.UP);
	}
	
	private void setATR(List<Market> marketList) {
		Collections.reverse(marketList);
		int movingAvg = 10;
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
	
	public void setAmount() {
		try{
			BigDecimal risk = new BigDecimal(1.00).divide(new BigDecimal(100));
			BigDecimal max = AccountDAO.getCurrentAccountBalance().divide(super.getClose(), MathContext.DECIMAL32).setScale(9, RoundingMode.DOWN);
			BigDecimal size = AccountDAO.getCurrentAccountBalance().multiply(risk, MathContext.DECIMAL32).divide(atr, MathContext.DECIMAL32).setScale(9, RoundingMode.UP);
			amount = (size.compareTo(max) > 0) ? max : size;
		}catch(Exception e){
			
		} 
	}
	
	public void setAmount(BigDecimal amount){
		this.amount = amount;
	}
	
	public BigDecimal getAmount(){
		return amount.setScale(8, RoundingMode.UP);
	}
	
	public BigDecimal getTotal() {
		return total.setScale(8, RoundingMode.UP);
	}
	
	private void setTotal() {
		this.total = super.getClose().multiply(amount);
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
			stop = super.getClose().subtract(new BigDecimal(2.00).multiply(atr, MathContext.DECIMAL32));
		}else{
			stop = super.getClose().add(new BigDecimal(2.00).multiply(atr, MathContext.DECIMAL32));
		}
	}
	
	public void setStop(BigDecimal stop){
		this.stop = stop;
	}
	
	public BigDecimal getStop(){
		return stop.setScale(8, RoundingMode.UP);
	}
	
	@Override
	public String toString(){
		String directionArrow = (direction.equalsIgnoreCase("LONG") ? "\u25B2" : "\u25BC");
		StringBuilder sb = new StringBuilder();
		sb.append(SpecDbDate.instantToShortDateTimeStr(Instant.ofEpochSecond(super.getDate()))+"\n");
		sb.append(super.getSymbol()+directionArrow+" ("+getHighLow()+") "+SpecDbNumFormat.bdToEightDecimal(super.getClose())+" ");
		sb.append("Vol: $"+SpecDbNumFormat.prettyUSDPrice(new BigDecimal(getVolume()))+"\n");
		sb.append("ATR: "+getATR().toPlainString()+",");
		sb.append("Units: "+getAmount().toPlainString()+",");
		sb.append("Total: "+getTotal().toPlainString()+",");
		sb.append("Stop: "+getStop().toPlainString());
		return sb.toString();
	}
	
	@Override
	public int compareTo(Market e) {
		return Integer.compare(e.getVolume(), getVolume());
	}

	public void setHighLow(int highLow) {
		this.highLow = highLow;		
	}
	
	public int getHighLow() {
		return highLow;
	}

}
