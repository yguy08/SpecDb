package com.speculation1000.specdb.start;

import java.util.List;
import java.util.logging.Level;

import com.speculation1000.specdb.dao.MarketSummaryDAO;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.MarketStatusContent;

public class MarketStatus {

	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	private static List<MarketStatusContent> marketStatusList;
	
	public static void updateMarketStatusList(DbConnectionEnum dbce){
		marketStatusList = MarketSummaryDAO.getMarketStatusList(dbce);
		specLogger.logp(Level.INFO, MarketStatus.class.getName(), "updateMarketStatusList", "Retrieved market status list!");
	}
	
	public static List<MarketStatusContent> getMarketStatusList(){
		return marketStatusList;
	}

	
}
