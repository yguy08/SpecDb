package com.speculation1000.specdb.dao;

import java.util.List;
import java.util.logging.Level;

import com.speculation1000.specdb.SpecDbException;
import com.speculation1000.specdb.StartRun;
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
		int deleted = 0;
		try{
			//delete all but last date in yesterdays markets
			deleted = DbUtils.nextDayCleanUp(StartRun.getStartRunTS(), "POLO");
		}catch(SpecDbException e){
			specLogger.logp(Level.SEVERE, PoloniexDAO.class.getName(), "cleanUpForNewDay", e.getMessage());
		}
		
		String msg = "Cleaning up for new day...\n " + deleted + " " + "records deleted...";
		specLogger.logp(Level.INFO, DbUtils.class.getName(), "cleanUpForNewDay", msg);
		
		//update oldest to..
	}

	@Override
	public String getMarketDbStatus() {
		return null;
	} 

}
