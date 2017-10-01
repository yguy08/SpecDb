package com.speculation1000.specdb.market;

import java.util.ArrayList;
import java.util.List;

public enum SupportedCurrenciesEnum {
	
	BTC("BTC"),
	USDT("USDT"),
	USD("USD"),
	ETH("ETH");
	
	private String counter;
	
	SupportedCurrenciesEnum(String counter){
		this.counter = counter;
	}
	
	public String getCounter(){
		return counter;
	}
	
	//helper list
	private static List<String> counterList;
	
	static {
		counterList = new ArrayList<>();
		for(SupportedCurrenciesEnum s : values()) {
			counterList.add(s.getCounter());
		}
	}
	
	public static List<String> getCounterList(){
		return counterList;
	}

}
