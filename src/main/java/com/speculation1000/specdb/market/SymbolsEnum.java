package com.speculation1000.specdb.market;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum SymbolsEnum {
	
	UP_ARROW 		("\u25B2","Go long..."),
	DOWN_ARROW		("\u25BC", "Go short..."),
	N 				("N","Average price movement in a single day..."),
	STOP            ("\u2702","Cut your loses"),
	TOTAL_COST      ("\u03A3","Total Cost..."),
	VOLUME          ("\u221A", "Volume..."),
	POUND			("#","Amount to buy..."),
	ENTRY			("\u2600","Entry...");
	
	private String symbol;
	private String statusText;
	
	private static final List<SymbolsEnum> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
	private static final int SIZE = VALUES.size();
	private static final Random RANDOM = new Random();
	
	SymbolsEnum(String symbolStr, String status){
		symbol = symbolStr;
		statusText = status;
	}
	
	public String getSymbol(){
		return symbol;
	}
	
	public String getDescription(){
		return statusText;
	}

	public static String randomStatus()  {
		int i = RANDOM.nextInt(SIZE);
		return VALUES.get(i).getSymbol() + " = " + VALUES.get(i).getDescription();
	}
	
}
