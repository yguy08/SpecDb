package com.speculation1000.specdb.dao;

import java.util.List;
import java.util.logging.Level;

import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.db.DbUtils;
import com.speculation1000.specdb.dto.PoloniexDTO;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;

public class PoloniexDAO implements MarketDAO {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();

	@Override
	public void updateMarkets() throws SpecDbException {
		try{
			List<Market> marketList = new PoloniexDTO().getLatestMarketList();
			DbUtils.insertBatchMarkets(marketList);
			specLogger.logp(Level.INFO, PoloniexDAO.class.getName(), "updateMarkets", "Polo markets updated!");
		}catch(Exception e){
			String message = SpecDbException.exceptionFormat(e.getStackTrace());
			throw new SpecDbException(message);
		}
	}

	@Override
	public void restoreMarkets() {
		List<Market> marketList = new PoloniexDTO().fetchEntireExchangeHistory();
		DbUtils.insertBatchMarkets(marketList);
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

	@Override
	public String getMarketDbStatus() {
		return null;
	}

}
