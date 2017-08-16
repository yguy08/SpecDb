package com.speculation1000.specdb.dao;

import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;

import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.db.DbConnection;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DbUtils;
import com.speculation1000.specdb.db.InsertRecord;
import com.speculation1000.specdb.dto.BittrexDTO;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;

public class BittrexDAO implements MarketDAO {

	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	@Override
	public void updateMarkets() throws SpecDbException {
		Connection conn = DbConnection.connect(DbConnectionEnum.H2_MAIN);
		try{
			List<Market> marketList = new BittrexDTO().getLatestMarketList();
			InsertRecord.insertBatchMarkets(marketList);
			conn.close();
			specLogger.logp(Level.INFO, PoloniexDAO.class.getName(), "updateMarkets", "Trex markets updated!");
		}catch(Exception e){
			throw new SpecDbException(e.getMessage());
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
            throw new SpecDbException(e.getMessage());
        }	
    }
}
