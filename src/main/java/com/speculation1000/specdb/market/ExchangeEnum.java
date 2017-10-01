package com.speculation1000.specdb.market;

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

}
