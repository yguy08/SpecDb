package com.speculation1000.specdb.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.stream.Collectors;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DbUtils;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Entry;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.market.Symbol;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.trade.TradeStatusEnum;

public class EntryDAO {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();

	public EntryDAO(DbConnectionEnum dbce, int days) throws SpecDbException {
		//update entries
		try{
			updateEntries(dbce, days);
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, TradeDAO.class.getName(),"TradeDAO","Error updating entries");
			throw new SpecDbException(e.getMessage());
		}
	}
	
	private void updateEntries(DbConnectionEnum dbce, int days) throws SpecDbException {
		BigDecimal accountBalance = AccountDAO.getCurrentAccountBalance(dbce);
		
		List<Entry> entryList = new ArrayList<>();
		Entry entry;
		try{
			List<Symbol> symbolList = DbUtils.getMarketHighs(dbce, days).stream().map(Market::getSymbol).collect(Collectors.toList());
			TreeMap<Symbol,List<Market>> marketMap = MarketDAO.getSelectMarketMap(dbce, 150, symbolList);
			for(Map.Entry<Symbol, List<Market>> e : marketMap.entrySet()){
				entry = new Entry(e.getKey(), e.getValue(),accountBalance,TradeStatusEnum.LONG);
				if(entry.passFilter()){
					entryList.add(entry);
				}
			}
			specLogger.logp(Level.INFO, TradeDAO.class.getName(),"TradeDAO","Found new trades (long)");
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, TradeDAO.class.getName(),"updateTrades","Error getting market highs");
		}
		
		try{
			List<Symbol> symbolList = DbUtils.getMarketLows(dbce, days).stream().map(Market::getSymbol).collect(Collectors.toList());
			TreeMap<Symbol,List<Market>> marketMap = MarketDAO.getSelectMarketMap(dbce, 150, symbolList);
			for(Map.Entry<Symbol, List<Market>> e : marketMap.entrySet()){
				entry = new Entry(e.getKey(), e.getValue(),accountBalance,TradeStatusEnum.SHORT);
				if(entry.passFilter()){
					entryList.add(entry);
				}
			}
			specLogger.logp(Level.INFO, TradeDAO.class.getName(),"TradeDAO","Found new trades (short)");
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, TradeDAO.class.getName(),"updateTrades","Error getting market lows");
		}
		
		try{
			DbUtils.insertNewEntries(dbce, entryList);
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, MarketDAO.class.getName(),"updateEntries","Error inserting new polo entries");
			throw new SpecDbException(e.getMessage());
		}
		
	}

	public static List<Entry> getMarketEntryList(DbConnectionEnum dbce){
		List<Entry> entryList = DbUtils.getNewEntries(dbce);
		Collections.sort(entryList);
		return entryList;
	}
	
	//COMPARATOR VOLUME?

}
