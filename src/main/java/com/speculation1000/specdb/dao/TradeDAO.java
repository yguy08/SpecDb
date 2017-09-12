package com.speculation1000.specdb.dao;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DeleteRecord;
import com.speculation1000.specdb.db.InsertRecord;
import com.speculation1000.specdb.db.QueryTable;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.MarketEntry;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.time.SpecDbDate;

public class TradeDAO {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public TradeDAO(DbConnectionEnum dbce, int days) throws SpecDbException {
		//update entries
		try{
			updateEntries(dbce, days);
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, TradeDAO.class.getName(),"TradeDAO","Error updating entries");
			throw new SpecDbException(e.getMessage());
		}
	}
	
	private void updateEntries(DbConnectionEnum dbce, int days) throws SpecDbException {
		List<MarketEntry> poloMarketEntries = new ArrayList<>();
		long todayMidnight = SpecDbDate.getTodayMidnightEpochSeconds(Instant.now());
		
		try{
			poloMarketEntries = new PoloniexDAO().getEntries(dbce, days);
		}catch(Exception e){
			
		}
		
		//ADD: bittrex		
		try{
			DeleteRecord.deleteEntries(dbce, todayMidnight);			
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, MarketDAO.class.getName(),"updateEntries","Error cleaning up today's entries");
			throw new SpecDbException(e.getMessage());
		}
		
		try{
			InsertRecord.insertBatchEntries(dbce, poloMarketEntries);
		}catch(Exception e){
			specLogger.logp(Level.SEVERE, MarketDAO.class.getName(),"updateEntries","Error inserting new polo entries");
			throw new SpecDbException(e.getMessage());
		}
		
	}
	
	public static List<MarketEntry> getMarketEntryList(DbConnectionEnum dbce,int days){
		Instant instant = SpecDbDate.getTodayMidnightInstant(Instant.now().minusSeconds(86400 * days));
		String sqlCommand = "SELECT * FROM entry WHERE Date >= " + SpecDbDate.getTodayMidnightEpochSeconds(instant)
				+ " GROUP BY Symbol,Date"
				+ " ORDER BY Symbol ASC, Date DESC";
		List<MarketEntry> marketEntryList = QueryTable.genericEntryQuery(dbce, sqlCommand);
		return marketEntryList;
	}
	
	

}
