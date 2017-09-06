package com.speculation1000.specdb.start;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;

import com.speculation1000.specdb.dao.AccountDAO;
import com.speculation1000.specdb.dao.MarketSummaryDAO;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.MarketStatusContent;
import com.speculation1000.specdb.utils.SpecDbNumFormat;

public class MarketStatus {

	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	private static List<MarketStatusContent> marketStatusList;
	
	private static BigDecimal accountBalance = null;
	
	public static void updateMarketStatusList(DbConnectionEnum dbce){
		marketStatusList = MarketSummaryDAO.getMarketStatusList(dbce);
		specLogger.logp(Level.INFO, MarketStatus.class.getName(), "updateMarketStatusList", "Retrieved market status list!");
	}
	
	public static void updateBalance(DbConnectionEnum dbce) {
		try {
			accountBalance = AccountDAO.getCurrentAccountBalance(dbce);
		} catch (SpecDbException e) {
			e.printStackTrace();
		}
		specLogger.logp(Level.INFO, MarketStatus.class.getName(), "updateMarketStatusList", "Retrieved market status list!");
	}
	
	public static BigDecimal getBalance() {
		return SpecDbNumFormat.bdToEightDecimal(accountBalance);
	}
	
	public static List<MarketStatusContent> getMarketStatusList(){
		return marketStatusList;
	}
	
	

	
}
