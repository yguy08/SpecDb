package com.speculation1000.specdb.market;

import java.math.BigDecimal;

public class Trade extends Entry {
	
	private BigDecimal currentPrice;
	
	private BigDecimal stopPrice;
	
	private BigDecimal exitPrice;
	
	private String status;
	
	private static final String[] statusArr = {"NEW","OPEN","CLOSED"};
	
	//maybe
	private BigDecimal pyramidPrice;

}
