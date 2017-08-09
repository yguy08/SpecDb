package com.speculation1000.specdb.dao;

import java.util.List;

import com.speculation1000.specdb.start.SpecDbException;
import com.speculation1000.specdb.db.DbUtils;
import com.speculation1000.specdb.dto.BittrexDTO;
import com.speculation1000.specdb.market.Market;

public class BittrexDAO implements MarketDAO {

	@Override
	public void updateMarkets() throws SpecDbException {
		try{
			List<Market> marketList = new BittrexDTO().getLatestMarketList();
			DbUtils.insertBatchMarkets(marketList);
		}catch(Exception e){
			String message = SpecDbException.exceptionFormat(e.getStackTrace());
			throw new SpecDbException(message);
		}
	}

	@Override
	public void restoreMarkets() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanUpForNewDay() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getMarketDbStatus() {
		// TODO Auto-generated method stub
		return null;
	}

}
