package com.speculation1000.specdb.dao;

import com.speculation1000.specdb.start.SpecDbException;

public interface MarketDAO {
	
	void updateMarkets() throws SpecDbException;
	
	void cleanUpForNewDay() throws SpecDbException;
		
	void restoreMarkets() throws SpecDbException;
	
	String getMarketDbStatus() throws SpecDbException;

}
