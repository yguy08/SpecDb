package com.speculation1000.specdb.dao;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.time.SpecDbDate;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.InsertRecord;
import com.speculation1000.specdb.db.QueryTable;
import com.speculation1000.specdb.dto.PoloniexDTO;
import com.speculation1000.specdb.exchange.ExchangeEnum;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.market.Symbol;

public class PoloniexDAO implements ExchangeDAO {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();

	@Override
	public List<Market> getLatestMarkets() throws SpecDbException {
		try{
			List<Market> marketList = new PoloniexDTO().getLatestMarketList();
			return marketList;
		}catch(Exception e){
			throw new SpecDbException(e.getMessage());
		}
	}

	@Override
	public void restoreMarkets(DbConnectionEnum dbce) throws SpecDbException {
		//Get oldest market date for poloniex
		long oldestDateInDb = getOldestRecordByExchange(dbce);
		if(oldestDateInDb > 1390003200){
			//Missing up to date so we don't overwrite existing data
			long missingUpToDate = oldestDateInDb - 86400;
			specLogger.logp(Level.INFO, PoloniexDAO.class.getName(), "restoreMarkets", "Polo Restore: Need to restore up to: " 
																						+ SpecDbDate.longToLogStringFormat(missingUpToDate));
			try{
				//Get poloniex chart data up to oldest date - 86400
				List<Market> marketList = new PoloniexDTO().fetchExchangeHistory(missingUpToDate);
				InsertRecord.insertBatchMarkets(dbce, marketList);
				specLogger.logp(Level.INFO, PoloniexDAO.class.getName(), "restoreMarkets", "Polo markets restored!");
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
				InsertRecord.insertBatchMarkets(dbce, marketList);
				specLogger.logp(Level.INFO, PoloniexDAO.class.getName(), "restoreMarkets", "Polo markets restored!");
			}catch(Exception e){
				specLogger.logp(Level.SEVERE, PoloniexDAO.class.getName(), "restoreMarkets", "Polo restore failed inserting markets!");
				throw new SpecDbException(e.getMessage());
			}
		}else{			
			specLogger.logp(Level.INFO, PoloniexDAO.class.getName(), "restoreMarkets", "Polo Restore: Nothing to restore!");			
		}
	}

	@Override
	public BigDecimal getAccountBalance(DbConnectionEnum dbce) throws SpecDbException {
		TreeMap<Symbol, BigDecimal> closeMap = MarketDAO.getCurrentCloseMap(dbce);
		BigDecimal accountBalance = new PoloniexDTO().getAccountBalance(closeMap);
		specLogger.logp(Level.INFO, PoloniexDAO.class.getName(),"getAccountBalance","AccountBalance");
		return accountBalance;
	}

	private static long getOldestRecordByExchange(DbConnectionEnum dbce){
		String exchange = ExchangeEnum.POLONIEX.getExchangeSymbol();
		String sqlCommand = "SELECT Min(Date) AS Date FROM markets WHERE exchange = " + "'"+exchange+"'";
		List<Market> marketList = QueryTable.genericMarketQuery(dbce, sqlCommand);
		return marketList.get(0).getDate();
	}

}
