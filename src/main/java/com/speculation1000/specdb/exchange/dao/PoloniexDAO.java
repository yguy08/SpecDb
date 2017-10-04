package com.speculation1000.specdb.exchange.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.utils.SpecDbLogger;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DbUtils;
import com.speculation1000.specdb.dto.PoloniexDTO;
import com.speculation1000.specdb.market.AccountBalance;
import com.speculation1000.specdb.market.Market;

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
	public int restoreMarkets(DbConnectionEnum dbce, int days) throws SpecDbException {
		int sum = 0;
		specLogger.logp(Level.INFO, PoloniexDAO.class.getName(),"restoreMarkets","Starting polo market restore");
		//select distinct dates
		List<Long> dateList = DbUtils.getDistinctDates(dbce, days, "POLO");
		List<Market> marketList = new ArrayList<>();
		for(long i = dateList.get(1);i < dateList.get(dateList.size()-1);i+=86400){
			if(dateList.indexOf(i) < 0){
				marketList.addAll(new PoloniexDTO().fetchExchangeHistory(i,i));
			}
		}		
		if(marketList.size()>0){
			specLogger.logp(Level.INFO, PoloniexDAO.class.getName(),"restoreMarkets","Had some missing markets! Inserting missing markets.");
			sum = DbUtils.insertMarkets(dbce, marketList);
		}else{
			specLogger.logp(Level.INFO, PoloniexDAO.class.getName(),"restoreMarkets","No markets missing");
		}
		
		return sum;
	}

	@Override
	public List<AccountBalance> getAccountBalance(DbConnectionEnum dbce) throws SpecDbException {
		List<AccountBalance> balanceList = new PoloniexDTO().getAccountBalances();
		specLogger.logp(Level.INFO, PoloniexDAO.class.getName(),"getAccountBalance","AccountBalance");
		return balanceList;
	}

}
