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
	
	public static void updateMarketStatusList(){
		marketStatusList = MarketSummaryDAO.getMarketStatusList(DbConnectionEnum.H2_MAIN);
		specLogger.logp(Level.INFO, MarketStatus.class.getName(), "updateMarketStatusList", "Retrieved market status list!");
	}
	
	public static List<MarketStatusContent> getMarketStatusList(){
		return marketStatusList;
	}
	
	public static void main(String[]args){
		updateMarketStatusList();
		for(MarketStatusContent msc : marketStatusList){
			System.out.println("***");
			System.out.println(msc.getSymbol());
			System.out.println(msc.getClosePriceMap());
			System.out.println(msc.getDayHighLowMap());
			System.out.println(msc.getAtrMap());
		}
	}

	
}
