package com.speculation1000.specdb.dao;

import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;

import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.db.DbConnection;
import com.speculation1000.specdb.db.DbUtils;
import com.speculation1000.specdb.db.InsertRecord;
import com.speculation1000.specdb.dto.PoloniexDTO;
import com.speculation1000.specdb.exchange.ExchangeEnum;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;

public class PoloniexDAO implements MarketDAO {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();

	@Override
	public void updateMarkets() throws SpecDbException {
		try{
			List<Market> marketList = new PoloniexDTO().getLatestMarketList();
			InsertRecord.insertBatchMarkets(marketList);
			specLogger.logp(Level.INFO, PoloniexDAO.class.getName(), "updateMarkets", "Polo markets updated!");
		}catch(Exception e){
			String message = SpecDbException.exceptionFormat(e.getStackTrace());
			throw new SpecDbException(message);
		}
	}

	@Override
	public void restoreMarkets() throws SpecDbException {
		Connection connection = DbConnection.mainConnect();
		
		try{
			//Get oldest market date for poloniex
			long oldestDateInDb = MarketSummaryDAO.getOldestRecordByExchange(connection, ExchangeEnum.POLONIEX.getExchangeSymbol());
			//Missing up to date so we don't overwrite existing data
			long missingUpToDate = oldestDateInDb - 86400;
			if(missingUpToDate < 0){
				missingUpToDate = 9999999999L;
			}
			//Get poloniex chart data up to oldest date - 86400
			List<Market> marketList = new PoloniexDTO().fetchExchangeHistory(missingUpToDate);
			long fromPoloDate = marketList.get(marketList.size()-1).getDate();
			if(fromPoloDate != 0){
				InsertRecord.insertBatchMarkets(connection, marketList);
				specLogger.logp(Level.INFO, PoloniexDAO.class.getName(), "restoreMarkets", "Polo markets restored!");
			}else{
				specLogger.logp(Level.INFO, PoloniexDAO.class.getName(), "restoreMarkets", "Nothing to restore!");
			}
			connection.close();
		}catch(Exception e){
			String message = SpecDbException.exceptionFormat(e.getStackTrace());
			throw new SpecDbException(message);
		}
		
	}

	@Override
	public void cleanUpForNewDay() throws SpecDbException {
		try{
			DbUtils.nextDayCleanUp("POLO");
			specLogger.logp(Level.INFO, PoloniexDAO.class.getName(), "cleanUpForNewDay", "Polo markets cleaned up.");
		}catch(Exception e){
			String message = SpecDbException.exceptionFormat(e.getStackTrace());
			throw new SpecDbException(message);
		}
	}

}
