package com.speculation1000.specdb.start;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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
			System.out.println(msc.getSymbol());
			for(Map.Entry<Long,BigDecimal> e : msc.getClosePriceMap().entrySet()){
				System.out.print(e.getKey() + " " + e.getValue() + " ");
			}
			System.out.println("");
			for(Map.Entry<Long,Integer> e : msc.getDayHighLowMap().entrySet()){
				System.out.print(e.getKey() + " " + e.getValue() + " ");
			}
			System.out.println("");
			for(Map.Entry<Long,BigDecimal> e : msc.getAtrMap().entrySet()){
				System.out.print(e.getKey() + " " + e.getValue() + " ");
			}
			System.out.println("");
		}
	}

	
}
