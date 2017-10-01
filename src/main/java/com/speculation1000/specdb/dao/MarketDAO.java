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
import com.speculation1000.specdb.db.DbUtils;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.market.Symbol;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.utils.SpecDbDate;
import com.speculation1000.specdb.utils.SpecDbLogger;

public class MarketDAO {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public static Map<Symbol,BigDecimal> currentCloseMap = null;
	
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
			restoreMarkets(dbce, 25);
		}catch(SpecDbException e){
			specLogger.logp(Level.SEVERE, MarketDAO.class.getName(),"MarketDAO","Error restoring markets");
			throw new SpecDbException(e.getMessage());
		}
		
		try {
			updateCurrentCloseMap(dbce);
		}catch(Exception e) {
			
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
			DbUtils.marketCleanUp(dbce, todayMidnight);			
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, MarketDAO.class.getName(),"updateTickerList","Error cleaning up today's market prices");
			throw new SpecDbException(e.getMessage());
		}
		
		try{
			DbUtils.insertMarkets(dbce, poloMarket);
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, MarketDAO.class.getName(),"updateTickerList","Error inserting new polo markets");
			throw new SpecDbException(e.getMessage());
		}
		
		try{
			DbUtils.insertMarkets(dbce, bittrexMarkets);
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, MarketDAO.class.getName(),"updateTickerList","Error inserting new trex markets");
			throw new SpecDbException(e.getMessage());
		}
		
	}
	
	private void restoreMarkets(DbConnectionEnum dbce, int days) throws SpecDbException{
		//restore markets, if necessary
		try{
			new PoloniexDAO().restoreMarkets(dbce,days);
			new BittrexDAO().restoreMarkets(dbce,days);
		}catch(SpecDbException e){
			specLogger.logp(Level.SEVERE, MarketDAO.class.getName(),"restoreMarkets","Error restoring markets");
			throw new SpecDbException(e.getMessage());
		}
	}
	
	public static List<Market> getMarketList(DbConnectionEnum dbce, int days){
		Instant instant = SpecDbDate.getTodayMidnightInstant(Instant.now().minusSeconds(86400 * days));
		String sqlCommand = "SELECT * FROM Markets WHERE Date >= " + SpecDbDate.getTodayMidnightEpochSeconds(instant)
				+ " GROUP BY Base,Counter,Exchange,Date"
				+ " ORDER BY Counter,Base ASC, Date DESC";
		List<Market> marketList = DbUtils.genericMarketQuery(dbce, sqlCommand);
		Collections.sort(marketList);
		return marketList;
	}
	
	public static Map<Symbol, List<Market>> getMarketMap(DbConnectionEnum dbce, int days){
		List<Market> marketList = getMarketList(dbce,days);
		Map<Symbol, List<Market>> marketsBySymbol = marketList.stream().collect(Collectors.groupingBy(Market::getSymbol));
		return marketsBySymbol;
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
	
	private static void updateCurrentCloseMap(DbConnectionEnum dbce) {
		String sqlCommand = "SELECT Base,Counter,Exchange,Close FROM Markets WHERE Date = (SELECT Max(DATE) AS DATE FROM MARKETS)";
		List<Market> marketList = DbUtils.genericMarketQuery(dbce, sqlCommand);
		Map<Symbol,BigDecimal> closeMap = new HashMap<>();
		for(Market m : marketList) {
			closeMap.put(m.getSymbol(), m.getClose());
		}
		currentCloseMap = closeMap;
	}
	
	public static BigDecimal getCurrentPrice(Symbol symbol) {
		if(currentCloseMap!=null) {
			return currentCloseMap.get(symbol);
		}else {
			updateCurrentCloseMap(DbConnectionEnum.H2_MAIN);
		}		
		return currentCloseMap.get(symbol);
		
	}

}
