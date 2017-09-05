package com.speculation1000.specdb.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SpecDbNumFormat {
	
	public static BigDecimal bdToEightDecimal(BigDecimal bd){
		return bd.setScale(8, RoundingMode.HALF_EVEN);
	}

	public static void main(String[] args) {
		

	}

}
