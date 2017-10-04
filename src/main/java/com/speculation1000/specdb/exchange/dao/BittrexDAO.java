package com.speculation1000.specdb.exchange.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.start.StandardMode;
import com.speculation1000.specdb.utils.SpecDbDate;
import com.speculation1000.specdb.utils.SpecDbLogger;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DbUtils;
import com.speculation1000.specdb.dto.BittrexDTO;
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
	public int restoreMarkets(DbConnectionEnum dbce, int days) throws SpecDbException {
		int sum = 0;
		specLogger.logp(Level.INFO, BittrexDAO.class.getName(),"restoreMarkets","Starting trex market restore");
		
		//select distinct dates
		List<Long> dateList;
		List<Market> marketList;
		long nDaysAgo = SpecDbDate.getTodayMidnightEpochSeconds(StandardMode.getStartRunTS().minusSeconds(86400*days));
		List<Long> getList = new ArrayList<>();
		try {
			dateList = DbUtils.getDistinctDates(dbce, days, "TREX");
			for(long i = nDaysAgo;i < SpecDbDate.getTodayMidnightEpochSeconds(StandardMode.getStartRunTS());i+=86400){
				if(dateList.indexOf(i) < 0){
					getList.add(i);
				}
			}
		}catch (Exception e) {
			specLogger.logp(Level.INFO, BittrexDAO.class.getName(),"restoreMarkets",e.getMessage());
			throw new SpecDbException(e.getMessage());
		}
		
		if(getList.size()>0) {
			try {
				marketList = new BittrexDTO().getMissingHistory(getList);
				specLogger.logp(Level.INFO, BittrexDAO.class.getName(),"restoreMarkets","Had some missing markets! Inserting missing markets.");
				sum = DbUtils.insertMarkets(dbce, marketList);
			}catch(Exception e) {
				specLogger.logp(Level.INFO, BittrexDAO.class.getName(),"restoreMarkets",e.getMessage());
				throw new SpecDbException(e.getMessage());
			}
		}else {
			specLogger.logp(Level.INFO, BittrexDAO.class.getName(),"restoreMarkets","No markets missing");
		}
		
		return sum;
	}

	@Override
	public List<AccountBalance> getAccountBalance(DbConnectionEnum dbce) throws SpecDbException {
		List<AccountBalance> balanceList = new BittrexDTO().getAccountBalances();
		specLogger.logp(Level.INFO, BittrexDAO.class.getName(),"getAccountBalance","Got Trex Account Balance");
		return balanceList;
	}
}
