package com.speculation1000.specdb.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;

import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.dto.BittrexDTO;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.market.MarketEntry;

public class BittrexDAO implements ExchangeDAO {

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
	public void restoreMarkets(DbConnectionEnum dbce) throws SpecDbException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BigDecimal getAccountBalance(DbConnectionEnum dbce) throws SpecDbException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<MarketEntry> getEntries(DbConnectionEnum dbce, int days) throws SpecDbException {
		// TODO Auto-generated method stub
		return null;
	}
}
