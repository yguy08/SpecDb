package com.speculation1000.specdb.dao;

import java.util.List;

import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.start.SpecDbException;

public interface MarketDAO {
	
	List<Market> getLatestMarkets() throws SpecDbException;
		
	void restoreMarkets() throws SpecDbException;

}
