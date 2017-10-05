package com.speculation1000.specdb.dao;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
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
import com.speculation1000.specdb.utils.SpecDbDate;
import com.speculation1000.specdb.utils.SpecDbLogger;

public class MarketDAO {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public static Map<Symbol,BigDecimal> currentCloseMap = null;
	
	public static List<Market> getMarketList(DbConnectionEnum dbce, int days){
		specLogger.logp(Level.INFO, "MarketDAO", "getMarketList", "Getting market list..");
		Instant instant = SpecDbDate.getTodayMidnightInstant(Instant.now().minusSeconds(86400 * days));
		String sqlCommand = "SELECT SYMBOL,DATE,HIGH,LOW,CLOSE,VOLUME,H_L"
							+ " FROM Markets WHERE Date >= " 
							+ SpecDbDate.getTodayMidnightEpochSeconds(instant)
							+ " GROUP BY SYMBOL,DATE"
							+ " ORDER BY SYMBOL ASC, DATE DESC";
		List<Market> markets = DbUtils.genericMarketQuery(dbce, sqlCommand);
		Map<Symbol, List<Market>> marketsBySymbol = markets.stream().collect(Collectors.groupingBy(Market::getSymbol));
		markets = new ArrayList<>();
		for(Map.Entry<Symbol, List<Market>> e : marketsBySymbol.entrySet()){
			Market m = e.getValue().get(0);
			m.setHistorical(e.getValue());
			markets.add(m);
		}
		return markets;
	}
	
	/*
	 * 
	 * String base, String counter, String exchange, long date, BigDecimal close,
												BigDecimal high, BigDecimal low, int volume
	 */
	
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
