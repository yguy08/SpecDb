package com.speculation1000.specdb.dao;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.logging.Level;

import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.start.SpecDbException;

public class AccountDAO {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public static String getAccountBalance() throws SpecDbException{
		BigDecimal poloBalance = new PoloniexDAO().getAccountBalance().round(new MathContext(9, RoundingMode.HALF_UP));
        specLogger.logp(Level.INFO, AccountDAO.class.getName(), "getAccountBalance", "Got account balance!");
        //get bittrex balance
		return poloBalance.toPlainString();
	}
	
	public static String getOpenTrades() throws SpecDbException{
		new PoloniexDAO().getOpenTrades();
		return "";
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(getOpenTrades());
		} catch (SpecDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
