package com.speculation1000.specdb.dao;

import java.sql.Connection;
import java.time.Instant;
import java.util.List;
import java.util.logging.Level;

import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.time.SpecDbDate;
import com.speculation1000.specdb.db.DbConnection;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DeleteRecord;
import com.speculation1000.specdb.db.InsertRecord;
import com.speculation1000.specdb.db.UpdateRecord;
import com.speculation1000.specdb.dto.PoloniexDTO;
import com.speculation1000.specdb.exchange.ExchangeEnum;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;

public class PoloniexDAO implements MarketDAO {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();

	@Override
	public void updateMarkets() throws SpecDbException {
		Connection conn = DbConnection.connect(DbConnectionEnum.H2_MAIN);
		try{
			List<Market> marketList = new PoloniexDTO().getLatestMarketList();
			InsertRecord.insertBatchMarkets(conn, marketList);
			conn.close();
			specLogger.logp(Level.INFO, PoloniexDAO.class.getName(), "updateMarkets", "Polo markets updated!");
		}catch(Exception e){
			throw new SpecDbException(e.getMessage());
		}
	}

	@Override
	public int cleanUpForToday() throws SpecDbException {
		DbConnectionEnum dbce = DbConnectionEnum.H2_MAIN;
		try{
			List<Market> marketList = MarketSummaryDAO.getTodaysMarketCleanUpList(dbce);
			int[] recordsDeleted = DeleteRecord.deleteBulkMarkets(dbce, marketList);
			specLogger.logp(Level.INFO, PoloniexDAO.class.getName(), "cleanUpForNewDay", "Polo markets cleaned up.");
			int sum = 0;
			for(Integer i : recordsDeleted){
				sum+=i;
			}
			return sum;
		}catch(Exception e){
			throw new SpecDbException(e.getMessage());
		}
	}

	@Override
	public int cleanUpForNewDay() throws SpecDbException {
		DbConnectionEnum dbce = DbConnectionEnum.H2_MAIN;
		try{
			List<Market> marketList = MarketSummaryDAO.getTodaysMarketCleanUpList(dbce);
			int[] recordsUpdated = UpdateRecord.updateBulkMarkets(dbce, marketList);
			specLogger.logp(Level.INFO, PoloniexDAO.class.getName(), "cleanUpForNewDay", "Polo markets cleaned up.");
			int sum = 0;
			for(Integer i : recordsUpdated){
				sum+=i;
			}
			return sum;
		}catch(Exception e){
			throw new SpecDbException(e.getMessage());
		}
	}

	@Override
	public void restoreMarkets() throws SpecDbException {
		Connection connection = DbConnection.connect(DbConnectionEnum.H2_MAIN);
		
		//Get oldest market date for poloniex
		long oldestDateInDb = MarketSummaryDAO.getOldestRecordByExchange(connection, ExchangeEnum.POLONIEX.getExchangeSymbol());
		
		if(oldestDateInDb > 1390003200){
			
			//Missing up to date so we don't overwrite existing data
			long missingUpToDate = oldestDateInDb - 86400;
			specLogger.logp(Level.INFO, PoloniexDAO.class.getName(), "restoreMarkets", "Polo Restore: Need to restore up to: " 
																						+ SpecDbDate.longToLogStringFormat(missingUpToDate));
			try{
				
				//Get poloniex chart data up to oldest date - 86400
				List<Market> marketList = new PoloniexDTO().fetchExchangeHistory(missingUpToDate);
				InsertRecord.insertBatchMarkets(connection, marketList);
				specLogger.logp(Level.INFO, PoloniexDAO.class.getName(), "restoreMarkets", "Polo markets restored!");
				connection.close();
				
			}catch(Exception e){
				specLogger.logp(Level.SEVERE, PoloniexDAO.class.getName(), "restoreMarkets", "Polo restore failed inserting markets!");
				throw new SpecDbException(e.getMessage());
			}
		}else if(oldestDateInDb == 0){
			
			long currentDay = SpecDbDate.getTodayMidnightEpochSeconds(Instant.now());
			specLogger.logp(Level.INFO, PoloniexDAO.class.getName(), "restoreMarkets", "Polo Restore: Need a full restore up to today: " 
																						+ SpecDbDate.longToLogStringFormat(currentDay) );
			try{
				
				//Get poloniex chart data up to oldest date - 86400
				List<Market> marketList = new PoloniexDTO().fetchExchangeHistory(currentDay);
				InsertRecord.insertBatchMarkets(connection, marketList);
				specLogger.logp(Level.INFO, PoloniexDAO.class.getName(), "restoreMarkets", "Polo markets restored!");
				connection.close();
				
			}catch(Exception e){
				specLogger.logp(Level.SEVERE, PoloniexDAO.class.getName(), "restoreMarkets", "Polo restore failed inserting markets!");
				throw new SpecDbException(e.getMessage());
			}
		}else{			
			specLogger.logp(Level.INFO, PoloniexDAO.class.getName(), "restoreMarkets", "Polo Restore: Nothing to restore!");			
		}
	}

}
