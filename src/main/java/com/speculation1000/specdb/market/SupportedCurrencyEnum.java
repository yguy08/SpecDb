package com.speculation1000.specdb.market;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public enum SupportedCurrencyEnum {
	
	BTC("BTC"),
	USDT("USDT"),
	USD("USD"),
	ETH("ETH");
	
	private String counter;
	
	SupportedCurrencyEnum(String counter){
		this.counter = counter;
	}
	
	public String getCounter(){
		return counter;
	}
	
	//helper list
	private static List<String> counterList;
	
	static {
		counterList = new ArrayList<>();
		for(SupportedCurrencyEnum s : values()) {
			counterList.add(s.getCounter());
		}
	}
	
	public static List<String> getCounterList(){
		return counterList;
	}
	
	public static String supportedCurrencyStr(){
		StringJoiner sj = new StringJoiner(":", "[", "]");
		for(SupportedCurrencyEnum sce : values()){
			sj.add(sce.getCounter());
		}
		return sj.toString();
	}

}
