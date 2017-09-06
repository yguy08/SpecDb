package com.speculation1000.specdb.dao;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DeleteRecord;
import com.speculation1000.specdb.db.InsertRecord;
import com.speculation1000.specdb.db.QueryTable;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.market.MarketStatusContent;
import com.speculation1000.specdb.market.MarketUtils;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.time.SpecDbDate;

public class MarketDAO {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public MarketDAO(DbConnectionEnum dbce) throws SpecDbException {
		//update ticker to latest
		try{
			updateTickerList(dbce);
	    	specLogger.logp(Level.INFO, MarketDAO.class.getName(),"updateTickerList","Ticker updated successfully");
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, MarketDAO.class.getName(),"MarketDAO","Error updating ticker");
			throw new SpecDbException(e.getMessage());
		}
		
		//restore markets, if necessary
		try{
			restoreMarkets(dbce);
		}catch(SpecDbException e){
			specLogger.logp(Level.SEVERE, MarketDAO.class.getName(),"MarketDAO","Error restoring markets");
			throw new SpecDbException(e.getMessage());
		}		
	}
	
	public static void updateTickerList(DbConnectionEnum dbce) throws SpecDbException{
		try{
			List<Market> poloMarket = new PoloniexDAO().getLatestMarkets();
			List<Market> bittrexMarkets = new BittrexDAO().getLatestMarkets();
	    	long todayMidnight = SpecDbDate.getTodayMidnightEpochSeconds(Instant.now());
	    	DeleteRecord.deleteBulkMarkets(dbce, todayMidnight);
			InsertRecord.insertBatchMarkets(dbce, poloMarket);
			InsertRecord.insertBatchMarkets(dbce, bittrexMarkets);
		}catch(Exception e){
			throw new SpecDbException(e.getMessage());
		}
	}
	
	public static List<Market> getLastXDayList(DbConnectionEnum dbce, int days){
		Instant instant = Instant.now().minusSeconds(86400 * days);
		String sqlCommand = "SELECT * FROM Markets WHERE Date >= " + SpecDbDate.getTodayMidnightEpochSeconds(instant)
				+ " GROUP BY Base,Counter,Exchange,Date"
				+ " ORDER BY Counter,Base ASC, Date DESC";
		List<Market> marketList = QueryTable.genericMarketQuery(dbce, sqlCommand);
		Collections.sort(marketList);
		return marketList;
	}
	
	public static void restoreMarkets(DbConnectionEnum dbce) throws SpecDbException{
        new PoloniexDAO().restoreMarkets(dbce);
        //bittrex
	}
	
	public static List<Market> getDistinctMarkets(DbConnectionEnum dbce){
		String sqlCommand = "SELECT DISTINCT Base,Counter,Exchange FROM MARKETS order by counter,base ASC";
		List<Market> marketList = QueryTable.genericMarketQuery(dbce, sqlCommand);
		return marketList;		
	}
	
	public static List<MarketStatusContent> getMarketStatusList(DbConnectionEnum dbce){
		List<Market> distinctList = getDistinctMarkets(dbce);
		List<Market> marketList = getLastXDayList(dbce,150);
		Map<String,List<Market>> marketMap = new HashMap<>();
		for(Market m : distinctList){
			List<Market> tmpList = new ArrayList<>();
			marketMap.put(m.getSymbol(), tmpList);
			for(Market m2 : marketList){
				if(m.getSymbol().equalsIgnoreCase(m2.getSymbol())){
					tmpList.add(m2);
				}else if(tmpList.size()>0){
					break;
				}
			}
		}
		List<MarketStatusContent> marketStatusList = new ArrayList<>();
		for(Map.Entry<String, List<Market>> e : marketMap.entrySet()){
			MarketStatusContent ms = new MarketStatusContent(e.getKey(),e.getValue());
			marketStatusList.add(ms);
		}
		return marketStatusList;		
	}
	
	public static TreeMap<String,BigDecimal> getCurrentCloseMap(DbConnectionEnum dbce){
		TreeMap<String,BigDecimal> closeMap = new TreeMap<>();
		List<Market> marketList = getLastXDayList(dbce,0);
		for(Market m : marketList) {
			closeMap.put(m.getSymbol(), m.getClose());
		}
		return closeMap;
	}
	
	public static TreeMap<String,Boolean> isHighMap(DbConnectionEnum dbce,int days){
		List<Market> distinctList = getDistinctMarkets(dbce);
		List<Market> marketList = getLastXDayList(dbce,days);
		Map<String,List<BigDecimal>> marketMap = new HashMap<>();
		for(Market m : distinctList){
			List<BigDecimal> tmpList = new ArrayList<>();
			marketMap.put(m.getSymbol(), tmpList);
			for(Market m2 : marketList){
				if(m.getSymbol().equalsIgnoreCase(m2.getSymbol())){
					tmpList.add(m2.getClose());
				}else if(tmpList.size()>0){
					break;
				}
			}
		}
		TreeMap<String,Boolean> highMap = new TreeMap<>();
		for(Map.Entry<String, List<BigDecimal>> e : marketMap.entrySet()){
			boolean isHigh = MarketUtils.isXDayHigh(e.getValue(),days);
			highMap.put(e.getKey(), isHigh);
		}
		return highMap;
	}
	
	public static TreeMap<String,Boolean> isLowMap(DbConnectionEnum dbce, int days){
		List<Market> distinctList = getDistinctMarkets(dbce);
		List<Market> marketList = getLastXDayList(dbce,days);
		Map<String,List<BigDecimal>> marketMap = new HashMap<>();
		for(Market m : distinctList){
			List<BigDecimal> tmpList = new ArrayList<>();
			marketMap.put(m.getSymbol(), tmpList);
			for(Market m2 : marketList){
				if(m.getSymbol().equalsIgnoreCase(m2.getSymbol())){
					tmpList.add(m2.getClose());
				}else if(tmpList.size()>0){
					break;
				}
			}
		}
		TreeMap<String,Boolean> lowMap = new TreeMap<>();
		for(Map.Entry<String, List<BigDecimal>> e : marketMap.entrySet()){
			boolean isHigh = MarketUtils.isXDayLow(e.getValue(),days);
			lowMap.put(e.getKey(), isHigh);
		}
		return lowMap;
	}
	
	public static long getOldestRecordByExchange(DbConnectionEnum dbce,String exchange){
		String sqlCommand = "SELECT Min(Date) AS Date FROM markets WHERE exchange = " + "'"+exchange+"'";
		List<Market> marketList = QueryTable.genericMarketQuery(dbce, sqlCommand);
		return marketList.get(0).getDate();
	}

}
