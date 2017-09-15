package com.speculation1000.specdb.trade;

public enum TradeStatusEnum {
	
	NEW("NEW"),
	PENDING("PENDING"),
	OPEN("OPEN"),
	EXPIRED("EXPIRED"),
	CLOSED("CLOSED"),
	LONG("LONG"),
	SHORT("SHORT");
	
	private String tradeStatus;
	
	TradeStatusEnum(String tradeStatus){
		this.tradeStatus = tradeStatus;
	}
	
	public String getTradeStatus(){
		return tradeStatus;
	}

}
