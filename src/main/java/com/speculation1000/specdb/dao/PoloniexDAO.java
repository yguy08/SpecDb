package com.speculation1000.specdb.dao;

import java.util.List;

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
	public void cleanUpForNewDay() {
		
	}

	@Override
	public String getMarketDbStatus() {
		return null;
	}

}
