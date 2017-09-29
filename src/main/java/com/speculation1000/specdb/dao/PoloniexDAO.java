package com.speculation1000.specdb.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.db.DbConnectionEnum;
import com.speculation1000.specdb.db.DbUtils;
import com.speculation1000.specdb.dto.PoloniexDTO;
import com.speculation1000.specdb.log.SpecDbLogger;
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
	public void restoreMarkets(DbConnectionEnum dbce) throws SpecDbException {
		specLogger.logp(Level.INFO, PoloniexDAO.class.getName(),"restoreMarkets","Starting polo market restore");
		//select distinct dates
		List<Long> dateList = DbUtils.getDistinctDates(dbce, 25, "POLO");
		List<Market> marketList = new ArrayList<>();
		for(long i = dateList.get(1);i < dateList.get(dateList.size()-1);i+=86400){
			if(dateList.indexOf(i) < 0){
				marketList.addAll(new PoloniexDTO().fetchExchangeHistory(i,i));
			}
		}		
		if(marketList.size()>0){
			specLogger.logp(Level.INFO, PoloniexDAO.class.getName(),"restoreMarkets","Had some missing markets! Inserting missing markets.");
			DbUtils.insertMarkets(dbce, marketList);
		}else{
			specLogger.logp(Level.INFO, PoloniexDAO.class.getName(),"restoreMarkets","No markets missing");
		}
	}

	@Override
	public List<AccountBalance> getAccountBalance(DbConnectionEnum dbce) throws SpecDbException {
		List<AccountBalance> balanceList = new PoloniexDTO().getAccountBalances();
		specLogger.logp(Level.INFO, PoloniexDAO.class.getName(),"getAccountBalance","AccountBalance");
		return balanceList;
	}

}
