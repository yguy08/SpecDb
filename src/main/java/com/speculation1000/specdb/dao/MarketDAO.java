package com.speculation1000.specdb.dao;

import java.math.BigDecimal;
import java.util.List;

import com.speculation1000.specdb.market.Market;
import com.speculation1000.specdb.start.SpecDbException;

public interface MarketDAO {
	
	List<Market> getLatestMarkets() throws SpecDbException;
		
	void restoreMarkets() throws SpecDbException;
	
	BigDecimal getAccountBalance() throws SpecDbException;
	
	String getOpenTrades() throws SpecDbException;

}
