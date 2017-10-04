package com.speculation1000.specdb.exchange;

import java.util.StringJoiner;

public enum ExchangeEnum {
	
	POLONIEX("POLO"),
	BITTREX("TREX");
	
	private String exchangeSymbol;
	
	ExchangeEnum(String exchangeSymbol){
		this.exchangeSymbol = exchangeSymbol;
	}
	
	public String getExchangeSymbol(){
		return exchangeSymbol;
	}
	
	public static String supportedExchangesStr(){
		StringJoiner sj = new StringJoiner(":", "[", "]");
		for(ExchangeEnum ee : values()){
			sj.add(ee.exchangeSymbol);
		}
		return sj.toString();
	}

}
