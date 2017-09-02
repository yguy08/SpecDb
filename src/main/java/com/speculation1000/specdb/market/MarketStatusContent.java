package com.speculation1000.specdb.market;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

public class MarketStatusContent implements Comparable<MarketStatusContent> {
	
	private String symbol;
	
	private BigDecimal currentPrice;
	
	private TreeMap<Long,BigDecimal> closePriceMap = new TreeMap<>(Collections.reverseOrder());
	
	private TreeMap<Long,BigDecimal> highPriceMap = new TreeMap<>(Collections.reverseOrder());
	
	private TreeMap<Long,BigDecimal> lowPriceMap = new TreeMap<>(Collections.reverseOrder());
	
	private TreeMap<Long,Integer> dayHighLowMap = new TreeMap<>(Collections.reverseOrder());
	
	private TreeMap<Long,BigDecimal> atrMap = new TreeMap<>(Collections.reverseOrder());
	
	private String toStr;
	
	public MarketStatusContent(String symbol, List<Market> marketList){
		setSymbol(symbol);
		setCurrentPrice(marketList.get(0).getClose());
		setClosePriceMap(marketList);
		setHighPriceMap(marketList);
		setLowPriceMap(marketList);
		setDayHighLowMap(closePriceMap);
		setAtrMap(marketList);
	}

	private void setSymbol(String symbol) {
		this.symbol = symbol;		
	}
	
	public String getSymbol(){
		return symbol;
	}
	
	private void setCurrentPrice(BigDecimal close) {
		this.currentPrice = close;		
	}
	
	public BigDecimal getCurrentPrice(){
		return currentPrice;
	}

	private void setClosePriceMap(List<Market> marketList) {
		for(int i = 0; i < marketList.size();i++){
			closePriceMap.put(marketList.get(i).getDate(), marketList.get(i).getClose());
		}
	}
	
	public TreeMap<Long,BigDecimal> getClosePriceMap(){
		return closePriceMap;
	}

	private void setHighPriceMap(List<Market> marketList) {
		for(int i = 0; i < marketList.size();i++){
			highPriceMap.put(marketList.get(i).getDate(), marketList.get(i).getClose());
		}
	}
	
	public TreeMap<Long,BigDecimal> getHighPriceMap(){
		return highPriceMap;
	}
	
	private void setLowPriceMap(List<Market> marketList) {
		for(int i = 0; i < marketList.size();i++){
			lowPriceMap.put(marketList.get(i).getDate(), marketList.get(i).getClose());
		}
	}
	
	public TreeMap<Long,BigDecimal> getLowPriceMap(){
		return lowPriceMap;
	}
	
	private void setDayHighLowMap(TreeMap<Long,BigDecimal> closePriceMap) {
		for(Entry<Long,BigDecimal> e : closePriceMap.entrySet()){
			long date = e.getKey();
			BigDecimal close = e.getValue();
			boolean hasNext = (closePriceMap.higherKey(date)) != null;
			boolean isHigh;
			long tmpDate = date;
			long dateP;
			BigDecimal closeP;
			int count = 0;
			if(hasNext){
				dateP = closePriceMap.higherKey(date);
				closeP = closePriceMap.get(dateP);
				isHigh = close.compareTo(closeP) >= 0 ? true : false;
			}else{
				dayHighLowMap.put(date,count);
				break;
			}
			while(hasNext){
			    dateP = closePriceMap.higherKey(tmpDate);
				closeP = closePriceMap.get(dateP);
			    if(isHigh){
				    if(close.compareTo(closeP) >= 0){
				    	count++;
				    }else{
				    	dayHighLowMap.put(date,count);
					    break;
				    }
			    }else{
				    if(close.compareTo(closeP) < 0){
				    	count--;
				    }else{
				    	dayHighLowMap.put(date,count);
					    break;
				    }
			    }
			    tmpDate-=86400;
				hasNext = (closePriceMap.higherKey(tmpDate)) != null;
			}			
			dayHighLowMap.put(date, count);
		}
	}
	
	public TreeMap<Long,Integer> getDayHighLowMap(){
		return dayHighLowMap;
	}
	
	private void setAtrMap(List<Market> marketList){
		Collections.reverse(marketList);
		int movingAvg = 20;
		if(marketList.size() > movingAvg){
			//set first TR for 0 position (H-L)
			BigDecimal tR = marketList.get(0).getHigh().subtract(marketList.get(0).getClose()).abs();
			atrMap.put(marketList.get(0).getDate(), tR);	
			for(int x = 1; x < movingAvg; x++){
				List<BigDecimal> trList = Arrays.asList(
						marketList.get(x).getHigh().subtract(marketList.get(x).getLow().abs(), MathContext.DECIMAL32),
						marketList.get(x).getHigh().subtract(marketList.get(x-1).getClose().abs(), MathContext.DECIMAL32),
						marketList.get(x-1).getClose().subtract(marketList.get(x).getLow().abs(), MathContext.DECIMAL32));				
					tR = tR.add(Collections.max(trList));
			}		
			tR = tR.divide(new BigDecimal(movingAvg), MathContext.DECIMAL32);		
			//initial up to MA get the same
			for(int x=1;x<movingAvg;x++){
				atrMap.put(marketList.get(x).getDate(), tR);
			}		
			//20 exponential moving average
			for(int x = movingAvg; x < marketList.size();x++){
				List<BigDecimal> trList = Arrays.asList(
						marketList.get(x).getHigh().subtract(marketList.get(x).getLow().abs(), MathContext.DECIMAL32),
						marketList.get(x).getHigh().subtract(marketList.get(x-1).getClose().abs(), MathContext.DECIMAL32),
						marketList.get(x-1).getClose().subtract(marketList.get(x).getLow().abs(), MathContext.DECIMAL32));					
						tR = tR.multiply(new BigDecimal(movingAvg - 1), MathContext.DECIMAL32)
						.add((Collections.max(trList)), MathContext.DECIMAL32).
						divide(new BigDecimal(movingAvg), MathContext.DECIMAL32);
						atrMap.put(marketList.get(x).getDate(), tR);
			}
		}		
	}
	
	public TreeMap<Long,BigDecimal> getAtrMap(){
		return atrMap;
	}
	
	public void setToStr(String toStr){
		this.toStr = toStr;
	}
	
	public String toString(){
		return toStr;
	}

	@Override
	public int compareTo(MarketStatusContent o) {
		return this.getSymbol().compareTo(o.getSymbol());
	}
}
