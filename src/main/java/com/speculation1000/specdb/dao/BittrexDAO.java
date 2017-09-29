package com.speculation1000.specdb.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DbUtils;
import com.speculation1000.specdb.dto.BittrexDTO;
import com.speculation1000.specdb.log.SpecDbLogger;
import com.speculation1000.specdb.market.AccountBalance;
import com.speculation1000.specdb.market.Market;

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
		specLogger.logp(Level.INFO, BittrexDAO.class.getName(),"restoreMarkets","Starting trex market restore");
		//select distinct dates
		List<Long> dateList = DbUtils.getDistinctDates(dbce, 25, "TREX");
		List<Market> marketList = new ArrayList<>();
		for(long i = dateList.get(1);i < dateList.get(dateList.size()-1);i+=86400){
			if(dateList.indexOf(i) < 0){
				//trex needs to get ALL
			}
		}		
		if(marketList.size()>0){
			specLogger.logp(Level.INFO, BittrexDAO.class.getName(),"restoreMarkets","Had some missing markets! Inserting missing markets.");
			DbUtils.insertMarkets(dbce, marketList);
		}else{
			specLogger.logp(Level.INFO, BittrexDAO.class.getName(),"restoreMarkets","No markets missing");
		}				
	}

	@Override
	public List<AccountBalance> getAccountBalance(DbConnectionEnum dbce) throws SpecDbException {
		List<AccountBalance> balanceList = new BittrexDTO().getAccountBalances();
		specLogger.logp(Level.INFO, BittrexDAO.class.getName(),"getAccountBalance","Got Trex Account Balance");
		return balanceList;
	}
}
