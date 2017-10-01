package com.speculation1000.specdb.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class SpecDbNumFormat {
	
	public static BigDecimal bdToEightDecimal(BigDecimal bd){
		return bd.setScale(8, RoundingMode.HALF_EVEN);
	}
	
	public static String prettySatsPrice(BigDecimal price){
		price = price.movePointRight(8);
		String pattern = "###,###,###";
		String number = new DecimalFormat(pattern).format(price);
		return number;
	}
	
	public static String prettyUSDPrice(BigDecimal price){
		String pattern = "###,###,###.##";
		String number = new DecimalFormat(pattern).format(price);
		return number;
	}

}
