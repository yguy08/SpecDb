package com.speculation1000.specdb.market;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class MarketUtils {
	
	public static boolean isXDayHigh(List<BigDecimal> closePriceList, int days) {
		if(closePriceList.size() < days){
			days = closePriceList.size();
		}
		BigDecimal currentPrice = closePriceList.get(0);
		List<BigDecimal> subList = closePriceList.subList(0, days);
		BigDecimal max = Collections.max(subList);
		if(currentPrice.compareTo(max)>=0){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isXDayLow(List<BigDecimal> closePriceList, int days) {
		if(closePriceList.size() < days){
			days = closePriceList.size();
		}
		BigDecimal currentPrice = closePriceList.get(0);
		List<BigDecimal> subList = closePriceList.subList(0, days);
		BigDecimal min = Collections.min(subList);
		if(currentPrice.compareTo(min)<=0){
			return true;
		}else{
			return false;
		}
	}

}