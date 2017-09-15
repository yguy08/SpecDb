package com.speculation1000.specdb.dao;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DeleteRecord;
import com.speculation1000.specdb.db.InsertRecord;
import com.speculation1000.specdb.db.QueryTable;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.market.Symbol;
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
	
	private void updateTickerList(DbConnectionEnum dbce) throws SpecDbException{
		List<Market> poloMarket;
		List<Market> bittrexMarkets;
		long todayMidnight = SpecDbDate.getTodayMidnightEpochSeconds(Instant.now());
		
		try{
			poloMarket = new PoloniexDAO().getLatestMarkets();
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, MarketDAO.class.getName(),"updateTickerList","Error getting latest Polo markets");
			throw new SpecDbException(e.getMessage());
		}
		
		try{
			bittrexMarkets = new BittrexDAO().getLatestMarkets();
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, MarketDAO.class.getName(),"updateTickerList","Error getting latest Trex markets");
			throw new SpecDbException(e.getMessage());
		}
		
		try{
			DeleteRecord.deleteBulkMarkets(dbce, todayMidnight);			
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, MarketDAO.class.getName(),"updateTickerList","Error cleaning up today's market prices");
			throw new SpecDbException(e.getMessage());
		}
		
		try{
			InsertRecord.insertBatchMarkets(dbce, poloMarket);
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, MarketDAO.class.getName(),"updateTickerList","Error inserting new polo markets");
			throw new SpecDbException(e.getMessage());
		}
		
		try{
			InsertRecord.insertBatchMarkets(dbce, bittrexMarkets);
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, MarketDAO.class.getName(),"updateTickerList","Error inserting new trex markets");
			throw new SpecDbException(e.getMessage());
		}
	}
	
	private void restoreMarkets(DbConnectionEnum dbce) throws SpecDbException{
        new PoloniexDAO().restoreMarkets(dbce);
	}
	
	public static List<Market> getMarketList(DbConnectionEnum dbce, int days){
		Instant instant = SpecDbDate.getTodayMidnightInstant(Instant.now().minusSeconds(86400 * days));
		String sqlCommand = "SELECT * FROM Markets WHERE Date >= " + SpecDbDate.getTodayMidnightEpochSeconds(instant)
				+ " GROUP BY Base,Counter,Exchange,Date"
				+ " ORDER BY Counter,Base ASC, Date DESC";
		List<Market> marketList = QueryTable.genericMarketQuery(dbce, sqlCommand);
		Collections.sort(marketList);
		return marketList;
	}
	
	public static List<Market> getMarketList(DbConnectionEnum dbce, int days, Symbol symbol){
		Instant instant = SpecDbDate.getTodayMidnightInstant(Instant.now().minusSeconds(86400 * days));
		String sqlCommand = "SELECT * FROM Markets WHERE Date >= "
				+ SpecDbDate.getTodayMidnightEpochSeconds(instant)
				+ " AND Concat(Base,Counter,':',Exchange) = '" + symbol + "' ORDER BY Date DESC";
		List<Market> marketList = QueryTable.genericMarketQuery(dbce, sqlCommand);
		return marketList;
	}
	
	public static Map<Symbol, List<Market>> getMarketMap(DbConnectionEnum dbce, int days){
		List<Market> marketList = getMarketList(dbce,days);
		Map<Symbol, List<Market>> marketsBySymbol = marketList.stream().collect(Collectors.groupingBy(Market::getSymbol));
		return marketsBySymbol;
	}
	
	public static Map<Symbol, List<BigDecimal>> getCloseMap(DbConnectionEnum dbce, int days){
		Map<Symbol, List<Market>> marketsBySymbol = getMarketMap(dbce, days);
		Map<Symbol,List<BigDecimal>> marketMap = new HashMap<>();
		for(Map.Entry<Symbol, List<Market>> e : marketsBySymbol.entrySet()){
			marketMap.put(e.getKey(), e.getValue().stream().map(Market::getClose).collect(Collectors.toList()));
		}
		return marketMap;
	}
	
	public static TreeMap<Symbol,BigDecimal> getCurrentCloseMap(DbConnectionEnum dbce){
		TreeMap<Symbol,BigDecimal> closeMap = new TreeMap<>();
		List<Market> marketList = getMarketList(dbce,0);
		for(Market m : marketList) {
			closeMap.put(new Symbol(m), m.getClose());
		}
		return closeMap;
	}
	
	public static TreeMap<Symbol,List<Market>> getSelectMarketMap(DbConnectionEnum dbce, int days,List<Symbol> symbolList){
		Map<Symbol, List<Market>> marketsBySymbol = getMarketMap(dbce, days);
		TreeMap<Symbol,List<Market>> marketMap = new TreeMap<>();
		for(Map.Entry<Symbol, List<Market>> e : marketsBySymbol.entrySet()){
			if(symbolList.contains(e.getKey())){
				marketMap.put(e.getKey(), e.getValue());				
			}
		}
		return marketMap;
	}

}
