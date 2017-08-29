package com.speculation1000.specdb.market;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MarketStatus {
	
	private String symbol;
	
	private BigDecimal currentPrice;
	
	private BigDecimal highPrice;
	
	private BigDecimal lowPrice;
	
	//isEntry? entry List??
	
	//isOpen?? open list??
	
	//close? close list??
	
	//ATR
	
	//Unit size
	
	//STOP
	
	//this contains the entire list...?
	
	public MarketStatus(String symbol, List<Market> marketList){
		setSymbol(symbol);
		setCurrentPrice(marketList.get(0).getClose());
		setHighPrice(marketList);
		setLowPrice(marketList);
	}

	private void setLowPrice(List<Market> marketList) {
		List<BigDecimal> closePriceList = new ArrayList<>();
		int size;
		if(marketList.size() < 25){
			size = marketList.size();
		}else{
			size = 25;
		}
		for(int i = 0; i < size; i++){
			closePriceList.add(marketList.get(i).getClose());
		}
		this.lowPrice = Collections.min(closePriceList);
	}

	private void setHighPrice(List<Market> marketList) {
		List<BigDecimal> closePriceList = new ArrayList<>();
		int size;
		if(marketList.size() < 25){
			size = marketList.size();
		}else{
			size = 25;
		}		
		for(int i = 0; i < size; i++){
			closePriceList.add(marketList.get(i).getClose());
		}
		this.highPrice = Collections.max(closePriceList);
	}

	private void setSymbol(String symbol) {
		this.symbol = symbol;		
	}

	private void setCurrentPrice(BigDecimal close) {
		this.currentPrice = close;		
	}
	
	public String toString(){
		return this.symbol + " " + "C: " + this.currentPrice + " " + "H: " + this.highPrice + " L:" + this.lowPrice;
	}

}
