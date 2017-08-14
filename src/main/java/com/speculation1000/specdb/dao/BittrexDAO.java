package com.speculation1000.specdb.dao;

import java.util.List;
import java.util.logging.Level;

import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.db.DbUtils;
import com.speculation1000.specdb.db.InsertRecord;
import com.speculation1000.specdb.dto.BittrexDTO;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;

public class BittrexDAO implements MarketDAO {

	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	@Override
	public void updateMarkets() throws SpecDbException {
		try{
			List<Market> marketList = new BittrexDTO().getLatestMarketList();
			InsertRecord.insertBatchMarkets(marketList);
			specLogger.logp(Level.INFO, PoloniexDAO.class.getName(), "updateMarkets", "Trex markets updated!");
		}catch(Exception e){
			String message = SpecDbException.exceptionFormat(e.getStackTrace());
			throw new SpecDbException(message);
		}
	}

	@Override
	public void restoreMarkets() {
				
	}

    @Override
    public void cleanUpForNewDay() throws SpecDbException {
        try{
            DbUtils.nextDayCleanUp("TREX");
            specLogger.logp(Level.INFO, BittrexDAO.class.getName(), "cleanUpForNewDay", "Trex markets cleaned up.");
        }catch(Exception e){
            String message = SpecDbException.exceptionFormat(e.getStackTrace());
            throw new SpecDbException(message);
        }	
    }
}
