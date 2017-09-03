package com.speculation1000.specdb.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;

import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.dto.BittrexDTO;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;

public class BittrexDAO implements MarketDAO {

	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	@Override
	public List<Market> getLatestMarkets() throws SpecDbException {
		try{
			List<Market> marketList = new BittrexDTO().getLatestMarketList();
			specLogger.logp(Level.INFO, BittrexDAO.class.getName(), "getLatestMarkets", "Got latest bittrex markets");
			return marketList;
		}catch(Exception e){
			throw new SpecDbException(e.getMessage());
		}
	}

	@Override
	public void restoreMarkets() {
				
	}

	@Override
	public BigDecimal getAccountBalance() throws SpecDbException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOpenTrades() throws SpecDbException {
		// TODO Auto-generated method stub
		return null;
	}
}
