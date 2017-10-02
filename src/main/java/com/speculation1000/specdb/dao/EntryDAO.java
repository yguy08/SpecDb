package com.speculation1000.specdb.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.stream.Collectors;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DbUtils;
import com.speculation1000.specdb.market.Entry;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.market.Symbol;
import com.speculation1000.specdb.market.TradeStatusEnum;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.start.StandardMode;
import com.speculation1000.specdb.utils.SpecDbDate;
import com.speculation1000.specdb.utils.SpecDbLogger;

public class EntryDAO {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();

	public EntryDAO(DbConnectionEnum dbce, int days) throws SpecDbException {
		//update entries
		try{
			updateEntries(dbce, days);
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, EntryDAO.class.getName(),"EntryDAO","Error updating entries");
			throw new SpecDbException(e.getMessage());
		}
	}
	
	private void updateEntries(DbConnectionEnum dbce, int days) throws SpecDbException {		
		List<Entry> entryList = new ArrayList<>();
		Entry entry;
		
		List<Integer> highList = new ArrayList<>();
		highList.add(55);
		highList.add(days);
		highList.add(11);
		
		for(Integer i : highList) {
			try{
				List<Symbol> symbolList = DbUtils.getMarketHighs(dbce, i).stream().map(Market::getSymbol).collect(Collectors.toList());
				TreeMap<Symbol,List<Market>> marketMap = MarketDAO.getSelectMarketMap(dbce, 100, symbolList);
				for(Map.Entry<Symbol, List<Market>> e : marketMap.entrySet()){
					entry = new Entry(e.getKey(), e.getValue(),TradeStatusEnum.LONG,i);
					if(entry.passFilter()){
						entryList.add(entry);
					}
				}
				specLogger.logp(Level.INFO, EntryDAO.class.getName(),"updateEntries","Market highs: " + i);
			}catch(Exception e){
				specLogger.logp(Level.SEVERE, EntryDAO.class.getName(),"updateEntries","Error getting market highs");
				throw new SpecDbException(e.getMessage());
			}
		}
		
		List<Integer> lowList = new ArrayList<>();
		lowList.add(-55);
		lowList.add(-days);
		lowList.add(-11);
		
		for(Integer i : highList) {			
			try{
				List<Symbol> symbolList = DbUtils.getMarketLows(dbce, i).stream().map(Market::getSymbol).collect(Collectors.toList());
				TreeMap<Symbol,List<Market>> marketMap = MarketDAO.getSelectMarketMap(dbce, 100, symbolList);
				for(Map.Entry<Symbol, List<Market>> e : marketMap.entrySet()){
					entry = new Entry(e.getKey(),e.getValue(),TradeStatusEnum.SHORT,i);
					if(entry.passFilter()){
						entryList.add(entry);
					}
				}
				specLogger.logp(Level.INFO, EntryDAO.class.getName(),"updateEntries","Market lows: " + i);
			}catch(Exception e){
				specLogger.logp(Level.SEVERE, EntryDAO.class.getName(),"updateEntries","Error getting market lows" + i);
				throw new SpecDbException(e.getMessage());
			}
		}
		
		long cleanUpTime = SpecDbDate.getLastSixHourSeconds(StandardMode.getStartRunTS());
		
		try{
			DbUtils.newEntriesCleanUp(dbce,cleanUpTime);
			specLogger.logp(Level.INFO, EntryDAO.class.getName(),"updateEntries","Cleaned up new entries");
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, EntryDAO.class.getName(),"updateEntries","Error cleaning up new entries");
			throw new SpecDbException(e.getMessage());
		}
		
		try{
			DbUtils.insertNewEntries(dbce, entryList);
			specLogger.logp(Level.INFO, EntryDAO.class.getName(),"updateEntries","Inserted market h/l");
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, EntryDAO.class.getName(),"updateEntries","Error inserting market h/l");
			throw new SpecDbException(e.getMessage());
		}
		
	}

	public static List<Entry> getMarketEntryList(DbConnectionEnum dbce,int days){
		List<Entry> entryList = DbUtils.getNewEntries(dbce,days);
		//Collections.sort(entryList);
		return entryList;
	}

}
